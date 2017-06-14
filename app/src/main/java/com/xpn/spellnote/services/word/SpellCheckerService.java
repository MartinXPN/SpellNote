package com.xpn.spellnote.services.word;

import com.xpn.spellnote.models.WordModel;

import java.util.List;

import io.reactivex.Single;


public interface SpellCheckerService {

    Single<Boolean> isWordCorrect(String word, String locale);
    Single<List<WordModel>> getCorrectWords(List <String> words, String locale);
}
