package com.xpn.spellnote.restapi;


import com.xpn.spellnote.entities.dictionary.DictionaryModel;
import com.xpn.spellnote.entities.word.WordModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/// All endpoints are defined here
public interface DataTransferAPI {

    /// base url of all requests
    String BASE_URL = "https://spellnote.herokuapp.com";


    @GET( "/dictionaries" )
    Call<ArrayList<DictionaryModel>> getAllDictionaries();

    @GET( "/getWords" )
    Call <ArrayList <WordModel> > getWords(@Query("locales") ArrayList <String> locales );
}
