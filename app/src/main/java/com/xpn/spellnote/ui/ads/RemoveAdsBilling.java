package com.xpn.spellnote.ui.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.xpn.spellnote.util.CacheUtil;


public class RemoveAdsBilling implements BillingProcessor.IBillingHandler {

    private static final String ADS_REMOVED_KEY = "rem_ads";
    private final Activity activity;
    private final BillingProcessor bp;
    private final String removeAdsId;
    private final ViewContract viewContract;

    public RemoveAdsBilling(ViewContract viewContract, Activity activity, String licenseKey, String removeAdsId) {
        this.viewContract = viewContract;
        this.activity = activity;
        this.bp = new BillingProcessor(activity, licenseKey, this);
        this.removeAdsId = removeAdsId;
    }

    public void release() {
        if (bp != null) {
            bp.release();
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return bp.handleActivityResult(requestCode, resultCode, data);
    }

    public void purchase() {
        bp.purchase(activity, removeAdsId);
    }

    public static boolean areAdsRemoved(Context context) {
        return CacheUtil.getCache(context, ADS_REMOVED_KEY, false);
    }

    public boolean areAdsRemoved() {
        return areAdsRemoved(activity);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        CacheUtil.setCache(activity, ADS_REMOVED_KEY, true);
        viewContract.onAdsRemoved();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        viewContract.onPurchaseError(error);
    }

    @Override
    public void onBillingInitialized() {

    }

    public interface ViewContract {
        void onAdsRemoved();
        void onPurchaseError(Throwable error);
    }
}
