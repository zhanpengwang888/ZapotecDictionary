package edu.haverford.cs.zapotecdictionary;


import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


public class MainActivity  extends FragmentActivity {

    //private ViewPager viewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

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

//    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
//        private Fragment[] frags;
//        SearchFragment tabSearch;
//        HistoryFragment tabHistory;
//        SettingsFragment tabSettings;
//
//        public AppSectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//            tabSearch = new SearchFragment();
//            tabHistory = new HistoryFragment();
//            tabSettings = new SettingsFragment();
//
//            frags = new Fragment[] { tabSearch, tabHistory, tabSearch };
//        }
//
//        @Override
//        public Fragment getItem(int i) {
//            return frags[i];
//        }
//
//        @Override
//        public int getCount() {
//            return frags.length;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "";
//        }
//
//    }

}
