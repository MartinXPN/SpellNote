package com.xpn.spellnote;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import io.reactivex.plugins.RxJavaPlugins;
import io.realm.Realm;
import timber.log.Timber;


public class SpellNoteApp extends Application {

    private DiContext diContext;


    @Override
    public void onCreate() {
        super.onCreate();

        if( !BuildConfig.DEBUG ) {
            Fabric.with(this, new Crashlytics());
        }
        RxJavaPlugins.setErrorHandler(e -> { });// Initialize RxJava error handling
        Realm.init(this);               // Initialize Realm
        Timber.plant(new Timber.DebugTree());   // Initialize logger
        diContext = new DiContext(this);        // Initialize app context
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public DiContext getDiContext() {
        return diContext;
    }
}
