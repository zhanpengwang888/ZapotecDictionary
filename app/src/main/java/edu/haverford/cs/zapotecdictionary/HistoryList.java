package edu.haverford.cs.zapotecdictionary;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

import java.util.AbstractList;
import java.util.LinkedList;

public class HistoryList extends AbstractList<String> {
    private final DataSetObservable dataSetObservable;
    protected final LinkedList<String> historyOfWords;

    public HistoryList() {
        this.dataSetObservable = new DataSetObservable();
        this.historyOfWords = new LinkedList<>();
    }

    public LinkedList<String> getHistoryList() {
        return historyOfWords;
    }

    protected void notifyChanged() {
        this.dataSetObservable.notifyChanged();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.dataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.dataSetObservable.unregisterObserver(observer);
    }

    @Override
    public boolean add(String word) {
        boolean isAddSuccessful = historyOfWords.offerFirst(word);
        notifyChanged();
        return isAddSuccessful;
    }

    @Override
    public int indexOf(Object word){
        return historyOfWords.indexOf(word);
    }

    @Override
    public void clear() {
        historyOfWords.clear();
        notifyChanged();
    }

    @Override
    public String get(int index) {
        return historyOfWords.get(index);
    }

    @Override
    public int size() {
        return historyOfWords.size();
    }
}
