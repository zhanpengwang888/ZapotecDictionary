package edu.haverford.cs.zapotecdictionary;


import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import java.util.LinkedHashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class MainActivity  extends FragmentActivity
                        implements ActivityCompat.OnRequestPermissionsResultCallback, SearchFragment.SendText{

    //private ViewPager viewPager;
    protected ActionBar actionBar;
    protected WordViewFragment wf;
    protected HistoryFragment hf;
    protected SettingsFragment sf;
    protected SearchFragment searchFragment;
    protected WordOfDayFragment wordfrag;
    protected DBHelper db;
    private Bundle savedState;
    protected DownloadData downloadData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(getApplicationContext());
        String url = "http://talkingdictionary.swarthmore.edu/dl/retrieve.php";
        downloadData = new DownloadData(db, getSharedPreferences("info",  Context.MODE_PRIVATE), url, this);
        downloadData.execute();


        wf = new WordViewFragment();
        hf = new HistoryFragment();
        sf = new SettingsFragment();
        sf.setmActivity(this);
        searchFragment = new SearchFragment();


        if (savedInstanceState != null && savedState == null) {
            onRestoreInstanceState(savedInstanceState);
        } else if(savedState != null) {
            hf.restoreHistoryList(savedState.getStringArrayList("historyList"));
            savedState = null;
        }

        if(Build.VERSION.SDK_INT >= 23) {
            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.INTERNET,
                                    Manifest.permission.ACCESS_NETWORK_STATE};
            requestPermissions(permission, R.integer.WRITE_GET_PERM);
        }


        wordfrag = new WordOfDayFragment();
        wf.setDB(db);
        hf.setDB(db);
        searchFragment.setDB(db);
        wordfrag.setDB(db);
        //wordfrag.set_curID(db.getOidOfRandomRow());



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
    }

    @Override public void sendText(int msg, boolean addHistory) {
        wf.set_curId(msg);
        if(addHistory) {
            hf.addNewWord(msg);
        }
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

                    if(downloadData != null) {
                        downloadData = new DownloadData(db, getSharedPreferences("info", Context.MODE_PRIVATE), url, this);
                    }
                    downloadData.execute();
                }
                else{
                    Toast.makeText(this, R.string.write_perm_error, Toast.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(sf.switchArr[0] == null) {
            for(int i = 0; i < sf.switchArr.length; i++) {
                editor.putBoolean(Integer.toString(i), false);
            }
        } else {
            for(int i = 0; i < sf.switchArr.length; i++) {
                editor.putBoolean(Integer.toString(i), sf.switchArr[i].isChecked());
            }
        }
        editor.putBoolean("wifi", sf.wifi_only);
        editor.putStringSet("historyList", new LinkedHashSet<String>(hf.getHistoryList()));
        editor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(db != null) {
            db.close();
        }
    }


}

class DownloadData extends AsyncTask<String, Void, Void> {
    public DBHelper db;
    private static HttpURLConnection con;
    private SharedPreferences sp;

    private boolean toMakeaToast = false;
    private String urlStr;
    // unzipfile name
    private final String dictionaryfp = "/tlacochahuaya_content/tlacochahuaya_export.json";

    // response code
    private final int NO_UPDATE = 204;
    private final int ERROR1 = 403;
    private final int ERROR2 = 404;
    private final int SUCCESS = 200;

    private String last_dl_type;

    private MainActivity mActivity;

    private static boolean wifi_only;



