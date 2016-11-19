package com.xpn.spellnote.restapi;


import com.xpn.spellnote.models.LanguageSchema;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/// All endpoints are defined here
public interface DataTransferAPI {

    /// base url of all requests
    String BASE_URL = "http://...";


    @GET( "/getAllLanguages" )
    Call<ArrayList<LanguageSchema>> getAllLanguages();
}
