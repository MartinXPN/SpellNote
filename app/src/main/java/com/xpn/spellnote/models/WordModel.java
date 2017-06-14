package com.xpn.spellnote.models;


import android.support.annotation.NonNull;

public class WordModel implements Comparable<WordModel> {

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

    @Override
    public int compareTo(@NonNull WordModel wordModel) {
        return Integer.valueOf(usage).compareTo( wordModel.getUsage() );
    }
}
