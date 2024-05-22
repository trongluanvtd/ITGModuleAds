package com.example.andmoduleads;

import com.ads.control.ads.ITGAd;
import com.ads.control.config.AdjustConfig;
import com.ads.control.config.ITGAdConfig;
import com.ads.control.application.AdsMultiDexApplication;
import com.ads.control.applovin.AppLovin;
import com.ads.control.applovin.AppOpenMax;
import com.ads.control.billing.AppPurchase;
import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.example.andmoduleads.activity.MainActivity;
import com.example.andmoduleads.activity.SplashActivity;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends AdsMultiDexApplication {

    private final String APPSFLYER_TOKEN = "2PUNpdyDTkedZTgeKkWCyB";
    private final String ADJUST_TOKEN = "cc4jvudppczk";
    private final String EVENT_PURCHASE_ADJUST = "gzel1k";
    private final String EVENT_AD_IMPRESSION_ADJUST = "gzel1k";

    protected StorageCommon storageCommon;
    private static MyApplication context;

    public static MyApplication getApplication() {
        return context;
    }

    public StorageCommon getStorageCommon() {
        return storageCommon;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Admob.getInstance().setNumToShowAds(0);

        storageCommon = new StorageCommon();
        initBilling();
        initAds();

    }

    private void initAds() {
        String environment = BuildConfig.env_dev ? ITGAdConfig.ENVIRONMENT_DEVELOP : ITGAdConfig.ENVIRONMENT_PRODUCTION;
        itgAdConfig = new ITGAdConfig(this, ITGAdConfig.PROVIDER_ADMOB, environment);

        // Optional: setup Adjust event
        AdjustConfig adjustConfig = new AdjustConfig(true,ADJUST_TOKEN);
        adjustConfig.setEventAdImpression(EVENT_AD_IMPRESSION_ADJUST);
        adjustConfig.setEventNamePurchase(EVENT_PURCHASE_ADJUST);
        itgAdConfig.setAdjustConfig(adjustConfig);
        itgAdConfig.setAdjustTokenTiktok("123456");

        // Optional: enable ads resume
        itgAdConfig.setIdAdResume(BuildConfig.ads_open_app);

        // Optional: setup list device test - recommended to use
        listTestDevice.add("EC25F576DA9B6CE74778B268CB87E431");
        itgAdConfig.setListDeviceTest(listTestDevice);
        itgAdConfig.setIntervalInterstitialAd(15);

        ITGAd.getInstance().init(this, itgAdConfig, false);

        // Auto disable ad resume after user click ads and back to app
        Admob.getInstance().setDisableAdResumeWhenClickAds(true);
        AppLovin.getInstance().setDisableAdResumeWhenClickAds(true);
        // If true -> onNextAction() is called right after Ad Interstitial showed
        Admob.getInstance().setOpenActivityAfterShowInterAds(true);

        if (ITGAd.getInstance().getMediationProvider() == ITGAdConfig.PROVIDER_ADMOB) {
            AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        } else {
            AppOpenMax.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        }
    }

    private void initBilling() {
        List<String> listINAPId = new ArrayList<>();
        listINAPId.add(MainActivity.PRODUCT_ID);
        List<String> listSubsId = new ArrayList<>();
        listSubsId.add(MainActivity.PRODUCT_ID);
        AppPurchase.getInstance().initBilling(getApplication(), listINAPId, listSubsId);
    }

}
