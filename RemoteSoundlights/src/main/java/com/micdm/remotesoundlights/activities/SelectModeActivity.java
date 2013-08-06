package com.micdm.remotesoundlights.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.micdm.remotesoundlights.R;
import com.micdm.remotesoundlights.activities.boss.BossActivity;

public class SelectModeActivity extends Activity {

    private void goToActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void setupButton() {
        findViewById(R.id.boss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(BossActivity.class);
            }
        });
        findViewById(R.id.guest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(GuestActivity.class);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_mode);
        setupButton();
    }
}
