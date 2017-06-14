package com.xpn.spellnote.services.dictionary;

import com.xpn.spellnote.models.DictionaryModel;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;


public interface SavedDictionaryService {

    Single<ArrayList<DictionaryModel>> getSavedDictionaries();
    Completable saveDictionary( DictionaryModel dictionary );
    Completable removeDictionary( DictionaryModel dictionary );
}
