package com.xpn.spellnote.services.word.local;

import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.word.SpellCheckerService;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmResults;


public class SpellCheckerServiceImpl extends BaseWordService implements SpellCheckerService {

    private final BeanMapper<WordModel, WordSchema> wordMapper;


    public SpellCheckerServiceImpl(BeanMapper<WordModel, WordSchema> wordMapper) {
        this.wordMapper = wordMapper;
    }

    private String capitalize(final String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }


    @Override
    public Single<List<WordModel>> getCorrectWords(List<String> askedWords, String locale) {
        return Single.defer(() -> {

            if( askedWords.isEmpty() )
                return Single.just( new ArrayList<WordModel>() );

            List <String> words = new ArrayList<>(askedWords);
            Set<String> requestedWords = new HashSet<>(words);

            /// handle the case of uppercase / lowercase words
            List <String> lowercaseWords = new ArrayList<>();
            for( String word : words ) {
                if( !word.isEmpty() && Character.isUpperCase(word.charAt(0)) )
                    lowercaseWords.add( word.toLowerCase(LocaleUtils.toLocale(locale)) );
            }
            words.addAll(lowercaseWords);


            Realm realm = getRealmInstance(locale);
            RealmResults <WordSchema> result = realm.where(WordSchema.class)
                    .in("word", words.toArray(new String[words.size()]))
                    .findAll();

            /// if hello is correct => Hello is correct too
            List <WordModel> ans = new ArrayList<>();
            for( WordSchema word : result ) {
                if( requestedWords.contains(word.word) )
                    ans.add( wordMapper.mapFrom(word) );

                if( Character.isLowerCase(word.word.charAt(0)) ) {
                    String capitalWord = capitalize(word.word);
                    if( requestedWords.contains(capitalWord) )
                        ans.add( new WordModel(capitalWord, word.usage, word.isUserDefined) );
                }
            }

            for( String word : requestedWords )
                if( StringUtils.isNumeric(word) )
                    ans.add(new WordModel(word, 0));

            realm.close();
            return Single.just( ans );
        });
    }
}
