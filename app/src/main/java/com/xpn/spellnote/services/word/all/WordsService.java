package com.xpn.spellnote.services.word.all;

import com.xpn.spellnote.models.WordModel;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WordsService {

    @GET( "/getWords" )
    Observable<ArrayList <WordModel> > getWords(@Query("locales") ArrayList <String> locales );
}
