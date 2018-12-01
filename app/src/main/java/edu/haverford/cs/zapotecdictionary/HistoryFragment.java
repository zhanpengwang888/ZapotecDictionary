package edu.haverford.cs.zapotecdictionary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View result = inflater.inflate(R.layout.history, container, false);
        ListView listView = (ListView) result.findViewById(R.id.history_list);
        historyOfWords.add("miehha");
        listAdapter = new HistoryListAdapter(getActivity(),historyOfWords);
        listView.setAdapter(listAdapter);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(savedInstanceState != null) {
//            onViewStateRestored(savedInstanceState);
//        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            ArrayList<String> store = savedInstanceState.getStringArrayList("history");
            historyOfWords.addAll(store);
        }
    }

    public void addNewWord(int newId) {
        String newWord = db.getInformationFromOID(newId, DBHelper.DICTIONARY_COLUMN_LANG);
        historyOfWords.add(newWord);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        ArrayList<String> store = new ArrayList<>(historyOfWords.getHistoryList());
        savedInstanceState.putStringArrayList("history", store);
    }


}
