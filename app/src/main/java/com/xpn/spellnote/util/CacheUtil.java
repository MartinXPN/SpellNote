package com.xpn.spellnote.util;


import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtil {

    private static final String SHARED_PREFS_NAME = "shared_prefs";

    public static void setCache(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString( key, value );
        editor.apply();
    }
    public static void setCache( Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt( key, value );
        editor.apply();
    }
    public static void setCache( Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean( key, value );
        editor.apply();
    }
    public static String getCache( Context context, String key, String defaultValue ) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString( key, defaultValue );
    }
    public static int getCache( Context context, String key, int defaultValue ) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getInt( key, defaultValue );
    }
    public static boolean getCache( Context context, String key, boolean defaultValue ) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean( key, defaultValue );
    }
}
