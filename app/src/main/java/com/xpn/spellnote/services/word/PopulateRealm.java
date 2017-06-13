package com.xpn.spellnote.services.word;

import android.content.Context;
import android.widget.Toast;

import com.xpn.spellnote.services.dictionary.local.WordSchema;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


/**
 * A temporary class to create separate database files for every locale separately
 * This class is not going to appear in production or be merged with master
 */
public class PopulateRealm {

    private static final String JSON_FILE = "en-realm.json";
    private static final String OUTPUT_DATABASE = "en.realm";

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

    public static void populateDatabase(Context context) {

        JSONArray words;
        try {
            words = new JSONArray(loadJSONFromAsset(context));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(OUTPUT_DATABASE)
                .build();

        Realm realmInstance = Realm.getInstance(realmConfiguration);
        realmInstance.executeTransaction(realm -> realm.createOrUpdateAllFromJson(WordSchema.class, words));
        realmInstance.close();

        Toast.makeText(context, "Realm database created!", Toast.LENGTH_SHORT).show();
        Timber.d("Realm database is now at location: " + realmConfiguration.getPath());
    }
}
