package com.xpn.spellnote.services.dictionary;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.WordModel;

import org.json.JSONArray;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;


public interface SavedDictionaryService {

    Single<ArrayList<DictionaryModel>> getSavedDictionaries();
    Completable saveDictionary( DictionaryModel dictionary, ArrayList <WordModel> words );
    Completable saveDictionary( DictionaryModel dictionary, JSONArray words );
    Completable removeDictionary( DictionaryModel dictionary );
}
