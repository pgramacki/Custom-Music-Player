package com.example.piotrgramacki238493.myplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    Switch looping;
    Switch next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViews();
        setListeners();
    }

    private void setListeners() {
        looping.setOnCheckedChangeListener((buttonView, isChecked) -> Settings.looping = isChecked);

        next.setOnCheckedChangeListener(((buttonView, isChecked) -> Settings.playNext = isChecked));
    }

    private void findViews() {
        looping = findViewById(R.id.settings_loop_switch);
        looping.setChecked(Settings.looping);

        next = findViewById(R.id.settings_next_switch);
        next.setChecked(Settings.playNext);
    }
}
