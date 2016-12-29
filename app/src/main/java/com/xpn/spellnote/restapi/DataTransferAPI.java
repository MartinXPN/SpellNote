package com.xpn.spellnote.restapi;


import com.xpn.spellnote.models.DictionarySchema;
import com.xpn.spellnote.models.WordSchema;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/// All endpoints are defined here
public interface DataTransferAPI {

    /// base url of all requests
    String BASE_URL = "https://spellnote.herokuapp.com";


    @GET( "/dictionaries" )
    Call<ArrayList<DictionarySchema>> getAllDictionaries();

    @GET( "/getWords" )
    Call <ArrayList <WordSchema> > getWords( @Query("locales") ArrayList <String> locales );
}
