package com.micdm.remotesoundlights.notifiers;

import android.content.Context;
import android.content.SharedPreferences;

abstract class Notifier {

    private static final String PREF_NAME = "notifiers";

    protected static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    protected static boolean isHiddenByUser(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    protected static void setHiddenByUser(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, true);
        editor.commit();
    }
}
