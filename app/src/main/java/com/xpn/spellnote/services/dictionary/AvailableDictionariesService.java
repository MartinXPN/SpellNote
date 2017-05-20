package com.xpn.spellnote.services.dictionary;

import com.xpn.spellnote.models.DictionaryModel;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;


public interface AvailableDictionariesService {

    @GET( "languages.json" )
    Observable<Map<String, DictionaryModel>> getAllDictionaries();
}
