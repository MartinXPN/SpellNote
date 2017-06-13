package com.xpn.spellnote;

import android.app.Application;

import com.xpn.spellnote.services.word.PopulateRealm;

import timber.log.Timber;


public class SpellNoteApp extends Application {

    private DiContext diContext;


    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());   // Initialize logger
        diContext = new DiContext(this);        // Initialize app context

        PopulateRealm.populateDatabase(this);
    }

    public DiContext getDiContext() {
        return diContext;
    }
}
