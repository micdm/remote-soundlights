package com.micdm.remotesoundlights.utils;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.micdm.remotesoundlights.R;

public class RateMessage {

    private static final String PREF_NAME = "rate_message";
    private static final String PREF_KEY_RUN_COUNT = "run_count";
    private static final String PREF_KEY_IS_HIDDEN = "is_hidden";
    private static final int RUN_COUNT_TO_MESSAGE = 5;

    private Context context;

    public RateMessage(Context context) {
        this.context = context;
    }

    private boolean isHiddenByUser() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_KEY_IS_HIDDEN, false);
    }

    private void setHiddenByUser() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_KEY_IS_HIDDEN, true);
        editor.commit();
    }

    private int getRunCount() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_KEY_RUN_COUNT, 0);
    }

    private void increaseRunCount() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_KEY_RUN_COUNT, getRunCount() + 1);
        editor.commit();
    }

    private AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.rate_message);
        builder.setPositiveButton(R.string.rate_message_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse(String.format("market://details?id=%s", context.getPackageName()));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    context.startActivity(intent);
                    setHiddenByUser();
                } catch (ActivityNotFoundException e) {}
                AnalyticsTracker.sendEvent(context, "rate", "select", "yes");
            }
        });
        builder.setNeutralButton(R.string.rate_message_later, null);
        builder.setNegativeButton(R.string.rate_message_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setHiddenByUser();
                AnalyticsTracker.sendEvent(context, "rate", "select", "no");
            }
        });
        return builder.create();
    }

    public void show() {
        if (isHiddenByUser()) {
            return;
        }
        int count = getRunCount();
        if (count == 0 || count % RUN_COUNT_TO_MESSAGE != 0) {
            return;
        }
        AlertDialog dialog = getDialog();
        dialog.show();
    }

    public void update() {
        increaseRunCount();
    }
}
