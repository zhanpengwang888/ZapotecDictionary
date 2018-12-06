package edu.haverford.cs.zapotecdictionary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;


public class SearchFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    SendText mCallback;
    private static DBHelper db;
    //protected MenuItem searchMenuItem;
    protected SearchView sv;
    private ListView listView;
    private SearchWordListAdapter listAdapter;
    private SearchWordList searchWordList;
    private static String oldQuery;

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }


    public String getOldQuery() {
        return oldQuery;
    }

    public interface SendText {
        public void sendText(int msg);
    }

    public SearchFragment() {
        super();
        searchWordList = new SearchWordList();
    }


    public void setDB(DBHelper db) {
        this.db = db;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // set on the option menu
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View result = inflater.inflate(R.layout.search_view, container, false);
        listView = result.findViewById(android.R.id.list);
        searchWordList.add(new DictionaryWord("aaa", "bbb", "ccc"));
        searchWordList.add(new DictionaryWord("ddd", "eee", "fff"));
        listAdapter = new SearchWordListAdapter(getActivity(), searchWordList);
        listView.setAdapter(listAdapter);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        final DBHelper finalDB = db;
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        sv = new SearchView(getActivity());
        MenuItemCompat.setShowAsAction(searchMenuItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(searchMenuItem, sv);
        //sv = (SearchView) searchMenuItem.getActionView();
        sv.setOnQueryTextListener(this);
        sv.setQueryHint("Search");
        sv.setIconified(false);
        if(oldQuery != null && oldQuery.length() != 0) {
            sv.setQuery(oldQuery, true);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        oldQuery = s;
        if(s.length() != 0) {
            Fragment f = fm.findFragmentByTag("WordOfDay");
            if(f != null) {
                transaction.remove(f);
                transaction.commit();
            }
            Log.e("textChange", "-------------------------" + s);
            SearchWordList res = searchWordList;
            if (s != null && !s.isEmpty()) {
                SearchWordList tmp = new SearchWordList();
                for (int i = 0; i < searchWordList.size(); i++) {
                    DictionaryWord word = searchWordList.get(i);
                    if (word.getEnglish().contains(s) || word.getSpanish().contains(s) || word.getZapotec().contains(s)) {
                        tmp.add(word);
                    }
                }
                res = tmp;
            }
            listAdapter = new SearchWordListAdapter(getActivity(), res);
            listView.setAdapter(listAdapter);
            return true;
        } else {
            Fragment f = fm.findFragmentByTag("WordOfDay");
            if(f == null) {
                transaction.add(android.R.id.content, new WordOfDayFragment(), "WordOfDay");
                transaction.commit();
            }
            fm.popBackStackImmediate("WordOfDay", 0);
            return true;
        }
    }

}
