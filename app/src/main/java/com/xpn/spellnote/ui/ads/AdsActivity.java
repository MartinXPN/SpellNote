package com.xpn.spellnote.ui.ads;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.xpn.spellnote.R;


public class AdsActivity extends AppCompatActivity {

    public static void launch(Context context) {
        Intent i = new Intent(context, AdsActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_ads);

        InterstitialAd ads = new InterstitialAd(this);
        ads.setAdUnitId( getString(R.string.ads_unit_id) );
        ads.loadAd(new AdRequest.Builder().build());

        ads.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                AdsActivity.this.finish();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
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
