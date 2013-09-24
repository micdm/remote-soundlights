package com.micdm.remotesoundlights.utils;

import android.app.Activity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.Map;

public abstract class AnalyticsTracker {

    private static EasyTracker getTracker(Activity activity) {
        return EasyTracker.getInstance(activity);
    }

    public static void sendActivityStart(Activity activity) {
        getTracker(activity).activityStart(activity);
    }

    public static void sendActivityStop(Activity activity) {
        getTracker(activity).activityStop(activity);
    }

    public static void sendEvent(Activity activity, String category, String action, String label) {
        Map<String, String> params = MapBuilder.createEvent(category, action, label, null).build();
        getTracker(activity).send(params);
    }
}
