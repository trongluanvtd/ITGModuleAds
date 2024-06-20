package com.ads.control.event;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.ads.control.config.ITGAdConfig;
import com.ads.control.funtion.AdType;
import com.ads.control.util.AppUtil;
import com.ads.control.util.SharePreferenceUtils;
import com.applovin.mediation.MaxAd;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdValue;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.math.BigDecimal;
import java.util.Currency;

public class ITGLogEventManager {

    private static final String TAG = "ITGLogEventManager";

    public static void logPaidAdImpression(Context context, AdValue adValue, String adUnitId, String mediationAdapterClassName, AdType adType) {
        logEventWithAds(context, (float) adValue.getValueMicros(), adValue.getPrecisionType(), adUnitId, mediationAdapterClassName, ITGAdConfig.PROVIDER_ADMOB);
        ITGAdjust.pushTrackEventAdmob(adValue);
        // Log revenue Facebook 30/08
        float value = adValue.getValueMicros() * 1.0f / 1000000;
        AppEventsLogger.newLogger(context).logPurchase(BigDecimal.valueOf(value), Currency.getInstance("VND"));
    }

    public static void logPaidAdjustWithToken(AdValue adValue, String adUnitId, String token) {
        AdjustEvent adjustEvent = new AdjustEvent(token);
        float value = adValue.getValueMicros() * 1.0f / 1000000;
        adjustEvent.setRevenue(value, "USD");
        adjustEvent.setOrderId(adUnitId);
        Adjust.trackEvent(adjustEvent);
    }

    public static void logPaidAdjustWithTokenMax(MaxAd adValue, String adUnitId, String token) {
        AdjustEvent adjustEvent = new AdjustEvent(token);
        double value = adValue.getRevenue();
        adjustEvent.setRevenue(value, "USD");
        adjustEvent.setOrderId(adUnitId);
        Adjust.trackEvent(adjustEvent);
    }

    public static void logPaidAdImpression(Context context, MaxAd adValue, AdType adType) {
        logEventWithMaxAds(context, adValue);
        ITGAdjust.pushTrackEventApplovin(adValue, context);

        // Log revenue Facebook 30/05/2024
        double value = adValue.getRevenue() * 25000;
        AppEventsLogger.newLogger(context).logPurchase(BigDecimal.valueOf(value), Currency.getInstance("VND"));
    }

