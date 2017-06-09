package com.xpn.spellnote.ui.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.xpn.spellnote.R;

import timber.log.Timber;


public class AdsActivity extends AppCompatActivity {

    public static void launch(Context context) {
        Intent i = new Intent(context, AdsActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InterstitialAd ads = new InterstitialAd(this);
        ads.setAdUnitId( getString(R.string.ads_unit_id) );
        ads.loadAd(new AdRequest.Builder().build());

        ads.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Timber.d("Closing the ad");
                AdsActivity.this.finish();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Timber.d("Failed loading the ad");
                AdsActivity.this.finish();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                ads.show();
            }
        });
    }
}
