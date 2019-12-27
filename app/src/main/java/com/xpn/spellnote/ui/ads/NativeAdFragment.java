package com.xpn.spellnote.ui.ads;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.NativeAdBinding;

public class NativeAdFragment extends Fragment implements NativeAdHelper.OnAdDisplayListener {

    private NativeAdBinding binding;
    private NativeAdHelper adHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.native_ad, container, false);

        // setup ads
        RemoveAdsBilling billing = new RemoveAdsBilling(null, getActivity(), getString(R.string.license_key), getString(R.string.remove_ads_id));
        adHelper = new NativeAdHelper(getActivity(), billing, this);

        binding.getRoot().setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        adHelper.loadAd();
    }

    @Override
    public void onDestroy() {
        adHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onAdReady(UnifiedNativeAd ad) {
        binding.getRoot().setVisibility(View.VISIBLE);
        adHelper.populate(binding);
    }

    @Override
    public void onHideAd(UnifiedNativeAd ad) {
        binding.getRoot().setVisibility(View.GONE);
    }
}
