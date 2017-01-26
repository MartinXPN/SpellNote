package com.xpn.spellnote.services;


import android.util.Log;

import com.xpn.spellnote.entities.dictionary.DictionaryModel;
import com.xpn.spellnote.restapi.DataTransferAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DictionaryGetterService {

    public static void loadDictionaries() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( DataTransferAPI.BASE_URL )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();


        DataTransferAPI api = retrofit.create(DataTransferAPI.class);
        api.getAllDictionaries().enqueue(new Callback<ArrayList<DictionaryModel>>() {
            @Override
            public void onResponse(Call<ArrayList<DictionaryModel>> call, Response<ArrayList<DictionaryModel>> response) {

                Log.d( "Response", String.valueOf(response.body().get( 0 ).getLanguageName()));
                if( response.body() == null )
                    return;
                EventBus.getDefault().post( response.body() );
            }

            @Override
            public void onFailure(Call<ArrayList<DictionaryModel>> call, Throwable t) {
                Log.d( "Failure", "failed to get all dictionaries" );
            }
        });
    }
}
