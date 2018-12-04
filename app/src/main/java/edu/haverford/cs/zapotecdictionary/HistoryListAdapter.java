package edu.haverford.cs.zapotecdictionary;


import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class HistoryListAdapter extends BaseAdapter implements ListAdapter {
    private Context mContext;
    private HistoryList searchHistory;

    public HistoryListAdapter(Context context, HistoryList historyOfWords) {
        this.mContext = context;
        this.searchHistory = historyOfWords;
        DataSetObserver observer = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        };
        this.searchHistory.registerDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        if (!searchHistory.isEmpty()) {
            return searchHistory.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (!searchHistory.isEmpty()) {
            return searchHistory.get(position);
        }
        return null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String word = searchHistory.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.history_item, parent, false);
        }
        TextView theWord = (TextView) convertView.findViewById(R.id.zapotec_word);
        TextView date = (TextView) convertView.findViewById(R.id.word_date);
        String[] wordInfo = word.split("/");
        theWord.setText(wordInfo[0]);
        //date.setText(wordInfo[1]);
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        searchHistory.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        searchHistory.unregisterDataSetObserver(observer);
    }
}
