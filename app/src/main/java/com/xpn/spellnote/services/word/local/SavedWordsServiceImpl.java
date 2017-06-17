package com.xpn.spellnote.services.word.local;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.SavedWordsService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import timber.log.Timber;


public class SavedWordsServiceImpl implements SavedWordsService {

    private final BeanMapper<WordModel, WordSchema> wordMapper;


    public SavedWordsServiceImpl(BeanMapper<WordModel, WordSchema> wordMapper) {
        this.wordMapper = wordMapper;
    }

    @Override
    public Single<List<WordModel>> getUserDefinedWords(String locale) {
        return Single.defer(() -> {
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .name(locale + ".realm")
                    .build();

            Timber.d("Opening database at: " + realmConfiguration.getPath());

            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();

            RealmResults<WordSchema> result = realm.where(WordSchema.class)
                    .equalTo("isUserDefined", true)
                    .findAll();

            return Single.just( Stream.of(result)
                    .map(wordMapper::mapFrom)
                    .collect(Collectors.toCollection(ArrayList::new)));
        });
    }
}
