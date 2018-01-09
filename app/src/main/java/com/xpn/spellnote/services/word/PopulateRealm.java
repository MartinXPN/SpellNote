package com.xpn.spellnote.services.word;

import android.content.Context;
import android.os.Environment;

import com.xpn.spellnote.services.word.local.WordSchema;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Completable;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


/**
 * A temporary class to create separate database files for every locale separately
 * This class is not going to appear in production or be merged with master
 * du -ks * (in adb shell to get size of all files in KB)
 *
 * Operations that we need to perform to get database from app to local file storage
 * adb pull sdcard/[LOCALE].realm ./
 */
public class PopulateRealm {

    private static final String JSON_FILE = "ru-realm.json";
    private static final String OUTPUT_DATABASE = "ru.realm";

    private static String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open(JSON_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static Completable populateDatabase(Context context) {

        return Completable.fromAction(() -> {

            JSONArray words = new JSONArray(loadJSONFromAsset(context));
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .directory(Environment.getExternalStorageDirectory())
                    .name(OUTPUT_DATABASE)
                    .build();

            /// delete database if there is a file created during previous sessions
            File file = new File(Environment.getExternalStorageDirectory() + "/" + OUTPUT_DATABASE );
            file.delete();

            Realm realmInstance = Realm.getInstance(realmConfiguration);
            realmInstance.executeTransaction(realm -> realm.createOrUpdateAllFromJson(WordSchema.class, words));
            realmInstance.close();
            Realm.compactRealm(realmConfiguration);

            Timber.d("Realm database is now at location: %s", realmConfiguration.getPath());
        });
    }
}
