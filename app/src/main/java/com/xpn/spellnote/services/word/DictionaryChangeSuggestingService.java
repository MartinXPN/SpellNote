package com.xpn.spellnote.services.word;

import com.xpn.spellnote.models.WordModel;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface DictionaryChangeSuggestingService {

    @POST( "suggestAddWords/{locale}.json" )
    Observable<WordModel> suggestAdding(@Path("locale") String locale, WordModel word);


    @POST( "suggestRemoveWords/{locale}.json" )
    Observable<WordModel> suggestRemoving(@Path("locale") String locale, WordModel word);
}
