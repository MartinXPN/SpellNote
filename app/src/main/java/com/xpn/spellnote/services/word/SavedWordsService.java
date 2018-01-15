package com.xpn.spellnote.services.word;

import com.xpn.spellnote.models.WordModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


public interface SavedWordsService {

    Single<List<WordModel>> getUserDefinedWords(String locale);
    Completable saveWords(String locale, List<WordModel> words);
    Completable saveWord(String locale, WordModel word);
    Completable removeWord(String locale, WordModel word);
}
