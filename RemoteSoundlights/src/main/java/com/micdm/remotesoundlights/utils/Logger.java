package com.micdm.remotesoundlights.utils;

import android.util.Log;

public class Logger {

    private static final String TAG = "com.micdm.remotesoundlights";

    public static void debug(String message, Object... args) {
        Log.d(TAG, String.format(message, args));
    }

    public static void warning(String message, Throwable e) {
        Log.w(TAG, message, e);
    }
}
