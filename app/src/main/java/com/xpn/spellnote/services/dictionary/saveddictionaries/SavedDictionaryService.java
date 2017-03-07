package com.xpn.spellnote.services.dictionary.saveddictionaries;

import com.xpn.spellnote.models.DictionaryModel;

import java.util.ArrayList;


public interface SavedDictionaryService {

    ArrayList<DictionaryModel> getSavedDictionaries();
    ArrayList<String> getSavedLocales();
    void saveDictionaries(ArrayList <DictionaryModel> dictionaries );
}
