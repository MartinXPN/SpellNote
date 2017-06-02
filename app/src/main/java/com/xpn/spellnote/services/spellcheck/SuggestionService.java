package com.xpn.spellnote.services.spellcheck;

import com.xpn.spellnote.models.WordModel;

import java.util.List;

import io.reactivex.Single;


public interface SuggestionService {

    Single<List <WordModel>> getSuggestions(String word, String locale);
}
