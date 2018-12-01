package edu.haverford.cs.zapotecdictionary;


import android.Manifest;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class MainActivity  extends FragmentActivity
                        implements ActivityCompat.OnRequestPermissionsResultCallback, SearchFragment.SendText{

    //private ViewPager viewPager;
    protected ActionBar actionBar;
    protected WordViewFragment wf;
    protected HistoryFragment hf;
    protected DBHelper db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
            //hf = (HistoryFragment)getSupportFragmentManager().getFragment(savedInstanceState, "historyFragment");
            //hf = savedInstanceState.getSerializable("historyFragment");
        }

        if(Build.VERSION.SDK_INT >= 23) {
            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.INTERNET,
                                    Manifest.permission.ACCESS_NETWORK_STATE};
            requestPermissions(permission, R.integer.WRITE_GET_PERM);
        } else {
            String url = "http://talkingdictionary.swarthmore.edu/dl/retrieve.php";

            db = new DBHelper(getApplicationContext());
            DownloadData downloadData = new DownloadData(db, url);
            downloadData.execute();
        }

        actionBar = getActionBar();
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

        wf = new WordViewFragment();
        wf.setDB(db);
        hf = new HistoryFragment();
        hf.setDB(db);

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        Fragment wordDay = new WordOfDayFragment();
        transaction.add(android.R.id.content, wordDay, "WordOfDay");
        transaction.commit();

    }

    @Override public void sendText(int msg) {
        wf.set_curId(msg);
        HistoryFragment hf = new HistoryFragment();
        hf.addNewWord(msg);
    }

    /*
        Get storage write and read requests for sdk version 23 and higher
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //TODO: add more cases for checking other permissions
            case R.integer.WRITE_GET_PERM:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Granted.
                    String url = "http://talkingdictionary.swarthmore.edu/dl/retrieve.php";

                    DownloadData downloadData = new DownloadData(db, url);
                    downloadData.execute();
                }
                else{
                    Toast.makeText(this, R.string.write_perm_error, Toast.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //getSupportFragmentManager().putFragment(outState, "historyFragment", hf);
        //outState.putSerializable("historyFragment", hf);
    }


}

class DownloadData extends AsyncTask<String, Void, Void> {
    public DBHelper db;
    private static HttpURLConnection con;

    private String urlStr;
    // unzipfile name
    private String dictionaryfp = "/tlacochahuaya_content/tlacochahuaya_export.json";

    // response code
    private final int NO_UPDATE = 204;
    private final int ERROR1 = 403;
    private final int ERROR2 = 404;
    private final int SUCCESS = 200;



    public DownloadData(DBHelper db, String url) {
        this.db = db;
        this.urlStr = url;
        con = null;
    }

    public String getUrlParameters(String dict, String current, String export, String dl_type, String hash, String current_hash) {
        StringBuilder sb = new StringBuilder("dict=");
        if(dict != null) {
            sb.append(dict);
            sb.append("&");
        }
        if(current != null) {
            sb.append("current=");
            sb.append(current);
            sb.append("&");
        }
        if(export != null) {
            sb.append("export=");
            sb.append(export);
            sb.append("&");
        }
        if(dl_type != null) {
            sb.append("dl_type=");
            sb.append(dl_type);
            sb.append("&");
        }
        if(hash != null) {
            sb.append("hash=");
            sb.append(hash);
            sb.append("&");
        }
        if(current_hash != null) {
            sb.append("current_hash=");
            sb.append(current_hash);
            sb.append("&");
        }
        return sb.toString();
    }

    /*
         - `download` will call `updateData` immediately if dictionary file does not exists
         - if file exists, then if will get hash and send post request with hash, which will download
            dictionary file if there is any updates, do nothing if no updates
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void download(String dict, String dl_type) {
        String fp = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getPath() +  "/dataFolder" + dictionaryfp;
        File f = new File(fp);
        if(!f.exists()) {
            updateData(dict, dl_type, "");
            return;
        }
        InputStream in = null;
        FileOutputStream out = null;

        String urlGetHash = getUrlParameters(dict, "TRUE", null, dl_type, "TRUE", "");
        byte[] data = urlGetHash.getBytes(Charset.defaultCharset());
        byte[] buffer = new byte[1024];
        int dataSize;
        String last_hash = "";

        // get last hash
        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(data);
            } catch (Exception e) {
                Log.e("bg0", "Error writing post msg");
                e.printStackTrace();
            }

            Log.e("hash code", "GET HASH ___________ " + con.getResponseCode()+ " ");

            in = con.getInputStream();
            dataSize = in.read(buffer);
            StringBuilder sb = new StringBuilder();
            while(dataSize > 0) {
                sb.append(new String(buffer, "UTF-8"), 0, dataSize);
                buffer = new byte[1024];
                dataSize = in.read(buffer);
            }
            last_hash = sb.toString();
            if(last_hash == null) {
                Log.e("get hash", "Error getting last hash in updateData");
            } else {
                //test
                Log.e("got hash", "+_+_+_+_+_+_+_+_+" + last_hash);
                updateData(dict, dl_type, last_hash);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateData(String dict, String dl_type, String last_hash) {
        InputStream in = null;
        FileOutputStream out = null;
        byte[] buffer = new byte[1024];
        int dataSize = 0;

        String urlParameters = getUrlParameters(dict, null, "TRUE", dl_type, last_hash, null);
        byte[] dldata = urlParameters.getBytes(Charset.defaultCharset());

        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            // provide parameters required for POST request
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(dldata);
            } catch (Exception e) {
                Log.e("bg0", "Error writing post msg");
                e.printStackTrace();
            }

            // check response code
            int responseCode = con.getResponseCode();
            //test
            Log.e("res code", "++_+_+_+_+_+" + responseCode);
            //test
            if(responseCode == ERROR1 || responseCode == ERROR2) {
                // TODO show error message to user
                //AlertDialog.Builder adb = new AlertDialog.Builder(context);

            } else if (responseCode == SUCCESS) {
                in = con.getInputStream();
                File path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                File file = new File(path, "update.zip");
                out = new FileOutputStream(file, true);
                dataSize = in.read(buffer);
                while (dataSize > 0) {
                    out.write(buffer, 0, dataSize);
                    dataSize = in.read(buffer);
                }
                out.flush();
                String fp = path.getPath() + "/dataFolder";
                unzip_file(file.getPath(), fp);
                //test
                Log.e("output file ", "==========================" + path.getPath() + "/dataFolder");
                //test
                writeDB(fp + dictionaryfp);
            } else if (responseCode == NO_UPDATE) {
                Log.e("no update", "dictionary data up to date");
            }
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
        try
        {
            File file = new File(zip_source);

            ZipFile zip = new ZipFile(file);

            File des = new File(destination);
            des.mkdir();
            Enumeration zipFileEntries = zip.entries();

            while (zipFileEntries.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(destination, currentEntry);
                File destinationParent = destFile.getParentFile();
                destinationParent.mkdirs();

                if (!entry.isDirectory())
                {
                    BufferedInputStream is = new BufferedInputStream(zip
                            .getInputStream(entry));
                    int numBytes = 0;
                    byte data[] = new byte[2048];
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos,
                            2048);
                    while ((numBytes = is.read(data, 0, 2048)) != -1) {
                        dest.write(data, 0, numBytes);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            }
        }
        catch (Exception e)
        {
            Log.e ("ERROR: ", "Error unziping files");
            e.printStackTrace();
        }

//        try {
//            ZipFile zipFile = new ZipFile(zip_source);
//            zipFile.extractAll(destination);
//        } catch (ZipException e) {
//            e.printStackTrace();
//        }
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
            Log.e("test", "write_DB+++++++++++++++++++++" + objArr[0].oid);
        }
        //test
        for(JsonObject j : objArr) {
            db.insertNewWord(j.oid, j.lang, j.ipa, j.gloss, j.pos, j.usageExample,
                    j.dialect, j.metaData, j.authority, j.audio, j.image, j.semantic_ids, j.czi, j.esGloss);
        }
        //test
        Log.e("dbtest", "writeDB _+_+_+_+_+_+_+_+_+_+_+_+_+" + db.getInformationFromOID(Integer.parseInt(objArr[0].oid), "lang"));
        //test
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... strings){
        download("tlacochahuaya","0");
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

