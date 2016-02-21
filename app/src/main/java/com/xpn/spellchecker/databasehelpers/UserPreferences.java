package com.xpn.spellchecker.databasehelpers;

/**
 * Created by USER on 20.02.2016.
 */
public class UserPreferences {
    private static UserPreferences ourInstance = new UserPreferences();

    public static UserPreferences getInstance() {
        return ourInstance;
    }

    private UserPreferences() {
    }
}
