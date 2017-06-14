package com.xpn.spellnote.services.dictionary.local;

import com.xpn.spellnote.models.DictionaryModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class DictionarySchema extends RealmObject {

    @PrimaryKey String locale;
    @Required String languageName;
    @Required String logoURL;
    @Required String downloadURL;
    @Required String alphabet;
    @Required Integer version;

    public DictionarySchema() {
        super();
    }


    private DictionarySchema(String locale, String languageName, String logoURL, Integer version, String alphabet, String downloadURL) {
        super();
        this.locale = locale;
        this.languageName = languageName;
        this.logoURL = logoURL;
        this.version = version;
        this.alphabet = alphabet;
        this.downloadURL = downloadURL;
    }

    DictionarySchema(DictionaryModel model) {
        this(model.getLocale(), model.getLanguageName(), model.getLogoURL(), model.getVersion(), model.getAlphabet(), model.getDownloadURL());
    }
}
