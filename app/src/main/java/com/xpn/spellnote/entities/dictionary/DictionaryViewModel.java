package com.xpn.spellnote.entities.dictionary;


import android.databinding.BaseObservable;

import com.xpn.spellnote.entities.ViewModelLifecycle;

public class DictionaryViewModel extends BaseObservable implements ViewModelLifecycle {

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

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }
}
