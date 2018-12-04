package edu.haverford.cs.zapotecdictionary;

public class DictionaryWord {
    private String spanish;
    private String english;
    private String zapotec;

    public DictionaryWord(String spanish, String english, String zapotec) {
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
}
