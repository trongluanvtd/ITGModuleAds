package com.itg.moduleads;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.admob.AppOpenManager;
import com.ads.control.funtion.AdCallback;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppOpenManager.getInstance().loadAndShowOpenSplash4SameTime(this,
                "ca-app-pub-3940256099942544/9257395921",
                "ca-app-pub-3940256099942544/92573959211",
                "ca-app-pub-3940256099942544/9257395921",
                "ca-app-pub-3940256099942544/92573959211",
                30000, new AdCallback() {
                    @Override
                    public void onAdSplashHigh1Ready() {
                        super.onAdSplashHigh1Ready();
                        AppOpenManager.getInstance().showOpenSplash4SameTime(SplashActivity.this, new AdCallback() {
                            @Override
                            public void onNextAction() {
                                super.onNextAction();
                                Log.d("LuanDev", "onAdSplashHigh1Ready: 1");
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                        });
                    }

                    @Override
                    public void onAdSplashHigh2Ready() {
                        super.onAdSplashHigh2Ready();
                        AppOpenManager.getInstance().showOpenSplash4SameTime(SplashActivity.this, new AdCallback() {
                            @Override
                            public void onNextAction() {
                                super.onNextAction();
                                Log.d("LuanDev", "onAdSplashHigh2Ready: 2");
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                        });
                    }

                    @Override
                    public void onAdSplashHigh3Ready() {
                        super.onAdSplashHigh3Ready();
                        AppOpenManager.getInstance().showOpenSplash4SameTime(SplashActivity.this, new AdCallback() {
                            @Override
                            public void onNextAction() {
                                super.onNextAction();
                                Log.d("LuanDev", "onAdSplashHigh3Ready: 3");
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                        });
                    }

                    @Override
                    public void onAdSplashNormalReady() {
                        super.onAdSplashNormalReady();
                        AppOpenManager.getInstance().showOpenSplash4SameTime(SplashActivity.this, new AdCallback() {
                            @Override
                            public void onNextAction() {
                                super.onNextAction();
                                Log.d("LuanDev", "onAdSplashNormalReady: 4");
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                        });
                    }
                });
    }
}
