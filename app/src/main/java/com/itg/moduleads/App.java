package com.itg.moduleads;

import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.ads.control.ads.ITGAd;
import com.ads.control.application.AdsMultiDexApplication;
import com.ads.control.billing.AppPurchase;
import com.ads.control.config.AdjustConfig;
import com.ads.control.config.ITGAdConfig;

import java.util.ArrayList;
import java.util.List;

public class App extends AdsMultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        initAds();
        initBilling();
    }

    private void initAds() {
        String environment = BuildConfig.DEBUG ? ITGAdConfig.ENVIRONMENT_DEVELOP : ITGAdConfig.ENVIRONMENT_PRODUCTION;
        mITGAdConfig = new ITGAdConfig(this, environment);

        AdjustConfig adjustConfig = new AdjustConfig(true, getString(R.string.adjust_token));
        mITGAdConfig.setAdjustConfig(adjustConfig);
        mITGAdConfig.setFacebookClientToken(getString(R.string.facebook_client_token));
        mITGAdConfig.setAdjustTokenTiktok(getString(R.string.tiktok_token));

        mITGAdConfig.setIdAdResume("");

        ITGAd.getInstance().init(this, mITGAdConfig);
        Admob.getInstance().setDisableAdResumeWhenClickAds(true);
        Admob.getInstance().setOpenActivityAfterShowInterAds(true);
        AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity.class);
    }

    private void initBilling() {
        List<String> listIAP = new ArrayList<>();
        listIAP.add("android.test.purchased");
        List<String> listSub = new ArrayList<>();
        AppPurchase.getInstance().initBilling(this, listIAP, listSub);
    }
}
