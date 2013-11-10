package com.micdm.remotesoundlights.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public abstract class AppVersion {

    public static String get(Context context) {
        PackageManager manager = context.getPackageManager();
        if (manager == null) {
            return null;
        }
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
