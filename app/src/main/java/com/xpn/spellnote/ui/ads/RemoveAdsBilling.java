package com.xpn.spellnote.ui.ads;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;


public class RemoveAdsBilling implements BillingProcessor.IBillingHandler {

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

    public boolean areAdsRemoved() {
        return bp.isPurchased(removeAdsId);
    }



    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        viewContract.onAdsRemoved();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if( errorCode == Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED ) {
            bp.loadOwnedPurchasesFromGoogle();
            viewContract.onAdsRemoved();
            return;
        }
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
