package com.example.andmoduleads.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.ads.ITGAd;
import com.example.andmoduleads.R;

public class TestSplash extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ITGAd.getInstance().loadSmartBanner(this, getString(R.string.admod_banner_id));
    }
}
