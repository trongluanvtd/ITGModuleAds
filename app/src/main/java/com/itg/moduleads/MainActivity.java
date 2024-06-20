package com.itg.moduleads;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.ads.ITGAd;
import com.ads.control.ads.wrapper.ApInterstitialAd;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.ads.control.billing.AppPurchase;
import com.ads.control.funtion.AdCallback;
import com.ads.control.funtion.PurchaseListener;
import com.ads.control.funtion.RewardCallback;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;

public class MainActivity extends AppCompatActivity {
    private ApInterstitialAd mInterstitialAd;
    private Button btnLoad, btnShow, btnIap, btnLoadReward, btnShowReward;
    private FrameLayout frAds;
    private ShimmerFrameLayout shimmerAds;
    private ApNativeAd mApNativeAd;
    private RewardedAd rewardedAds;
    private boolean isEarn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnLoad = findViewById(R.id.btnLoad);
        btnShow = findViewById(R.id.btnShow);
        btnIap = findViewById(R.id.btnIap);
        btnLoadReward = findViewById(R.id.btnLoadReward);
        btnShowReward = findViewById(R.id.btnShowReward);
        frAds = findViewById(R.id.fr_ads);
        shimmerAds = findViewById(R.id.shimmer_native);


        // Interstitial Ads
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ITGAd.getInstance().getInterstitialAds(MainActivity.this, BuildConfig.ad_interstitial_splash, new AdCallback() {
                    @Override
                    public void onApInterstitialLoad(@Nullable ApInterstitialAd apInterstitialAd) {
                        super.onApInterstitialLoad(apInterstitialAd);
                        mInterstitialAd = apInterstitialAd;
                        Toast.makeText(MainActivity.this, "Ads Ready", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnShow.setOnClickListener(v -> ITGAd.getInstance().forceShowInterstitial(MainActivity.this, mInterstitialAd, new AdCallback() {
        }, true));

        // Banner Ads
        ITGAd.getInstance().loadBanner(this, BuildConfig.ad_banner);
        /*ITGAd.getInstance().loadCollapsibleBanner(this, BuildConfig.ad_banner, AppConstant.CollapsibleGravity.BOTTOM, new AdCallback());*/

        // Native Ads: Load And Show
        ITGAd.getInstance().loadNativeAd(this, BuildConfig.ad_native, R.layout.native_large, frAds, shimmerAds, new AdCallback() {
            @Override
            public void onAdFailedToLoad(@Nullable LoadAdError i) {
                super.onAdFailedToLoad(i);
                frAds.removeAllViews();
            }

            @Override
            public void onAdFailedToShow(@Nullable AdError adError) {
                super.onAdFailedToShow(adError);
                frAds.removeAllViews();
            }
        });

        // Native Ads: Load
        ITGAd.getInstance().loadNativeAdResultCallback(this, BuildConfig.ad_native, R.layout.native_large, new AdCallback() {
            @Override
            public void onNativeAdLoaded(@NonNull ApNativeAd nativeAd) {
                super.onNativeAdLoaded(nativeAd);

                mApNativeAd = nativeAd;
            }

            @Override
            public void onAdFailedToLoad(@Nullable LoadAdError i) {
                super.onAdFailedToLoad(i);

                mApNativeAd = null;
            }

            @Override
            public void onAdFailedToShow(@Nullable AdError adError) {
                super.onAdFailedToShow(adError);

                mApNativeAd = null;
            }
        });

        // Native Ads: Show
        if (mApNativeAd != null) {
            ITGAd.getInstance().populateNativeAdView(this, mApNativeAd, frAds, shimmerAds);
        }

        // In-App Purchase
        AppPurchase.getInstance().setPurchaseListener(new PurchaseListener() {
            @Override
            public void onProductPurchased(String productId, String transactionDetails) {
                Toast.makeText(MainActivity.this, "onProductPurchased", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void displayErrorMessage(String errorMsg) {
                Toast.makeText(MainActivity.this, "displayErrorMessage", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserCancelBilling() {
                Toast.makeText(MainActivity.this, "onUserCancelBilling", Toast.LENGTH_SHORT).show();
            }
        });

        btnIap.setOnClickListener(v -> AppPurchase.getInstance().purchase(MainActivity.this, "android.test.purchased"));

        // Reward Ads
        btnLoadReward.setOnClickListener(v -> {
            ITGAd.getInstance().initRewardAds(this, BuildConfig.ad_reward, new AdCallback() {
                @Override
                public void onRewardAdLoaded(RewardedAd rewardedAd) {
                    super.onRewardAdLoaded(rewardedAd);
                    rewardedAds = rewardedAd;
                    Toast.makeText(MainActivity.this, "Ads Ready", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnShowReward.setOnClickListener(v -> {
            isEarn = false;
            ITGAd.getInstance().showRewardAds(MainActivity.this, rewardedAds, new RewardCallback() {
                @Override
                public void onUserEarnedReward(RewardItem var1) {
                    isEarn = true;
                }

                @Override
                public void onRewardedAdClosed() {
                    if (isEarn) {
                        // action intent
                    }
                }

                @Override
                public void onRewardedAdFailedToShow(int codeError) {

                }

                @Override
                public void onAdClicked() {

                }
            });
        });


    }
}
