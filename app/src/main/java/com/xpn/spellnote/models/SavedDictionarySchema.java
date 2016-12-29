package com.xpn.spellnote.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table( name = "SavedDictionarySchema" )
public class SavedDictionarySchema extends Model {

    @Column( index = true, unique = true ) public String locale;
    @Column public String languageName;
    @Column public String logoURL;
    @Column public Integer version;

    public SavedDictionarySchema() {
        super();
    }

    public SavedDictionarySchema( DictionarySchema dictionary ) {
        super();
        locale = dictionary.locale;
        languageName = dictionary.languageName;
        logoURL = dictionary.logoURL;
        version = dictionary.version;
    }
}
