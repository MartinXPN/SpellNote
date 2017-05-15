package com.xpn.spellnote;

import com.activeandroid.app.Application;

import timber.log.Timber;


public class SpellNoteApp extends Application {

    private DiContext diContext;


    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());   // Initialize logger
        diContext = new DiContext(this);        // Initialize app context
    }

    public DiContext getDiContext() {
        return diContext;
    }
}
