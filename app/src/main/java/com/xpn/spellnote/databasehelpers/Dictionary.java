package com.xpn.spellnote.databasehelpers;

/**
 * Created by USER on 20.02.2016.
 */
public class Dictionary {
    private static Dictionary ourInstance = new Dictionary();

    public static Dictionary getInstance() {
        return ourInstance;
    }

    private Dictionary() {
    }
}
