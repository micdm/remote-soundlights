package com.micdm.remotesoundlights.notifiers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;

import com.micdm.remotesoundlights.R;
import com.micdm.remotesoundlights.utils.AppVersion;

public abstract class NewsNotifier extends Notifier {

    private static final String PREF_KEY_IS_HIDDEN = "news_message";

    public static boolean show(Context context) {
        if (!needShow(context)) {
            return false;
        }
        String version = AppVersion.get(context);
        String news = getNews(context, version);
        if (news == null || news.length() == 0) {
            return false;
        }
        AlertDialog dialog = getDialog(context, version, news);
        dialog.show();
        return true;
    }

    private static boolean needShow(Context context) {
        return !isHiddenByUser(context, PREF_KEY_IS_HIDDEN);
    }

    private static String getNews(Context context, String version) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier(String.format("news_%s", version), "string", context.getPackageName());
        return id == 0 ? null : resources.getString(id);
    }

    private static AlertDialog getDialog(final Context context, String version, String news) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.news_title, version));
        builder.setMessage(news);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setHiddenByUser(context, PREF_KEY_IS_HIDDEN);
            }
        });
        return builder.create();
    }
}
