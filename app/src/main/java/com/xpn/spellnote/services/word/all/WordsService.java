package com.xpn.spellnote.services.word.all;

import com.xpn.spellnote.models.WordModel;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WordsService {

    @GET( "words.json" )
    Observable<Map<String, WordModel>> getWords(@Query("locale") String locale );
}
