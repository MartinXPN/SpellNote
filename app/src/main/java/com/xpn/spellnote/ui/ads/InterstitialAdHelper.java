package com.xpn.spellnote.ui.ads;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.xpn.spellnote.R;


public class InterstitialAdHelper {
    private final Context context;
    private final InterstitialAd ad;

    public InterstitialAdHelper(Context context) {
        this.context = context;
        if( RemoveAdsBilling.areAdsRemoved(context) ) {
            ad = null;
            return;
        }

        MobileAds.initialize(context, context.getString(R.string.ads_unit_id) );

        ad = new InterstitialAd(context);
        ad.setAdUnitId( context.getString(R.string.ads_unit_id) );
        loadAd();

        ad.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                /// request once again
                super.onAdFailedToLoad(i);
                loadAd();
            }
        });
    }

    private void loadAd() {
        ad.loadAd(new AdRequest.Builder().build());
    }


    public void showAd(OnAdShownListener listener) throws IllegalStateException {
        if( RemoveAdsBilling.areAdsRemoved(context) ) {
            throw new IllegalStateException("Ads are already removed");
        }
        if( !ad.isLoaded() ) {
            throw new IllegalStateException("Advertisement wasn't loaded yet");
        }

        ad.show();
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                listener.onDone();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                listener.onDone();
            }
        });
    }

    public interface OnAdShownListener {
        void onDone();
    }
}
