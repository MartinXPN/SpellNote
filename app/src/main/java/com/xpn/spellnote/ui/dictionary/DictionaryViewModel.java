package com.xpn.spellnote.ui.dictionary;


import android.databinding.BaseObservable;

import com.xpn.spellnote.models.DictionaryModel;

public class DictionaryViewModel extends BaseObservable {

    private DictionaryModel dictionary;

    public DictionaryViewModel( DictionaryModel dictionary ) {
        this.dictionary = dictionary;
    }

    public String getLogoURL() {
        return dictionary.getLogoURL();
    }
    public String getLanguageName() {
        return dictionary.getLanguageName();
    }
}
