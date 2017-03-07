package com.xpn.spellnote.services.dictionary.saveddictionaries.local;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.dictionary.saveddictionaries.SavedDictionaryService;

import java.util.ArrayList;
import java.util.List;


public class LocalSavedDictionaryServiceImpl implements SavedDictionaryService {

    private BeanMapper<DictionaryModel, DictionarySchema> mapper;

    public LocalSavedDictionaryServiceImpl(DictionaryMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ArrayList<DictionaryModel> getSavedDictionaries() {
        List<DictionarySchema> dictionaries = new Select().from(DictionarySchema.class).execute();

        ArrayList <DictionaryModel> res = new ArrayList<>();
        for( DictionarySchema dictionary : dictionaries ) {
            res.add(mapper.mapFrom(dictionary));
        }
        return res;
    }

    @Override
    public ArrayList<String> getSavedLocales() {
        List <DictionaryModel> dictionaries = getSavedDictionaries();
        ArrayList <String> locales = new ArrayList<>();
        for( DictionaryModel dictionary : dictionaries ) {
            locales.add( dictionary.getLocale() );
        }

        return locales;
    }

    @Override
    public void saveDictionaries(ArrayList<DictionaryModel> dictionaries) {
        ActiveAndroid.beginTransaction();
        try {
            for (DictionaryModel dictionary : dictionaries ) {
                if( dictionary.getId() != -1 ) {
                    new Delete().from(DictionarySchema.class).where("id = ?", dictionary.getId()).executeSingle();
                }
                DictionarySchema schema = mapper.mapTo(dictionary);
                schema.save();
                dictionary.setId(schema.getId());
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }
}
