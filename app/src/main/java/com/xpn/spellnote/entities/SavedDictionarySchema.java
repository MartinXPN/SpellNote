package com.xpn.spellnote.entities;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.xpn.spellnote.entities.dictionary.DictionaryModel;

@Table( name = "SavedDictionarySchema" )
public class SavedDictionarySchema extends Model {

    @Column( index = true, unique = true ) public String locale;
    @Column public String languageName;
    @Column public String logoURL;
    @Column public Integer version;

    public SavedDictionarySchema() {
        super();
    }

    public SavedDictionarySchema( DictionaryModel dictionary ) {
        super();
        locale = dictionary.getLocale();
        languageName = dictionary.getLanguageName();
        logoURL = dictionary.getLogoURL();
        version = dictionary.getVersion();
    }
}
