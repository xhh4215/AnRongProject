package com.example.xiaohei.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.xiaohei.R;
import com.example.xiaohei.manager.PlayerManager;

public class PLayActivity extends AppCompatActivity {
    PlayerManager playerManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        playerManager = new PlayerManager(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("playurl");
        playerManager.play(url);
    }
}
