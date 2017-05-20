package com.xpn.spellnote.services.dictionary.saved;

import com.xpn.spellnote.models.DictionaryModel;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;


public interface SavedDictionaryService {

    Single<ArrayList<DictionaryModel>> getSavedDictionaries();
    Completable saveDictionary( DictionaryModel dictionary );
}
