package edu.haverford.cs.zapotecdictionary;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class HistoryList extends AbstractList<String> {
    private final DataSetObservable dataSetObservable;
    protected final LinkedList<String> historyOfWords;
    //protected LinkedList<Integer> historyWordsIndex;
    protected static HashMap<String, Tuple> historyWordOids;

    public HistoryList() {
        this.dataSetObservable = new DataSetObservable();
        this.historyOfWords = new LinkedList<>();
        this.historyWordOids = new HashMap<>();
    }

    class Tuple {
        int woid;
        int windex;

        public Tuple(int oid, int index) {
            this.woid = oid;
            this.windex = index;
        }

        public int getWoid() {
            return woid;
        }

        public int getWindex() {
            return windex;
        }
    }

    public LinkedList<String> getHistoryList() {
        return historyOfWords;
    }

    public boolean containsWord(String word) {
        return historyWordOids != null ? historyWordOids.containsKey(word) : false;
    }

//    public LinkedList<Integer> getIndexList() {
//        return historyWordsIndex;
//    }

    public void addAllToList(ArrayList<String> words) {
        for(String w : words) {
            add(w);
        }
        //historyOfWords.addAll(words);
    }

    //public void addAllIndex(ArrayList<Integer> indices) { historyWordsIndex.addAll(indices); }

    protected void notifyChanged() {
        this.dataSetObservable.notifyChanged();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.dataSetObservable.registerObserver(observer);
    }

    public void addOid(String word) {
        String[] args = word.split("@");
        Set<String> keys = historyWordOids.keySet();
        for(String k : keys) {
            Tuple curT = historyWordOids.get(k);
            historyWordOids.put(k, new Tuple(curT.getWoid(), curT.getWindex()+1));
        }
        historyWordOids.put(args[1], new Tuple(Integer.parseInt(args[0]), 0));
    }

    public int get_word_Oid(String word) {
        if(historyWordOids == null) {
            return -1;
        }
        return historyWordOids.containsKey(word) ? historyWordOids.get(word).getWoid() : -1;
    }

    public int getOid(int index) {
        String target_word = historyOfWords.get(index);
        String[] args = target_word.split("@");
        return Integer.parseInt(args[0]);
    }

    public void removeWord(String word) {
        if(historyWordOids != null && historyWordOids.size() > 0) {
            Tuple t = historyWordOids.get(word);
            historyOfWords.remove(t.getWindex());
            historyWordOids.remove(word);
            Set<String> keys = historyWordOids.keySet();
            for(String k : keys) {
                Tuple curT = historyWordOids.get(k);
                if(curT.getWindex() > t.getWindex()) {
                    historyWordOids.put(k, new Tuple(curT.getWoid(), curT.getWindex()-1));
                }
            }
        }
        return;
    }


    @Override
    public boolean add(String word) {
        String[] args = word.split("@");
        if(historyWordOids != null && historyWordOids.containsKey(args[1]) == false) {
            boolean isAddSuccessful = historyOfWords.offerFirst(word);
            if(isAddSuccessful) {
                addOid(word);
                notifyChanged();
            }
            return isAddSuccessful;
        }
        return false;
    }


    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.dataSetObservable.unregisterObserver(observer);
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

//    public void removeById(int index) {
//        historyOfWords.remove(index);
//        historyWordOids.remove
//        return;
//    }
}
