package com.xpn.spellnote.models;


public class DictionaryModel {

    private Long id;
    private String locale;
    private String languageName;
    private String logoURL;
    private Integer version;
    private int chunks;

    public DictionaryModel(Long id, String locale, String languageName, String logoURL, Integer version ) {
        this(id, locale, languageName, logoURL, version, 1);
    }

    public DictionaryModel(Long id, String locale, String languageName, String logoURL, Integer version, int chunks) {
        this.id = id;
        this.locale = locale;
        this.languageName = languageName;
        this.logoURL = logoURL;
        this.version = version;
        this.chunks = chunks;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Integer getChunks() {
        return chunks;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof DictionaryModel &&
                ((DictionaryModel) obj).getLocale().equals(getLocale()) &&
                ((DictionaryModel) obj).getLanguageName().equals(getLanguageName());
    }
}
