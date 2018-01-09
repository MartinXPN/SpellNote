package com.xpn.spellnote.services.word.local;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.SavedWordsService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmResults;


public class SavedWordsServiceImpl extends BaseWordService implements SavedWordsService {

    private final BeanMapper<WordModel, WordSchema> wordMapper;


    public SavedWordsServiceImpl(BeanMapper<WordModel, WordSchema> wordMapper) {
        this.wordMapper = wordMapper;
    }

    @Override
    public Single<List<WordModel>> getUserDefinedWords(String locale) {
        return Single.defer(() -> {
            Realm realm = getRealmInstance(locale);
            RealmResults<WordSchema> result = realm.where(WordSchema.class)
                    .equalTo("isUserDefined", true)
                    .findAll();

            List<WordModel> res = Stream.of(result)
                    .map(wordMapper::mapFrom)
                    .collect(Collectors.toCollection(ArrayList::new));

            realm.close();
            return Single.just(res);
        });
    }


    @Override
    public Completable saveWords(String locale, List<WordModel> words) {
        return Completable.defer(() -> Completable.fromAction(() -> {
            if( words.isEmpty() )
                return;

            Realm realmInstance = getRealmInstance(locale);
            List <WordSchema> wordSchemas = Stream.of(words)
                    .map(wordMapper::mapTo)
                    .collect(Collectors.toCollection(ArrayList::new));

            realmInstance.executeTransaction(realm -> realm.copyToRealmOrUpdate(wordSchemas));
            realmInstance.close();
        }));
    }


    @Override
    public Completable saveWord(String locale, WordModel word) {
        return Completable.defer(() -> Completable.fromAction(() -> {
            Realm realmInstance = getRealmInstance(locale);
            realmInstance.executeTransaction(realm -> realm.copyToRealmOrUpdate(wordMapper.mapTo(word)));
            realmInstance.close();
        }));
    }

    @Override
    public Completable removeWord(String locale, WordModel word) {
        return Completable.defer(() -> Completable.fromAction(() -> {
            Realm realmInstance = getRealmInstance(locale);
            WordSchema schema = realmInstance.where(WordSchema.class)
                    .equalTo("word", word.getWord())
                    .findFirst();

            if( schema != null )
                realmInstance.executeTransaction(realm -> schema.deleteFromRealm());
            realmInstance.close();
        }));
    }
}
