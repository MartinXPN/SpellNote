package com.xpn.spellnote.models;


public class WordModel {

    private String word;
    private Integer usage;
    private String locale;
    private Boolean isUserDefined = false;

    public WordModel( String word,
                      Integer usage,
                      String locale,
                      Boolean isUserDefined ) {
        this.word = word;
        this.usage = usage;
        this.locale = locale;
        this.isUserDefined = isUserDefined;
    }

    public String getWord() {
        return word;
    }
    public Integer getUsage() {
        return usage;
    }
    public String getLocale() {
        return locale;
    }
    public Boolean getUserDefined() {
        return isUserDefined;
    }
}
