package edu.haverford.cs.zapotecdictionary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SearchFragment extends Fragment {

    SendText mCallback;
    private static DBHelper db;
    protected SearchView sv;
    protected MenuItem searchMenuItem;

    public interface SendText {
        public void sendText(int msg);
    }

    public SearchFragment() {
        super();
    }


    public void setDB(DBHelper db) {
        this.db = db;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        return inflater.inflate(R.layout.search_view, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true); // set on the option menu
        if(savedInstanceState != null) {
            onViewStateRestored(savedInstanceState);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        final DBHelper finalDB = db;
        menuInflater.inflate(R.menu.search_menu, menu);
        searchMenuItem = menu.findItem(R.id.search_view_menu);
        sv = (SearchView) searchMenuItem.getActionView().findViewById(R.id.searchView);
        sv.setIconified(false);
        sv.setSubmitButtonEnabled(true);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e("textChange", s);
                onQueryTextChange(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("textChange", "-------------------------" + s);

                return true;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        Fragment wf = fm.findFragmentByTag("WordOfDay");
//        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
//        transaction.remove(wf);
//        Fragment searchFragment = new SearchFragment();
//        transaction.add(android.R.id.content, searchFragment, "SearchFragment");
//        transaction.commit();

        menu.clear();
        MenuItemCompat.expandActionView(searchMenuItem);
        Log.e("miehaha", "==============+++++" + sv.getQuery());
        sv.setQuery(sv.getQuery().toString(), true);
        //sv.setQuery("haha",true);
        super.onPrepareOptionsMenu(menu);

    }
}
