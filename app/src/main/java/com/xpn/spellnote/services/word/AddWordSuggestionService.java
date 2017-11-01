package com.xpn.spellnote.services.word;

import com.xpn.spellnote.models.WordModel;

import io.reactivex.Observable;
import retrofit2.http.POST;


public interface AddWordSuggestionService {

    @POST( "suggestedWords.json" )
    Observable<WordModel> suggestAdding(WordModel word);
}
