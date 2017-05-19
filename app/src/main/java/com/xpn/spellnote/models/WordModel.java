package com.xpn.spellnote.models;


public class WordModel {

    private String word;
    private int usage;
    private String locale;
    private boolean isUserDefined = false;

    public WordModel( String word, int usage, String locale, boolean isUserDefined ) {
        this.word = word;
        this.usage = usage;
        this.locale = locale;
        this.isUserDefined = isUserDefined;
    }


    public WordModel( String word, int usage, String locale ) {
        this(word, usage, locale, false);
    }

    public String getWord() {
        return word;
    }
    public int getUsage() {
        return usage;
    }
    public String getLocale() {
        return locale;
    }
    public boolean getUserDefined() {
        return isUserDefined;
    }
}
