package com.xpn.spellnote.models;


public class DictionaryModel {

    private String locale;
    private String languageName;
    private String logoURL;
    private Integer version;
    private String alphabet;
    private String downloadURL;

    public DictionaryModel(String locale, String languageName, String logoURL, Integer version, String alphabet, String downloadURL) {
        this.locale = locale;
        this.languageName = languageName;
        this.logoURL = logoURL;
        this.version = version;
        this.alphabet = alphabet;
        this.downloadURL = downloadURL;
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
    public String getAlphabet() {
        return alphabet;
    }
    public String getDownloadURL() {
        return downloadURL;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof DictionaryModel &&
                ((DictionaryModel) obj).getLocale().equals(getLocale()) &&
                ((DictionaryModel) obj).getLanguageName().equals(getLanguageName());
    }

    @Override
    public String toString() {
        return locale + '\n' +
                languageName + '\n' +
                logoURL + '\n' +
                version + '\n' +
                alphabet + '\n' +
                downloadURL;
    }
}
