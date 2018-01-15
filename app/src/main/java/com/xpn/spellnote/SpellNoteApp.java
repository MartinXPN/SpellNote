package com.xpn.spellnote;

import android.app.Application;

import io.realm.Realm;
import timber.log.Timber;


public class SpellNoteApp extends Application {

    private DiContext diContext;


    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);               // Initialize Realm
        Timber.plant(new Timber.DebugTree());   // Initialize logger
        diContext = new DiContext(this);        // Initialize app context
    }

    public DiContext getDiContext() {
        return diContext;
    }
}
