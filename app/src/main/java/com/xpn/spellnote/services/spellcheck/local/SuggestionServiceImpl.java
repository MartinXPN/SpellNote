package com.xpn.spellnote.services.spellcheck.local;

import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.dictionary.local.WordSchema;
import com.xpn.spellnote.services.spellcheck.SuggestionService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class SuggestionServiceImpl implements SuggestionService {

    private static final int SUGGESTION_LIMIT = 100;
    private final BeanMapper<WordModel, WordSchema> wordMapper;
    private final RealmConfiguration realmConfiguration;

    public SuggestionServiceImpl(RealmConfiguration realmConfiguration, BeanMapper<WordModel, WordSchema> wordMapper) {
        this.realmConfiguration = realmConfiguration;
        this.wordMapper = wordMapper;
    }


    @Override
    public Single<List<WordModel>> getSuggestions(String word, String locale) {
        return Single.defer(() -> {
            if( locale == null || word == null || word.isEmpty() )
                return Single.just( new ArrayList<>() );

            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();

            RealmResults <WordSchema> continuations = realm.where(WordSchema.class)
                    .equalTo("locale", locale)
                    .like("word", word + '*')
                    .findAll();


            ArrayList <WordModel> result = new ArrayList<>();
            for( WordSchema wordSchema : continuations ) {
                if( !wordSchema.getWord().equals(word) )    result.add( wordMapper.mapFrom(wordSchema) );
                if( result.size() >= SUGGESTION_LIMIT )     break;
            }

            return Single.just(result);
        });
    }
}