    private static void logEventWithMaxAds(Context context, MaxAd impressionData) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        double revenue = impressionData.getRevenue(); // In USD
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "AppLovin");
        params.putString(FirebaseAnalytics.Param.AD_SOURCE, impressionData.getNetworkName());
        params.putString(FirebaseAnalytics.Param.AD_FORMAT, impressionData.getFormat().getLabel());
        params.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, impressionData.getAdUnitId());
        params.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
        params.putString(FirebaseAnalytics.Param.CURRENCY, "USD"); // All Applovin revenue is sent in USD
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params);
    }

    private static void logEventWithAds(Context context, float revenue, int precision, String adUnitId, String network, int mediationProvider) {
        Bundle params = new Bundle(); // Log ad value in micros.
        params.putDouble("valuemicros", revenue);
        params.putString("currency", "USD");
        params.putInt("precision", precision);
        params.putString("adunitid", adUnitId);
        params.putString("network", network);

        logPaidAdImpressionValue(context, revenue / 1000000.0, precision, adUnitId, network, mediationProvider);
        FirebaseAnalyticsUtil.logEventWithAds(context, params);
        FacebookEventUtils.logEventWithAds(context, params);
        SharePreferenceUtils.updateCurrentTotalRevenueAd(context, (float) revenue);
        logCurrentTotalRevenueAd(context, "event_current_total_revenue_ad");

        AppUtil.currentTotalRevenue001Ad += revenue;
        SharePreferenceUtils.updateCurrentTotalRevenue001Ad(context, AppUtil.currentTotalRevenue001Ad);
        logTotalRevenue001Ad(context);

        logTotalRevenueAdIn3DaysIfNeed(context);
        logTotalRevenueAdIn7DaysIfNeed(context);
    }

    private static void logPaidAdImpressionValue(Context context, double value, int precision, String adunitid, String network, int mediationProvider) {
        Bundle params = new Bundle();
        params.putDouble("value", value);
        params.putString("currency", "USD");
        params.putInt("precision", precision);
        params.putString("adunitid", adunitid);
        params.putString("network", network);


        ITGAdjust.logPaidAdImpressionValue(value, "USD");
        FirebaseAnalyticsUtil.logPaidAdImpressionValue(context, params, mediationProvider);

        FacebookEventUtils.logPaidAdImpressionValue(context, params, mediationProvider);
    }

    public static void logClickAdsEvent(Context context, String adUnitId) {
        Log.d(TAG, String.format(
                "User click ad for ad unit %s.",
                adUnitId));
        Bundle bundle = new Bundle();
        bundle.putString("ad_unit_id", adUnitId);

        FirebaseAnalyticsUtil.logClickAdsEvent(context, bundle);
        FacebookEventUtils.logClickAdsEvent(context, bundle);
    }

    public static void logCurrentTotalRevenueAd(Context context, String eventName) {
        float currentTotalRevenue = SharePreferenceUtils.getCurrentTotalRevenueAd(context);
        Bundle bundle = new Bundle();
        bundle.putFloat("value", currentTotalRevenue);

        FirebaseAnalyticsUtil.logCurrentTotalRevenueAd(context, eventName, bundle);
        FacebookEventUtils.logCurrentTotalRevenueAd(context, eventName, bundle);
    }


    public static void logTotalRevenue001Ad(Context context) {
        float revenue = AppUtil.currentTotalRevenue001Ad;
        if (revenue / 1000000 >= 0.01) {
            AppUtil.currentTotalRevenue001Ad = 0;
            SharePreferenceUtils.updateCurrentTotalRevenue001Ad(context, 0);
            Bundle bundle = new Bundle();
            bundle.putFloat("value", revenue / 1000000);
            FirebaseAnalyticsUtil.logTotalRevenue001Ad(context, bundle);
            FacebookEventUtils.logTotalRevenue001Ad(context, bundle);
        }
    }

    public static void logTotalRevenueAdIn3DaysIfNeed(Context context) {
        long installTime = SharePreferenceUtils.getInstallTime(context);
        if (!SharePreferenceUtils.isPushRevenue3Day(context)
                && (System.currentTimeMillis() - installTime >= 3L * 24 * 60 * 60 * 1000)) {
            Log.d(TAG, "logTotalRevenueAdAt3DaysIfNeed: ");
            logCurrentTotalRevenueAd(context, "event_total_revenue_ad_in_3_days");
            SharePreferenceUtils.setPushedRevenue3Day(context);
        }
    }

    public static void logTotalRevenueAdIn7DaysIfNeed(Context context) {
        long installTime = SharePreferenceUtils.getInstallTime(context);
        if (!SharePreferenceUtils.isPushRevenue7Day(context)
                && (System.currentTimeMillis() - installTime >= 7L * 24 * 60 * 60 * 1000)) {
            Log.d(TAG, "logTotalRevenueAdAt7DaysIfNeed: ");
            logCurrentTotalRevenueAd(context, "event_total_revenue_ad_in_7_days");
            SharePreferenceUtils.setPushedRevenue7Day(context);
        }
    }


    public static void setEventNamePurchaseAdjust(String eventNamePurchase) {
        ITGAdjust.setEventNamePurchase(eventNamePurchase);
    }

    public static void trackAdRevenue(String id) {
        ITGAdjust.trackAdRevenue(id);
    }

    public static void onTrackEvent(String eventName) {
        ITGAdjust.onTrackEvent(eventName);
    }

    public static void onTrackEvent(String eventName, String id) {
        ITGAdjust.onTrackEvent(eventName, id);
    }

    public static void onTrackRevenue(String eventName, float revenue, String currency) {
        ITGAdjust.onTrackRevenue(eventName, revenue, currency);
    }

    public static void onTrackRevenuePurchase(float revenue, String currency, String idPurchase, int typeIAP) {
        ITGAdjust.onTrackRevenuePurchase(revenue, currency);
    }

    public static void pushTrackEventAdmob(AdValue adValue) {
        ITGAdjust.pushTrackEventAdmob(adValue);
    }

    public static void pushTrackEventApplovin(MaxAd ad, Context context) {
        ITGAdjust.pushTrackEventApplovin(ad, context);
    }
}
