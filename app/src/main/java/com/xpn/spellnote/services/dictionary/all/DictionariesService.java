package com.xpn.spellnote.services.dictionary.all;

import com.xpn.spellnote.models.DictionaryModel;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;


public interface DictionariesService {

    @GET( "dictionaries" )
    Observable<ArrayList<DictionaryModel>> loadAllDictionaries();
}
