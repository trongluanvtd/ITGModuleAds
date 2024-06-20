package com.ads.control.application;

import android.app.Application;

import com.ads.control.config.ITGAdConfig;
import com.ads.control.util.AppUtil;
import com.ads.control.util.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class AdsApplication extends Application {

    protected ITGAdConfig mITGAdConfig;
    protected List<String> listTestDevice;

    @Override
    public void onCreate() {
        super.onCreate();
        listTestDevice = new ArrayList<String>();
        mITGAdConfig = new ITGAdConfig(this);
        if (SharePreferenceUtils.getInstallTime(this) == 0) {
            SharePreferenceUtils.setInstallTime(this);
        }
        AppUtil.currentTotalRevenue001Ad = SharePreferenceUtils.getCurrentTotalRevenue001Ad(this);
    }

}
