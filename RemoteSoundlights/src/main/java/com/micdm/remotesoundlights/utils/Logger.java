package com.micdm.remotesoundlights.utils;

import android.util.Log;

public class Logger {

    private static final String TAG = "com.micdm.blueflash";

    public static void debug(String message) {
        Log.d(TAG, message);
    }

    public static void warning(String message, Throwable e) {
        Log.w(TAG, message, e);
    }
}
