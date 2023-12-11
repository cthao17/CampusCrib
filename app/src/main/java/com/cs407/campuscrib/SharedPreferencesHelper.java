package com.cs407.campuscrib;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "MyPrefsFile";

    public static void setFavoriteStatus(Context context, String listingId, boolean isFavorite) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(listingId, isFavorite);
        editor.apply();
    }

    public static boolean getFavoriteStatus(Context context, String listingId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(listingId, false);
    }
}

