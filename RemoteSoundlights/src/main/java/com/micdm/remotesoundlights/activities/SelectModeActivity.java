package com.micdm.remotesoundlights.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.micdm.remotesoundlights.R;

public class SelectModeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_mode);
        setupBossButton();
        setupGuestButton();
    }

    private void selectMode(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void setupBossButton() {
        View button = findViewById(R.id.boss);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMode(BossActivity.class);
            }
        });
    }

    private void setupGuestButton() {
        View button = findViewById(R.id.guest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMode(GuestActivity.class);
            }
        });
    }
}
