package com.xpn.spellnote;

import android.app.Application;
import android.widget.Toast;

import com.xpn.spellnote.services.word.PopulateRealm;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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

        PopulateRealm.populateDatabase(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Toast.makeText(this, "Realm database created!", Toast.LENGTH_SHORT).show()
                );
    }

    public DiContext getDiContext() {
        return diContext;
    }
}
