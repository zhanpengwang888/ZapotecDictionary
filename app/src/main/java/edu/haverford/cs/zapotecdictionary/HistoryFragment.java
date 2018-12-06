package edu.haverford.cs.zapotecdictionary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import java.text.SimpleDateFormat;

public class HistoryFragment extends Fragment {

    private HistoryListAdapter listAdapter;
    private static HistoryList historyOfWords;
    private static DBHelper db;

    public HistoryFragment() {
        super();
        historyOfWords = new HistoryList();
    }

    public void setDB(DBHelper db) {
        this.db = db;
    }

    protected ArrayList<String> getHistoryList() {
        return new ArrayList<String>(historyOfWords.getHistoryList());
    }

    protected void restoreHistoryList(ArrayList<String> historyList) {
        historyOfWords.addAllToList(historyList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View result = inflater.inflate(R.layout.history, container, false);
        ListView listView = (ListView) result.findViewById(R.id.history_list);
        historyOfWords.add("miehha");
        historyOfWords.add("miewawa");
        listAdapter = new HistoryListAdapter(getActivity(),historyOfWords);
        listView.setAdapter(listAdapter);
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
            restoreHistoryList(new ArrayList<String>(hs));
        }
        if(savedInstanceState != null) {
            ArrayList<String> store = savedInstanceState.getStringArrayList("historyList");
            historyOfWords.addAll(store);
        }
        super.onActivityCreated(savedInstanceState);
    }


    public void addNewWord(int newId) {
        String newWord = db.getInformationFromOID(newId, DBHelper.DICTIONARY_COLUMN_LANG).toString();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        newWord += ("/" + dateFormat.format(date));
        historyOfWords.add(newWord);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> store = new ArrayList<>(historyOfWords.getHistoryList());
        outState.putStringArrayList("history", store);
    }



}
