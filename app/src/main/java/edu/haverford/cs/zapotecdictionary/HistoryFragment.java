package edu.haverford.cs.zapotecdictionary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

public class HistoryFragment extends Fragment {


    protected static SearchFragment.SendText mCallback;
    private HistoryListAdapter listAdapter;
    private static HistoryList historyOfWords;
    private static DBHelper db;
    private static ListView listView;

    public HistoryFragment() {
        super();
        if(historyOfWords == null) {
            historyOfWords = new HistoryList();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (SearchFragment.SendText) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TextClicked");
        }
    }


    public void setDB(DBHelper db) {
        this.db = db;
    }

    protected ArrayList<String> getHistoryList() {
        return new ArrayList<String>(historyOfWords.getHistoryList());
    }

    protected ArrayList<String> getHistoryIndices() {
        LinkedList<Integer> indices = historyOfWords.getIndexList();
        ArrayList<String> historyIndices = new ArrayList<>();
        for(int i : indices) {
            historyIndices.add(Integer.toString(i));
        }
        return historyIndices;
    }

    protected void restoreHistoryList(ArrayList<String> historyList) {
        historyOfWords.addAllToList(historyList);
    }

    protected void restoreHistoryIndex(ArrayList<Integer> historyIndices) {
        historyOfWords.addAllIndex(historyIndices);
    }

    public void sendOid(int oid, boolean addHistory){
        mCallback.sendText(oid, addHistory);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View result = inflater.inflate(R.layout.history, container, false);
        listView = (ListView) result.findViewById(R.id.history_list);
        listAdapter = new HistoryListAdapter(getActivity(), historyOfWords);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sendOid(historyOfWords.getOid(i), false);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                Fragment f = fm.findFragmentByTag("WordView");
                if(f != null) {
                    ft.remove(f);
                }
                f = new WordViewFragment();
                ft.replace(android.R.id.content, f, "WordView")
                        .addToBackStack("Search")
                        .commit();
            }
        });
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            onViewStateRestored(savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        SharedPreferences sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        if(sp != null) {
            HashSet<String> hs = (HashSet<String>) sp.getStringSet("historyList", new HashSet<String>());
            HashSet<String> temp = (HashSet<String>) sp.getStringSet("historyIndex", new HashSet<String>());
            HashSet<Integer> hi = new HashSet<>();
            for (String str : temp) {
                hi.add(Integer.parseInt(str));
            }
            restoreHistoryList(new ArrayList<String>(hs));
            restoreHistoryIndex(new ArrayList<Integer>(hi));
        }
        //TODO: onRestart adding duplicate words to history
        if(savedInstanceState != null) {
            ArrayList<String> store = savedInstanceState.getStringArrayList("historyList");
            historyOfWords.addAll(store);
            historyOfWords.addAllIndex(savedInstanceState.getIntegerArrayList("historyIndex"));
            savedInstanceState = null;
        }
        super.onActivityCreated(savedInstanceState);
    }


    public void addNewWord(int newId) {
        String newWord = db.getInformationFromOID(newId, DBHelper.DICTIONARY_COLUMN_LANG).toString();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        newWord += ("/" + dateFormat.format(date));
        for(int i = 0; i < historyOfWords.getIndexList().size(); i++) {
            if(historyOfWords.getOid(i) == newId) {
                historyOfWords.removeById(i);
            }
        }
        historyOfWords.add(newWord);
        historyOfWords.addOid(newId);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> store = new ArrayList<>(historyOfWords.getHistoryList());
        outState.putStringArrayList("history", store);
        outState.putIntegerArrayList("historyIndex", new ArrayList<Integer>(historyOfWords.getIndexList()));
    }



}
