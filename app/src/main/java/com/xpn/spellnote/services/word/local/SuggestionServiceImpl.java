package com.xpn.spellnote.services.word.local;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.SpellCheckerService;
import com.xpn.spellnote.services.word.SuggestionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import timber.log.Timber;


public class SuggestionServiceImpl implements SuggestionService {

    private static final int SUGGESTION_LIMIT = 100;
    private final SpellCheckerService spellCheckerService;
    private final BeanMapper<WordModel, WordSchema> wordMapper;

    public SuggestionServiceImpl(SpellCheckerService spellCheckerService, BeanMapper<WordModel, WordSchema> wordMapper) {
        this.spellCheckerService = spellCheckerService;
        this.wordMapper = wordMapper;
    }


    @Override
    public Single<List<WordModel>> getSuggestions(String word, DictionaryModel dictionary) {

        Timber.d("Get suggestions for " + word + "!" );
        return Single.defer(() -> {
            if( dictionary == null || dictionary.getLanguageName() == null || word == null || word.isEmpty() )
                return Single.just( new ArrayList<>() );

            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .name(dictionary.getLocale() + ".realm")
                    .build();
            Realm realm = Realm.getInstance(realmConfiguration);
            realm.refresh();


            /// get continuations
            RealmResults <WordSchema> continuations = realm.where(WordSchema.class)
                    .like("word", word + '*')
                    .findAll();


            ArrayList <WordModel> continuationResult = new ArrayList<>();
            for( WordSchema wordSchema : continuations ) {
                if( !wordSchema.word.equals(word) )                 continuationResult.add( wordMapper.mapFrom(wordSchema) );
                if( continuationResult.size() >= SUGGESTION_LIMIT ) break;
            }


            /// get corrections
            ArrayList <String> corrections = editDistance(word, dictionary);
            BehaviorSubject< List<WordModel> > correctionsResult = BehaviorSubject.create();

            Observable.create((ObservableOnSubscribe<List<WordModel>>) subscriber ->
                    spellCheckerService
                            .getCorrectWords(corrections, dictionary.getLocale())
                            .subscribe(subscriber::onNext))
                    .subscribe( correctionsResult );

            /// combine results
            ArrayList <WordModel> result = new ArrayList<>(continuationResult);
            if( correctionsResult.getValue() != null )
                result.addAll(correctionsResult.getValue());

            /// sort the result in order of decreasing usage
            Collections.sort(result, (a, b) -> Integer.valueOf( b.getUsage() ).compareTo( a.getUsage() ));
            return Single.just(result);
        });
    }


    /**
     * @param s the initial string
     * @return  list of words that are different in 1 place from the initial string
     *          difference -> deletion, insertion, replacing a character
     */
    private ArrayList<String> editDistance( String s, DictionaryModel dictionary ) {

        StringBuilder now = new StringBuilder( s );

        Set<String> res = new HashSet<>();
        for( int i=0; i < s.length(); i++ ) {
            for( char c : dictionary.getAlphabet().toCharArray() ) {

                StringBuilder tmp = new StringBuilder( now );
                res.add( tmp.insert( i, c ).toString() );

                if( s.charAt(i) != c ) {
                    tmp = new StringBuilder( now );
                    tmp.setCharAt( i, c );
                    res.add( tmp.toString() );
                }
            }

            StringBuilder tmp = new StringBuilder( now );
            res.add(tmp.deleteCharAt(i).toString());
        }

        res.remove(s);
        return new ArrayList<>(res);
    }
}
