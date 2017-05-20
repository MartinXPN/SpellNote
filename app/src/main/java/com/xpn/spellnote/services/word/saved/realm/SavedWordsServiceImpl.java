package com.xpn.spellnote.services.word.saved.realm;

import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.saved.SavedWordsService;

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
            Realm realm = Realm.getInstance(realmConfiguration);
            realm.executeTransaction(realmInstance -> {
                        for( WordModel word : words )
                            realmInstance.copyToRealmOrUpdate( mapper.mapTo(word) );
                    });
            realm.close();
        });
    }
}
