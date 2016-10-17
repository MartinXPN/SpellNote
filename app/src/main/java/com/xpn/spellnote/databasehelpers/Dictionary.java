package com.xpn.spellnote.databasehelpers;

import com.xpn.spellnote.databasemodels.LanguageSchema;

import org.greenrobot.eventbus.Subscribe;


public class Dictionary {
    private static LanguageSchema currentLanguage;
    private static Dictionary ourInstance = new Dictionary();

    public static Dictionary getInstance() {
        return ourInstance;
    }
    private Dictionary() {}


    @Subscribe
    public static void setCurrentLanguage(LanguageSchema languageSchema) {
        if( languageSchema == currentLanguage )
            return;
        currentLanguage = languageSchema;
    }

    public static LanguageSchema getCurrentLanguage() {
        return currentLanguage;
    }
}
