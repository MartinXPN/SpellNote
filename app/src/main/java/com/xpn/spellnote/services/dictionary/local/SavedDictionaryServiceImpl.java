package com.xpn.spellnote.services.dictionary.local;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.dictionary.SavedDictionaryService;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class SavedDictionaryServiceImpl implements SavedDictionaryService {

    private final BeanMapper<DictionaryModel, DictionarySchema> mapper;
    private final RealmConfiguration realmConfiguration;

    public SavedDictionaryServiceImpl(RealmConfiguration realmConfiguration, BeanMapper<DictionaryModel, DictionarySchema> mapper) {
        this.realmConfiguration = realmConfiguration;
        this.mapper = mapper;
    }

    @Override
    public Single<ArrayList<DictionaryModel>> getSavedDictionaries() {
        return Single.defer(() -> {
            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();
            RealmResults <DictionarySchema> dictionaries = realm.where(DictionarySchema.class).findAll();

            return Single.just( Stream.of(dictionaries)
                    .map(mapper::mapFrom)
                    .collect(Collectors.toCollection(ArrayList::new)));
        });
    }

    @Override
    public Completable saveDictionary(DictionaryModel dictionary) {
        return Completable.fromAction(() -> {
            Realm realmInstance = Realm.getInstance(realmConfiguration);
            realmInstance.executeTransaction(realm -> realm.insert(mapper.mapTo(dictionary)));
            realmInstance.refresh();
        });
    }
}
