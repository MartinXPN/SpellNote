package com.xpn.spellnote.models;


public class WordModel {

    private String word;
    private int usage;
    private boolean isUserDefined = false;

    public WordModel( String word, int usage, boolean isUserDefined ) {
        this.word = word;
        this.usage = usage;
        this.isUserDefined = isUserDefined;
    }


    public WordModel( String word, int usage ) {
        this(word, usage, false);
    }

    public String getWord() {
        return word;
    }
    public int getUsage() {
        return usage;
    }
    public boolean getUserDefined() {
        return isUserDefined;
    }
}
