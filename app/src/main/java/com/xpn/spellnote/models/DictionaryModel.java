package com.xpn.spellnote.models;


public class DictionaryModel {

    private String locale;
    private String languageName;
    private String logoURL;
    private Integer version;

    public DictionaryModel(String locale, String languageName, String logoURL, Integer version) {
        this.locale = locale;
        this.languageName = languageName;
        this.logoURL = logoURL;
        this.version = version;
    }


    public String getLocale() {
        return locale;
    }
    public String getLanguageName() {
        return languageName;
    }
    public String getLogoURL() {
        return logoURL;
    }
    public Integer getVersion() {
        return version;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof DictionaryModel &&
                ((DictionaryModel) obj).getLocale().equals(getLocale()) &&
                ((DictionaryModel) obj).getLanguageName().equals(getLanguageName());
    }
}
