package com.xpn.spellnote.services.word;

import com.xpn.spellnote.models.WordModel;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface WordsService {

    @GET( "words/{locale}.json" )
    Observable<Map<String, WordModel>> getWords(@Path("locale") String locale);
}
