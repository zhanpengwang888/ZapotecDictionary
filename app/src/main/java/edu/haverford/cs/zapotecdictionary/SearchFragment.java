package edu.haverford.cs.zapotecdictionary;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;


public class SearchFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    protected static SendText mCallback;
    private static DBHelper db;
    //protected MenuItem searchMenuItem;
    protected SearchView sv;
    private ListView listView;
    private SearchWordListAdapter listAdapter;
    private SearchWordList searchWordList;
    private static String oldQuery;

    public interface SendText {
        public void sendText(int msg);
    }

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

    public SearchFragment() {
        super();
        searchWordList = new SearchWordList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (SendText) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TextClicked");
        }
    }

    public void sendOid(int oid){
        mCallback.sendText(oid);
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
        //searchWordList.add(new DictionaryWord("aaa", "bbb", "ccc"));
        //searchWordList.add(new DictionaryWord("ddd", "eee", "fff"));
        listAdapter = new SearchWordListAdapter(getActivity(), searchWordList);
        listView.setAdapter(listAdapter);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
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
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        oldQuery = s;
        if(s.length() != 0) {
            Fragment f = fm.findFragmentByTag("WordOfDay");
            if(f != null) {
                transaction.remove(f);
                transaction.commit();
            }
            Log.e("textChange", "-------------------------" + s);
            //SearchWordList res = searchWordList;

            SearchWordList tmp = new SearchWordList();
            ArrayList<Integer> oids = db.getOidsForQueryMatchingString(s);

            for (Integer oid : oids) {
                StringBuilder spanish = db.getInformationFromOID(oid, db.DICTIONARY_COLUMN_ES_GLOSS);
                spanish = spanish.length() == 0 ? new StringBuilder("Spanish: ") : spanish.insert(0, "Spanish: ");
                StringBuilder english = db.getInformationFromOID(oid, db.DICTIONARY_COLUMN_GLOSSARY);
                english = english.length() == 0 ? new StringBuilder("English: ") : english.insert(0, "English: ");
                StringBuilder zapotec = db.getInformationFromOID(oid, db.DICTIONARY_COLUMN_LANG);
                zapotec = zapotec.length() == 0 ? new StringBuilder("Zapotec: ") : zapotec.insert(0, "Zapotec: ");
                tmp.add(new DictionaryWord(oid, spanish.toString(), english.toString(), zapotec.toString()));
            }
            searchWordList = tmp;
            //res = tmp;

            listAdapter = new SearchWordListAdapter(getActivity(), searchWordList);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    DictionaryWord item = searchWordList.get(i);
                    sendOid(item.getOid());
                    android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                    if(fm.findFragmentByTag("WordView") == null) {
                        ft.add(new WordViewFragment(), "WordView");
                    }
                    ft.commit();
                    fm.popBackStackImmediate("WordView", 0);
                }
            });
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
