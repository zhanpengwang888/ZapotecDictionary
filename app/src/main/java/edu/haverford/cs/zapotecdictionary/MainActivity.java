package edu.haverford.cs.zapotecdictionary;


import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


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

    public DownloadData(Context context, String url) {
        db = new ZapotecDictionaryDBHelper(context);
        this.urlStr = url;
        con = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... strings){
        String urlParameters = "dict=tlacochahuaya&export=TRUE&dl_type=1";
        byte[] data = urlParameters.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = new byte[1024];
        int dataSize = 0;

        InputStream in = null;
        FileOutputStream out = null;

        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();

            // provide parameters required for POST request
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(data);
            } catch (Exception e) {
                Log.e("bg0", "Error writing post msg");
                e.printStackTrace();
            }

            in = con.getInputStream();
            out = new FileOutputStream("update.zip");

            dataSize = in.read(buffer);
            Log.e("----------------------- cur size" , Integer.toString(dataSize));
            while(dataSize > 0) {
                Log.e("----------------------- cur size" , Integer.toString(dataSize));
                out.write(buffer, 0, dataSize);
                dataSize = in.read(buffer);
            }
            out.flush();
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

        return null;
    }
}

