package com.xpn.spellnote.services.word;

import com.xpn.spellnote.models.WordModel;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface AddWordSuggestionService {

    @POST( "suggestedWords/{locale}.json" )
    Observable<WordModel> suggestAdding(@Path("locale") String locale, WordModel word);
}
