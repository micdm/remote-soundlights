package com.micdm.remotesoundlights.notifiers;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.micdm.remotesoundlights.R;
import com.micdm.remotesoundlights.utils.AnalyticsTracker;
import com.micdm.remotesoundlights.utils.Logger;

public abstract class RateNotifier extends Notifier {

    private static final String PREF_KEY_RUN_COUNT = "run_count";
    private static final String PREF_KEY_IS_HIDDEN = "rate_message";
    private static final int RUN_COUNT_TO_MESSAGE = 5;

    public static void update(Context context) {
        increaseRunCount(context);
    }

    private static void increaseRunCount(Context context) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_KEY_RUN_COUNT, getRunCount(context) + 1);
        editor.commit();
    }

    public static boolean show(Context context) {
        if (!needShow(context)) {
            return false;
        }
        AlertDialog dialog = getDialog(context);
        dialog.show();
        return true;
    }

    private static boolean needShow(Context context) {
        if (isHiddenByUser(context, PREF_KEY_IS_HIDDEN)) {
            return false;
        }
        int count = getRunCount(context);
        return count != 0 && count % RUN_COUNT_TO_MESSAGE == 0;
    }


    private static int getRunCount(Context context) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getInt(PREF_KEY_RUN_COUNT, 0);
    }

    private static AlertDialog getDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.rate_message);
        builder.setPositiveButton(R.string.rate_message_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse(String.format("market://details?id=%s", context.getPackageName()));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    context.startActivity(intent);
                    setHiddenByUser(context, PREF_KEY_IS_HIDDEN);
                } catch (ActivityNotFoundException e) {
                    Logger.warning("No activity found", e);
                }
                AnalyticsTracker.sendEvent(context, "rate", "select", "yes");
            }
        });
        builder.setNeutralButton(R.string.rate_message_later, null);
        builder.setNegativeButton(R.string.rate_message_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setHiddenByUser(context, PREF_KEY_IS_HIDDEN);
                AnalyticsTracker.sendEvent(context, "rate", "select", "no");
            }
        });
        return builder.create();
    }
}
