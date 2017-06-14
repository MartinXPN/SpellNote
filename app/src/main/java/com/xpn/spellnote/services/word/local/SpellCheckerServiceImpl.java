package com.xpn.spellnote.services.word.local;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.SpellCheckerService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import timber.log.Timber;


public class SpellCheckerServiceImpl implements SpellCheckerService {

    private final BeanMapper<WordModel, WordSchema> wordMapper;


    public SpellCheckerServiceImpl(BeanMapper<WordModel, WordSchema> wordMapper) {
        this.wordMapper = wordMapper;
    }


    @Override
    public Single<Boolean> isWordCorrect(String word, String locale) {
        return Single.defer(() -> {
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .name(locale + ".realm")
                    .build();

            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();

            return Single.just( realm.where(WordSchema.class)
                    .equalTo("word", word)
                    .findFirst() != null);
        });
    }

    @Override
    public Single<List<WordModel>> getCorrectWords(List<String> words, String locale) {
        return Single.defer(() -> {
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .name(locale + ".realm")
                    .build();

            Timber.d( "Opening database at: " + realmConfiguration.getPath() );

            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();

            RealmResults <WordSchema> result = realm.where(WordSchema.class)
                    .in("word", words.toArray(new String[words.size()]))
                    .findAll();

            return Single.just( Stream.of(result)
                    .map(wordMapper::mapFrom)
                    .collect(Collectors.toCollection(ArrayList::new)));
        });
    }
}
