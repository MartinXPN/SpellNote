package com.xpn.spellnote.services.dictionary.local;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.dictionary.SavedDictionaryService;

import org.json.JSONArray;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import timber.log.Timber;


public class SavedDictionaryServiceImpl implements SavedDictionaryService {

    private final BeanMapper<DictionaryModel, DictionarySchema> dictionaryMapper;
    private final BeanMapper<WordModel, WordSchema> wordMapper;
    private final RealmConfiguration realmConfiguration;

    public SavedDictionaryServiceImpl(RealmConfiguration realmConfiguration,
                                      BeanMapper<DictionaryModel, DictionarySchema> dictionaryMapper,
                                      BeanMapper<WordModel, WordSchema> wordMapper) {
        this.realmConfiguration = realmConfiguration;
        this.dictionaryMapper = dictionaryMapper;
        this.wordMapper = wordMapper;
    }

    @Override
    public Single<ArrayList<DictionaryModel>> getSavedDictionaries() {
        return Single.defer(() -> {
            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();
            RealmResults <DictionarySchema> dictionaries = realm.where(DictionarySchema.class).findAll();

            return Single.just( Stream.of(dictionaries)
                    .map(dictionaryMapper::mapFrom)
                    .collect(Collectors.toCollection(ArrayList::new)));
        });
    }

    @Override
    public Completable saveDictionary(DictionaryModel dictionary, ArrayList<WordModel> words) {
        return Completable.fromAction(() -> {
            Realm realmInstance = Realm.getInstance(realmConfiguration);
            realmInstance.executeTransaction(realm -> {

                realm.copyToRealmOrUpdate(dictionaryMapper.mapTo(dictionary));
                for( WordModel word : words )
                    realm.copyToRealmOrUpdate( wordMapper.mapTo(word) );
            });
            realmInstance.refresh();
        });
    }

    @Override
    public Completable saveDictionary(DictionaryModel dictionary, JSONArray words) {
        return Completable.fromAction(() -> {
            Realm realmInstance = Realm.getInstance(realmConfiguration);
            realmInstance.executeTransaction(realm -> {

                realm.copyToRealmOrUpdate(dictionaryMapper.mapTo(dictionary));
                realm.createOrUpdateAllFromJson(WordSchema.class, words);
            });
            realmInstance.refresh();
        });
    }

    @Override
    public Completable removeDictionary(DictionaryModel dictionary) {
        return Completable.fromAction(() -> {
            Realm realmInstance = Realm.getInstance(realmConfiguration);
            realmInstance.refresh();

            realmInstance.executeTransaction(realm -> {

                RealmResults <WordSchema> tobeRemovedWords = realm.where(WordSchema.class)
                        .equalTo("locale", dictionary.getLocale())
                        .equalTo("isUserDefined", false)
                        .findAll();
                Timber.d("Found all words that need to be deleted");


                DictionarySchema tobeRemovedDictionary = realm.where(DictionarySchema.class)
                        .equalTo("locale", dictionary.getLocale())
                        .findFirst();
                Timber.d("Found the dictionary that needs to be deleted");

                tobeRemovedWords.deleteAllFromRealm();
                Timber.d("Removed words...");
                tobeRemovedDictionary.deleteFromRealm();
                Timber.d("Removed dictionary");
            });
            Timber.d("Removed all words!");
            realmInstance.refresh();
        });
    }
}
