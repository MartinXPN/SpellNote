package com.xpn.spellnote.services.word.local;

import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.SavedWordsService;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class SavedWordsServiceImpl implements SavedWordsService {

    private final BeanMapper<WordModel, WordSchema> mapper;
    private final RealmConfiguration realmConfiguration;

    public SavedWordsServiceImpl(RealmConfiguration realmConfiguration, BeanMapper<WordModel, WordSchema> mapper) {
        this.realmConfiguration = realmConfiguration;
        this.mapper = mapper;
    }


    @Override
    public Completable saveAllWords(ArrayList<WordModel> words) {
        return Completable.fromAction(() -> {
            Realm realmInstance = Realm.getInstance(realmConfiguration);
            realmInstance.executeTransaction(realm -> {
                for( WordModel word : words )
                    realm.copyToRealmOrUpdate( mapper.mapTo(word) );
            });
            realmInstance.refresh();
        });
    }
}
