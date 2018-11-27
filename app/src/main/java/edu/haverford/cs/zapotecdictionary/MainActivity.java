package edu.haverford.cs.zapotecdictionary;


import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.gson.Gson;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


public class MainActivity  extends FragmentActivity {

    //private ViewPager viewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        String url = "http://talkingdictionary.swarthmore.edu/dl/retrieve.php";

        DownloadData downloadData = new DownloadData(getApplicationContext(), url);
        downloadData.execute();

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#91aaa7")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#cedbda")));

        Drawable[] tabIcons = new Drawable[3];
        tabIcons[0] = getResources().getDrawable(R.drawable.lookup);
        tabIcons[1] = getResources().getDrawable(R.drawable.history);
        tabIcons[2] = getResources().getDrawable(R.drawable.settings);

        ActionBar.Tab tab1 = actionBar.newTab();
        tab1.setIcon(getResources().getDrawable(R.drawable.lookup));
        tab1.setTabListener(new FragmentTabListener<SearchFragment>(this, "Search", SearchFragment.class));
        actionBar.addTab(tab1);

        ActionBar.Tab tab2 = actionBar.newTab();
        tab2.setIcon(getResources().getDrawable(R.drawable.history));
        tab2.setTabListener(new FragmentTabListener<HistoryFragment>(this, "History", HistoryFragment.class));
        actionBar.addTab(tab2);

        ActionBar.Tab tab3 = actionBar.newTab();
        tab3.setIcon(getResources().getDrawable(R.drawable.settings));
        tab3.setTabListener(new FragmentTabListener<SettingsFragment>(this, "Settings", SettingsFragment.class));
        actionBar.addTab(tab3);

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        Fragment wordDay = new WordOfDayFragment();
        transaction.add(android.R.id.content, wordDay, "WordOfDay");
        transaction.commit();

    }

}

class DownloadData extends AsyncTask<String, Void, Void> {
    public ZapotecDictionaryDBHelper db;
    private static HttpURLConnection con;
    private String urlStr;
    // unzipfile name
    private String dictionaryfn = "/tlacochahuaya_content/tlacochahuaya_export.json";

    public DownloadData(Context context, String url) {
        db = new ZapotecDictionaryDBHelper(context);
        this.urlStr = url;
        con = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void fetchData(String urlParameters) {
        //String urlParameters = "dict=tlacochahuaya&export=TRUE&dl_type=1";
        byte[] data = urlParameters.getBytes(Charset.defaultCharset());
        byte[] buffer = new byte[1024];
        int dataSize = 0;

        InputStream in = null;
        FileOutputStream out = null;

        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            // provide parameters required for POST request
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(data);
            } catch (Exception e) {
                Log.e("bg0", "Error writing post msg");
                e.printStackTrace();
            }

            in = con.getInputStream();
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, "update.zip");
            out = new FileOutputStream(file, true);
            dataSize = in.read(buffer);
            while(dataSize > 0) {
                out.write(buffer, 0, dataSize);
                dataSize = in.read(buffer);
            }
            out.flush();
            String fp = path.getPath()+"/dataFolder";
            unzip_file(file.getPath(), fp);
            //test
            Log.e("output file ", "==========================" + path.getPath()+"/dataFolder");
            //test
            writeDB(fp + dictionaryfn);
        } catch (Exception e) {
            Log.e("bg1", "Error making connection");
            e.printStackTrace();
        } finally {
            if(con != null) {
                con.disconnect();
            }
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void unzip_file(String zip_source, String destination) {
        try {
            ZipFile zipFile = new ZipFile(zip_source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public void writeDB(String fp) {
        StringBuilder sb = new StringBuilder();
        File file = new File(fp);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text;
            while((text = reader.readLine()) != null) {
                sb.append(text+"\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        Gson gson = new Gson();
        JsonObject[] objArr = gson.fromJson(sb.toString(), JsonObject[].class);
        //test
        if(objArr.length > 0) {
            Log.e("test", "+++++++++++++++++++++" + objArr[0].oid);
        }
        //test
        for(JsonObject j : objArr) {
            db.insertNewWord(j.oid, j.lang, j.ipa, j.gloss, j.pos, j.usageExample,
                    j.dialect, j.metaData, j.authority, j.audio, j.image, j.semantic_ids, j.czi, j.esGloss);
        }
        //test
        Log.e("dbtest", "_+_+_+_+_+_+_+_+_+_+_+_+_+" + db.getEntry(Integer.parseInt(objArr[0].oid   )));
        //test
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... strings){
        fetchData("dict=tlacochahuaya&export=TRUE&dl_type=1");
        return null;
    }

    class JsonObject {
        protected String oid;
        protected String lang;
        protected String ipa;
        protected String gloss;
        protected String pos;
        protected String usageExample;
        protected String dialect;
        protected String metaData;
        protected String authority;
        protected String audio;
        protected String image;
        protected String semantic_ids;
        protected String czi;
        protected String esGloss;

        public JsonObject(String oid, String lang, String ipa, String gloss, String pos, String usageExample,
                   String dialect, String metaData, String authority, String audio, String image,
                   String semantic_ids, String czi, String esGloss) {
            this.oid = oid;
            this.lang = lang;
            this.ipa = ipa;
            this.gloss = gloss;
            this.pos = pos;
            this.usageExample = usageExample;
            this.dialect = dialect;
            this.metaData = metaData;
            this.authority = authority;
            this.audio = audio;
            this.image = image;
            this.semantic_ids = semantic_ids;
            this.czi = czi;
            this.esGloss = esGloss;
        }
    }
}

