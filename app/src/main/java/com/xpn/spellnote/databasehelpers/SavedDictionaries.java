package com.xpn.spellnote.databasehelpers;


import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.xpn.spellnote.entities.dictionary.DictionaryModel;
import com.xpn.spellnote.entities.SavedDictionarySchema;

import java.util.ArrayList;
import java.util.List;

public class SavedDictionaries {

    public static List<SavedDictionarySchema> getSavedDictionaries() {
        return new Select()
                .from(SavedDictionarySchema.class)
                .execute();
    }

    public static ArrayList<String> getSavedLocales() {
        List <SavedDictionarySchema> dictionaries = getSavedDictionaries();
        ArrayList <String> locales = new ArrayList<>();
        for( SavedDictionarySchema dictionary : dictionaries ) {
            locales.add( dictionary.locale );
        }

        return locales;
    }

    public static void addDictionaries(ArrayList <SavedDictionarySchema> dictionaries ) {
        ActiveAndroid.beginTransaction();
        try {
            for (SavedDictionarySchema dictionary : dictionaries ) {
                dictionary.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static void saveDictionaries(ArrayList <DictionaryModel> dictionaries ) {
        ActiveAndroid.beginTransaction();
        try {
            for (DictionaryModel dictionary : dictionaries ) {
                SavedDictionarySchema schema = new SavedDictionarySchema( dictionary );
                schema.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }
}
