package com.xpn.spellnote.services.spellcheck.local;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.dictionary.local.WordSchema;
import com.xpn.spellnote.services.spellcheck.SpellCheckerService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class SpellCheckerServiceImpl implements SpellCheckerService {

    private final BeanMapper<WordModel, WordSchema> wordMapper;
    private final RealmConfiguration realmConfiguration;


    public SpellCheckerServiceImpl(RealmConfiguration realmConfiguration, BeanMapper<WordModel, WordSchema> wordMapper) {
        this.realmConfiguration = realmConfiguration;
        this.wordMapper = wordMapper;
    }


    @Override
    public Single<Boolean> isWordCorrect(String word, String locale) {
        return Single.defer(() -> {
            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();

            return Single.just( realm.where(WordSchema.class)
                    .equalTo("locale", locale)
                    .equalTo("word", word)
                    .findFirst() != null);
        });
    }

    @Override
    public Single<List<WordModel>> getCorrectWords(List<String> words, String locale) {
        return Single.defer(() -> {
            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();

            RealmResults <WordSchema> result = realm.where(WordSchema.class)
                    .equalTo("locale", locale)
                    .in("word", words.toArray(new String[words.size()]))
                    .findAll();

            return Single.just( Stream.of(result)
                    .map(wordMapper::mapFrom)
                    .collect(Collectors.toCollection(ArrayList::new)));
        });
    }
}
