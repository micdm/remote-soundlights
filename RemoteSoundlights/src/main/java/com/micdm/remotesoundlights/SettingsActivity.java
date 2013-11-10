package com.micdm.remotesoundlights;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.micdm.remotesoundlights.utils.AnalyticsTracker;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsTracker.sendActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AnalyticsTracker.sendActivityStop(this);
    }
}
