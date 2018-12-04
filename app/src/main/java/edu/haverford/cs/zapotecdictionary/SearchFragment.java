package edu.haverford.cs.zapotecdictionary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
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
        this.setRetainInstance(true);
        if(savedInstanceState != null) {
            onViewStateRestored(savedInstanceState);
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu);
        searchMenuItem = menu.findItem(R.menu.search_menu);
        sv = (SearchView) searchMenuItem.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e("textChange", s);
                onQueryTextChange(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


}
