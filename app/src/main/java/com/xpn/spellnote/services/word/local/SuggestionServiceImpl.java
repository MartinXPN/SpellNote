package com.xpn.spellnote.services.word.local;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.SpellCheckerService;
import com.xpn.spellnote.services.word.SuggestionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;


public class SuggestionServiceImpl extends BaseWordService implements SuggestionService {

    private static final int SUGGESTION_LIMIT = 100;
    private final SpellCheckerService spellCheckerService;
    private final BeanMapper<WordModel, WordSchema> wordMapper;

    public SuggestionServiceImpl(SpellCheckerService spellCheckerService, BeanMapper<WordModel, WordSchema> wordMapper) {
        this.spellCheckerService = spellCheckerService;
        this.wordMapper = wordMapper;
    }


    @Override
    public Single<List<WordModel>> getSuggestions(String word, DictionaryModel dictionary) {

        Timber.d("Get suggestions for %s!", word );
        return Single.defer(() -> {
            if( dictionary == null || dictionary.getLanguageName() == null || word == null || word.isEmpty() )
                return Single.just( new ArrayList<>() );

            /// get continuations
            Realm realm = getRealmInstance(dictionary.getLocale());
            RealmResults <WordSchema> continuations = realm.where(WordSchema.class)
                    .like("word", word + '*')
                    .findAll();


            ArrayList <WordModel> continuationResult = new ArrayList<>();
            for( WordSchema wordSchema : continuations ) {
                if( !wordSchema.word.equals(word) )                 continuationResult.add( wordMapper.mapFrom(wordSchema) );
                if( continuationResult.size() >= SUGGESTION_LIMIT ) break;
            }


            /// get corrections
            int numberOfAllowedChanges = 1;
            Map <String, Integer> corrections = editDistance(word, dictionary, numberOfAllowedChanges, new HashSet<>() );
            BehaviorSubject< List<WordModel> > correctionsResult = BehaviorSubject.create();

            Observable.create((ObservableOnSubscribe<List<WordModel>>) subscriber ->
                    spellCheckerService
                            .getCorrectWords(new ArrayList<>(corrections.keySet()), dictionary.getLocale())
                            .subscribe(subscriber::onNext))
                    .subscribe( correctionsResult );

            Timber.d("CORRECTIONS: %s", corrections );

            /// combine results
            ArrayList <WordModel> result = new ArrayList<>(continuationResult);

            /// make usage of corrections less by factor of number of changes made on the word
            if( correctionsResult.getValue() != null ) {
                List <WordModel> correction = correctionsResult.getValue();
                for( WordModel wordModel : correction ) {
                    wordModel.setUsage(wordModel.getUsage() / (numberOfAllowedChanges + 1 - corrections.get(wordModel.getWord())));
                }
                result.addAll(correction);
            }

            /// sort the result in order of decreasing usage
            Collections.sort(result, (a, b) -> Integer.compare(b.getUsage(), a.getUsage()));
            realm.close();
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

                /// Insert character at position [i]
                now.insert(i, c);
                res.add( now.toString() );
                now.deleteCharAt(i);

                if( now.charAt(i) == c )
                    continue;

                /// Replace character at position [i] with c
                char currentChar = now.charAt(i);
                now.setCharAt( i, c );
                res.add( now.toString() );
                now.setCharAt( i, currentChar );
            }

            /// Delete character at position [i]
            char currentChar = now.charAt(i);
            now.deleteCharAt(i);
            res.add(now.toString());
            now.insert(i, currentChar);

            /// Swap two neighbour characters
            if( i == s.length() - 1 )
                continue;
            swap(now, i, i+1);
            res.add(now.toString());
            swap(now, i, i+1);
        }

        res.remove(s);
        return new ArrayList<>(res);
    }


    /**
     * Finds all resulting strings by making changes to s by at most numberOfAllowedChanges times
     * one change = call ArrayList<String> editDistance( String s, DictionaryModel dictionary ) once
     * Very costly recursion
     * Try to keep numberOfAllowedChanges <= 3
     * @return Map <word, numberOfAllowedChanges>
     */
    private Map<String, Integer> editDistance(String s, DictionaryModel dictionary, int numberOfAllowedChanges, Set <String> alreadyPresentWords ) {

        Map<String, Integer> res = new HashMap<>();
        ArrayList <String> editions = editDistance(s, dictionary);
        ArrayList <String> newWords = new ArrayList<>();

        for( String word : editions ) {
            if( !alreadyPresentWords.contains(word) ) {
                res.put(word, numberOfAllowedChanges);
                newWords.add( word );
            }
        }
        alreadyPresentWords.addAll( newWords );
        Timber.d("There are %d new words(%d)", newWords.size() , numberOfAllowedChanges);

        for( String word : newWords ) {
            if( numberOfAllowedChanges > 1 )
                res.putAll( editDistance( word, dictionary, numberOfAllowedChanges - 1, alreadyPresentWords ) );
        }

        return res;
    }


    private void swap(StringBuilder s, int i, int j) {
        char tmp = s.charAt(i);
        s.setCharAt(i, s.charAt(j));
        s.setCharAt(j, tmp);
    }
}
