package edu.haverford.cs.zapotecdictionary;

public class DictionaryWord {
    private int oid;
    private String spanish;
    private String english;
    private String zapotec;

    public DictionaryWord(int oid, String spanish, String english, String zapotec) {
        this.oid = oid;
        this.spanish = spanish;
        this.english = english;
        this.zapotec = zapotec;
    }

    public String getSpanish() {
        return this.spanish;
    }

    public String getEnglish() {
        return this.english;
    }

    public String getZapotec() {
        return this.zapotec;
    }

    public int getOid() {
        return this.oid;
    }
}
