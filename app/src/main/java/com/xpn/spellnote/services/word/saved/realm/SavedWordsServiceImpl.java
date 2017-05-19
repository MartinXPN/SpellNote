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

    public SavedWordsServiceImpl(BeanMapper<WordModel, WordSchema> mapper) {
        this.mapper = mapper;
    }


    @Override
    public Completable saveAllWords(ArrayList<WordModel> words) {
        return Completable.fromAction(() -> Realm
                .getInstance( new RealmConfiguration.Builder().build() )
                .executeTransaction(realm -> {
                    for( WordModel word : words )
                        realm.copyToRealmOrUpdate( mapper.mapTo(word) );
                }));
    }
}
