package edu.haverford.cs.zapotecdictionary;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class SearchWordListAdapter extends BaseAdapter implements ListAdapter {
    private Context mContext;
    private SearchWordList searchWordList;

    public SearchWordListAdapter(Context context, SearchWordList historyOfWords) {
        this.mContext = context;
        this.searchWordList = historyOfWords;
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
        this.searchWordList.registerDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        if (!searchWordList.isEmpty()) {
            return searchWordList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (!searchWordList.isEmpty()) {
            return searchWordList.get(position);
        }
        return null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DictionaryWord word = searchWordList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_view, parent, false);
        }
        TextView english = (TextView) convertView.findViewById(R.id.english);
        TextView spanish = (TextView) convertView.findViewById(R.id.spanish);
        TextView zapotec = (TextView) convertView.findViewById(R.id.zapotec);
        english.setText(word.getEnglish());
        spanish.setText(word.getSpanish());
        zapotec.setText(word.getZapotec());
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        searchWordList.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        searchWordList.unregisterDataSetObserver(observer);
    }
}
