package com.xpn.spellchecker.databasehelpers;

/**
 * Created by USER on 20.02.2016.
 */
public class CreatedDocuments {
    private static CreatedDocuments ourInstance = new CreatedDocuments();

    public static CreatedDocuments getInstance() {
        return ourInstance;
    }

    private CreatedDocuments() {
    }
}
