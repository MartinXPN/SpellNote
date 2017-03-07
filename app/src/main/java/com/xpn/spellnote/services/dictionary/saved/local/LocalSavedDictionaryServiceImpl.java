package com.xpn.spellnote.services.dictionary.saved.local;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.dictionary.saved.SavedDictionaryService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


public class LocalSavedDictionaryServiceImpl implements SavedDictionaryService {

    private BeanMapper<DictionaryModel, DictionarySchema> mapper;

    public LocalSavedDictionaryServiceImpl(BeanMapper<DictionaryModel, DictionarySchema> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Single<ArrayList<DictionaryModel>> getSavedDictionaries() {
        return Single.defer(() -> {
            List<DictionarySchema> dictionaries = new Select().from(DictionarySchema.class).execute();
            return Single.just( Stream.of(dictionaries)
                    .map(dictionary -> mapper.mapFrom(dictionary))
                    .collect(Collectors.toCollection(ArrayList::new)));
        });
    }

    @Override
    public Single<ArrayList<String>> getSavedLocales() {
        return Single.defer(() -> {
            List<DictionarySchema> dictionaries = new Select().from(DictionarySchema.class).execute();
            return Single.just( Stream.of(dictionaries)
                    .map(dictionary -> dictionary.locale)
                    .collect(Collectors.toCollection(ArrayList::new)));
        });
    }

    @Override
    public Completable saveDictionaries(ArrayList<DictionaryModel> dictionaries) {
        return Completable.fromAction(() -> {
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
        });
    }
}
