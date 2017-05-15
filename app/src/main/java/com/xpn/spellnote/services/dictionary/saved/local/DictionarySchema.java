package com.xpn.spellnote.services.dictionary.saved.local;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table( name = "SavedDictionaries" )
public class DictionarySchema extends Model {

    @Column( index = true, unique = true ) String locale;
    @Column String languageName;
    @Column String logoURL;
    @Column Integer version;


    public DictionarySchema() {
        super();
    }

    public DictionarySchema( String locale,
                             String languageName,
                             String logoURL,
                             Integer version ) {
        super();
        this.locale = locale;
        this.languageName = languageName;
        this.logoURL = logoURL;
        this.version = version;
    }
}
