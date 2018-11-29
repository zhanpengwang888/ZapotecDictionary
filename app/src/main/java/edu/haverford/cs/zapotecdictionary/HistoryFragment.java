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
    private HistoryList historyOfWords;

    public HistoryFragment() {
        super();
        historyOfWords = new HistoryList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View result = inflater.inflate(R.layout.history, container, false);
        ListView listView = (ListView) result.findViewById(R.id.history_list);
        // test
        historyOfWords.add("yarbay");
        historyOfWords.add("haha");
        // test
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
        Bundle bundle = getArguments();
        if (bundle!=null) {
            ArrayList<String> words = bundle.getStringArrayList("words");
            if (words != null) {
                historyOfWords.addAll(words);
            }
        }
    }


}
