package edu.haverford.cs.zapotecdictionary;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Application;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shamanland.fonticon.FontIconDrawable;
import com.shamanland.fonticon.FontIconTypefaceHolder;

public class MainActivity  extends FragmentActivity implements
        ActionBar.TabListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AppSectionsPagerAdapter appSectionsPagerAdapter;
    private ViewPager viewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontIconTypefaceHolder.init(getAssets(), "fontawesome-4.2.0.ttf");
        //final Application app = (Application)getApplication();
        setContentView(R.layout.activity_main);

        appSectionsPagerAdapter = new AppSectionsPagerAdapter(
                getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = findViewById(R.id.main_page);
        viewPager.setOffscreenPageLimit(appSectionsPagerAdapter.getCount());
        viewPager.setAdapter(appSectionsPagerAdapter);

        final String[] subtitles = new String[] {
                getString(R.string.subtitle_search),
                getString(R.string.subtitle_history),
                getString(R.string.subtitle_settings)
        };

        // sets the tabs for actionBar, needs to modified `subtitles`
        viewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                        actionBar.setSubtitle(subtitles[position]);
                    }
                });

        Drawable[] tabIcons = new Drawable[3];
        tabIcons[0] = FontIconDrawable.inflate(this, R.xml.icon_search);
        tabIcons[1] = FontIconDrawable.inflate(this, R.xml.icon_history);
        tabIcons[2] = FontIconDrawable.inflate(this, R.xml.icon_settings);
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < appSectionsPagerAdapter.getCount(); i++) {
            ActionBar.Tab tab = actionBar.newTab();
            tab.setTabListener(this);
            tab.setIcon(tabIcons[i]);
            actionBar.addTab(tab);
        }

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else {
            viewPager.setCurrentItem(0);
        }
    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] frags;
        SearchFragment tabSearch;
        HistoryFragment tabHistory;
        SettingFragment tabSettings;

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            tabSearch = new SearchFragment();
            tabHistory = new HistoryFragment();
            tabSettings = new SettingFragment();

            frags = new Fragment[] { tabSearch, tabHistory, tabSearch };
        }

        @Override
        public Fragment getItem(int i) {
            return frags[i];
        }

        @Override
        public int getCount() {
            return frags.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }

}
