package com.micdm.remotesoundlights.utils;

import android.app.Activity;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.Map;

public abstract class AnalyticsTracker {

    private static EasyTracker getTracker(Context context) {
        return EasyTracker.getInstance(context);
    }

    public static void sendActivityStart(Activity activity) {
        getTracker(activity).activityStart(activity);
    }

    public static void sendActivityStop(Activity activity) {
        getTracker(activity).activityStop(activity);
    }

    public static void sendEvent(Context context, String category, String action, String label) {
        Map<String, String> params = MapBuilder.createEvent(category, action, label, null).build();
        getTracker(context).send(params);
    }
}
