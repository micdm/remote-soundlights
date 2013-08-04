package com.micdm.remotesoundlights.activities;

import android.os.Bundle;

import com.micdm.remotesoundlights.R;

public class GuestActivity extends PartyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startReceiverAndSender();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopReceiverAndSender();
    }
}
