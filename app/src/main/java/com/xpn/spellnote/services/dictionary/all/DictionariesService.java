package com.xpn.spellnote.services.dictionary.all;

import com.xpn.spellnote.models.DictionaryModel;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;


public interface DictionariesService {

    @GET( "languages.json" )
    Observable<Map<String, DictionaryModel>> loadAllDictionaries();
}