    public DownloadData(DBHelper db, SharedPreferences sp, String url, MainActivity mActivity) {

        this.mActivity = mActivity;
        this.sp = sp;
        this.db = db;
        this.urlStr = url;
        con = null;
        last_dl_type = null;
        wifi_only = true;

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

        if(last_dl_type == null) {
            last_dl_type = dl_type;
            try {
                updateData(dict, dl_type, last_hash);
                last_dl_type = dl_type;
            } catch(Exception e) {
                Log.e("bg update", "Always download something wrong");
                e.printStackTrace();
            }
            return;
        } else if(last_dl_type != dl_type) {
            try {
                updateData(dict, dl_type, last_hash);
                last_dl_type = dl_type;
            } catch(Exception e) {
                Log.e("bg update", "Always download something wrong");
                e.printStackTrace();
            }
            return;
        }

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
                Log.e("got hash", "+_+_+_+_+_+_+_+_+" + last_hash + " dl_type " + dl_type);
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
            Log.e("unzip1 ", "download1");
            ZipFile zip = new ZipFile(file);
            Log.e("unzip2 ", "download2");
            Thread.sleep(1000);
            File des = new File(destination);
            Thread.sleep(1000);
            Log.e("unzip3 ", "download3");
            des.mkdir();
            Thread.sleep(1000);
            Enumeration zipFileEntries = zip.entries();
            Thread.sleep(1000);
            Log.e("unzip4 ", "download4");
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

            File oldUpdate = new File(zip_source);
            Log.e("delete old", oldUpdate.getPath());
            if(oldUpdate.exists()) {
                oldUpdate.delete();
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
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        JsonObject[] objArr = gson.fromJson(sb.toString(), JsonObject[].class);
        //test
        if(objArr.length > 0) {
            Log.e("test", "write_DB+++++++++++++++++++++" + objArr[0].oid);
        }
        //test
        for(JsonObject j : objArr) {
            // test
//            Log.e("lang ----", "---" + j.getLang() + " " + j.getIpa() + " " + j.getGloss() + " " + j.getPos() + " " + j.getUsage_example()
//            + j.getDialect() + " " + j.getMetadata() + " " + j.getAuthority() + " " + j.getAudio() + " " + j.getImage() + " " + j.getSemantic_ids() +
//            " " + j.getCzi() + " " + j.getEs_gloss());
            // test
            db.insertNewWord(j.oid, j.getLang(), j.getIpa(), j.getGloss(), j.getPos(), j.getUsage_example(),
                    j.getDialect(), j.getMetadata(), j.getAuthority(), j.getAudio(), j.getImage(), j.getSemantic_ids(), j.getCzi(), j.getEs_gloss());
        }
        //test
        Log.e("dbtest", "writeDB _+_+_+_+_+_+_+_+_+_+_+_+_+" + db.getInformationFromOID(Integer.parseInt(objArr[0].oid), "metadata"));
        //test
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... strings){
        Integer target = null;

        ConnectivityManager cm =
                (ConnectivityManager)mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean wifiConnected = wifiNetwork != null && wifiNetwork.isConnected();
        NetworkInfo.State mobile = mobileNetwork.getState();

        if(sp == null) {
            if(mActivity != null && mActivity.sf != null) {
                wifi_only = mActivity.sf.getWifiOnly();
            }
            if((wifi_only == false && mobile == NetworkInfo.State.CONNECTED) || wifiConnected) {
                download("tlacochahuaya","1");
            } else {
                toMakeaToast = true;
                //Toast.makeText(mActivity, "Please turn on wifi or data, and restart for downloading dictionary data", Toast.LENGTH_LONG);
            }
        } else {
            for(int i = 0; i < 4; i++) {
                if(sp.getBoolean(Integer.toString(i), false)) {
                    target = i;
                    break;
                }
            }
            wifi_only = sp.getBoolean("wifi", true);
        }

        if((wifi_only == false
                && cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) ||
                cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            if(target == null) {
                download("tlacochahuaya","1");
            } else {
                download("tlacochahuaya",target.toString());
            }
        } else {
            toMakeaToast = true;
            //Toast.makeText(mActivity, "Please turn on wifi or data, and restart for downloading dictionary data", Toast.LENGTH_LONG);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(null);
        if (toMakeaToast) {
            Toast.makeText(mActivity, "Please turn on wifi or data, and restart for downloading dictionary data. ", Toast.LENGTH_LONG*3).show();
            toMakeaToast = false;
        }

    }

    class JsonObject {
        protected String oid;
        protected String lang;
        protected String ipa;
        protected String gloss;
        protected String pos;
        protected String usage_example;
        protected String dialect;
        protected String metadata;
        protected String authority;
        protected String audio;
        protected String image;
        protected String semantic_ids;
        protected String czi;
        protected String es_gloss;

        public JsonObject(String oid, String lang, String ipa, String gloss, String pos, String usageExample,
                   String dialect, String metaData, String authority, String audio, String image,
                   String semantic_ids, String czi, String esGloss) {
            this.oid = oid;
            this.lang = lang;
            this.ipa = ipa;
            this.gloss = gloss;
            this.pos = pos;
            this.usage_example = usageExample;
            this.dialect = dialect;
            this.metadata = metaData;
            this.authority = authority;
            this.audio = audio;
            this.image = image;
            this.semantic_ids = semantic_ids;
            this.czi = czi;
            this.es_gloss = esGloss;
        }

        public String getLang() {
            if (this.lang != null) {
                return this.lang.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getIpa() {
            if (this.ipa != null) {
                return this.ipa.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getGloss() {
            if (this.gloss != null) {
                return this.gloss.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getPos() {
            if (this.pos != null) {
                return this.pos.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getUsage_example() {
            if (this.usage_example != null) {
                return this.usage_example.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getDialect() {
            if (this.dialect != null) {
                return this.dialect.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getMetadata() {
            if (this.metadata != null) {
                return this.metadata.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getAuthority() {
            if (this.authority != null) {
                return this.authority.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getAudio() {
            if (this.audio != null) {
                return this.audio.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getImage() {
            if (this.image != null) {
                return this.image.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getSemantic_ids() {
            if (this.semantic_ids != null) {
                return this.semantic_ids.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getCzi() {
            if (this.czi != null) {
                return this.czi.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }

        public String getEs_gloss() {
            if (this.es_gloss != null) {
                return this.es_gloss.replace("&#8217;", "'").replace("&quot;", "\"");
            }
            return null;
        }
    }
}

