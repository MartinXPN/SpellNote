package com.xpn.spellnote.services.word;

import com.xpn.spellnote.models.WordModel;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface DictionaryChangeSuggestingService {

    @POST( "suggestAddWords/{locale}.json" )
    Single<WordModel> suggestAdding(@Path("locale") String locale, @Body WordModel word);


    @POST( "suggestRemoveWords/{locale}.json" )
    Single<WordModel> suggestRemoving(@Path("locale") String locale, @Body WordModel word);
}
