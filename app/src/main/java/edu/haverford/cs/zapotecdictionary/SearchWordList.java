package edu.haverford.cs.zapotecdictionary;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

import java.util.AbstractList;
import java.util.ArrayList;

public class SearchWordList extends AbstractList<DictionaryWord> {
    private final DataSetObservable dataSetObservable;
    protected final ArrayList<DictionaryWord> searchWordList;

    public SearchWordList() {
        this.dataSetObservable = new DataSetObservable();
        this.searchWordList = new ArrayList<>();
    }

    public ArrayList<DictionaryWord> getSearchWordList() {
        return searchWordList;
    }

    public void addAllToList(ArrayList<DictionaryWord> words) {
        searchWordList.addAll(words);
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
    public boolean add(DictionaryWord word) {
        boolean isAddSuccessful = searchWordList.add(word);
        notifyChanged();
        return isAddSuccessful;
    }


    @Override
    public int indexOf(Object word){
        return searchWordList.indexOf(word);
    }

    @Override
    public void clear() {
        searchWordList.clear();
        notifyChanged();
    }

    @Override
    public DictionaryWord get(int index) {
        return searchWordList.get(index);
    }

    @Override
    public int size() {
        return searchWordList.size();
    }
}
