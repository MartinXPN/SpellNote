package com.xpn.spellnote.services.word.local;

import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.SuggestionService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class SuggestionServiceImpl implements SuggestionService {

    private static final int SUGGESTION_LIMIT = 100;
    private final BeanMapper<WordModel, WordSchema> wordMapper;

    public SuggestionServiceImpl(BeanMapper<WordModel, WordSchema> wordMapper) {
        this.wordMapper = wordMapper;
    }


    @Override
    public Single<List<WordModel>> getSuggestions(String word, String locale) {
        return Single.defer(() -> {
            if( locale == null || word == null || word.isEmpty() )
                return Single.just( new ArrayList<>() );

            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .name(locale + ".realm")
                    .build();
            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();

            RealmResults <WordSchema> continuations = realm.where(WordSchema.class)
                    .like("word", word + '*')
                    .findAll();


            ArrayList <WordModel> result = new ArrayList<>();
            for( WordSchema wordSchema : continuations ) {
                if( !wordSchema.word.equals(word) )         result.add( wordMapper.mapFrom(wordSchema) );
                if( result.size() >= SUGGESTION_LIMIT )     break;
            }

            return Single.just(result);
        });
    }
}
