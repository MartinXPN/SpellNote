package com.xpn.spellnote.services.word.local;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


class BaseWordService {


    Realm getRealmInstance(String locale) {

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(locale + ".realm")
                .build();

        Timber.d("Opening database at: %s", realmConfiguration.getPath());
        Realm realm = Realm.getInstance(realmConfiguration);
        realm.refresh();
        return realm;
    }
}
