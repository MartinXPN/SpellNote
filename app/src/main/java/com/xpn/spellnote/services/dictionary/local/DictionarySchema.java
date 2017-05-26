package com.xpn.spellnote.services.dictionary.local;

import com.xpn.spellnote.models.DictionaryModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class DictionarySchema extends RealmObject {

    @PrimaryKey @Required String locale;
    @Required String languageName;
    @Required String logoURL;
    Integer version;

    public DictionarySchema() {
        super();
    }


    public DictionarySchema( String locale, String languageName, String logoURL, Integer version ) {
        super();
        this.locale = locale;
        this.languageName = languageName;
        this.logoURL = logoURL;
        this.version = version;
    }

    public DictionarySchema(DictionaryModel model) {
        this(model.getLocale(), model.getLanguageName(), model.getLogoURL(), model.getVersion());
    }
}
