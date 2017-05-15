package com.xpn.spellnote.models;


public class WordModel {

    private String word;
    private Float usage;
    private String locale;
    private Boolean isUserDefined = false;

    public WordModel( String word, Float usage, String locale, Boolean isUserDefined ) {
        this.word = word;
        this.usage = usage;
        this.locale = locale;
        this.isUserDefined = isUserDefined;
    }


    public WordModel( String word, Float usage, String locale ) {
        this(word, usage, locale, false);
    }

    public String getWord() {
        return word;
    }
    public Float getUsage() {
        return usage;
    }
    public String getLocale() {
        return locale;
    }
    public Boolean getUserDefined() {
        return isUserDefined;
    }
}
