package com.xpn.spellnote.databasehelpers;


public class UserPreferences {
    private static UserPreferences ourInstance = new UserPreferences();

    public static UserPreferences getInstance() {
        return ourInstance;
    }

    private UserPreferences() {
    }
}
