package com.xpn.spellnote.services.spellcheck;

import com.xpn.spellnote.models.WordModel;

import java.util.List;


public interface SuggestionService {

    List <WordModel> getSuggestions(String word, String locale);
}
