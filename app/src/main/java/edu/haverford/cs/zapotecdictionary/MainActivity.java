package edu.haverford.cs.zapotecdictionary;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.shamanland.fonticon.FontIconDrawable;

public class MainActivity  extends FragmentActivity implements
        ActionBar.TabListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static class AppSectionPagerAdapter extends FragmentActivity {
        private Fragment[] frags;
        SearchFragment tabSearch;
        HistoryFragment tabHistory;
        SettingFragment tabSetting;

        public AppSectionPagerAdapter() {
            tabSearch = new SearchFragment();
            tabHistory = new HistoryFragment();
            tabSetting = new SettingFragment();

            frags = new Fragment[] { tabSearch, tabHistory, tabSearch };
        }



    }
}
