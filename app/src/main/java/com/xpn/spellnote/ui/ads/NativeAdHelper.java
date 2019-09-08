package com.xpn.spellnote.ui.ads;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.NativeAdListItemBinding;


public class NativeAdHelper {
    private static final int FAILED_LOAD_COUNT_LIMIT = 10;
    private final AdLoader adLoader;
    private UnifiedNativeAd ad = null;
    private int failedLoadCount = 0;
    private final OnAdDisplayListener listener;


    public NativeAdHelper(Context context, RemoveAdsBilling billing, OnAdDisplayListener onAdDisplayListener) {
        listener = onAdDisplayListener;
        if( billing.areAdsRemoved() ) {
            adLoader = null;
            return;
        }

        adLoader = new AdLoader.Builder(context, context.getString(R.string.ads_native_unit_id))
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    ad = unifiedNativeAd;
                    onAdDisplayListener.onAdReady(ad);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        ++failedLoadCount;
                        if( failedLoadCount >= FAILED_LOAD_COUNT_LIMIT )
                            return;

                        super.onAdFailedToLoad(errorCode);
                        loadAd();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

    }

    public void loadAd() {
        if( adLoader == null )
            return;

        if( ad == null )
            adLoader.loadAd(new AdRequest.Builder().build());
        else
            listener.onAdReady(ad);
    }

    public UnifiedNativeAd getAd() {
        return ad;
    }

    public void populate(NativeAdListItemBinding binding) {
        binding.adRoot.setMediaView(binding.adMedia);
        binding.adRoot.setHeadlineView(binding.adHeadline);
        binding.adRoot.setBodyView(binding.adBody);
        binding.adRoot.setCallToActionView(binding.adCallToAction);
        binding.adRoot.setIconView(binding.adAppIcon);
        binding.adRoot.setPriceView(binding.adPrice);
        binding.adRoot.setStarRatingView(binding.adStars);
        binding.adRoot.setStoreView(binding.adStore);
        binding.adRoot.setAdvertiserView(binding.adAdvertiser);

        /// Fill in the content
        ((TextView)binding.adRoot.getHeadlineView()).setText(ad.getHeadline());
        binding.adRoot.getMediaView().setMediaContent(ad.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (ad.getBody() == null) {
            binding.adRoot.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            binding.adRoot.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) binding.adRoot.getBodyView()).setText(ad.getBody());
        }

        if (ad.getCallToAction() == null) {
            binding.adRoot.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            binding.adRoot.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) binding.adRoot.getCallToActionView()).setText(ad.getCallToAction());
        }

        if (ad.getIcon() == null) {
            binding.adRoot.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) binding.adRoot.getIconView()).setImageDrawable(ad.getIcon().getDrawable());
            binding.adRoot.getIconView().setVisibility(View.VISIBLE);
        }

        if (ad.getPrice() == null) {
            binding.adRoot.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            binding.adRoot.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) binding.adRoot.getPriceView()).setText(ad.getPrice());
        }

        if (ad.getStore() == null) {
            binding.adRoot.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            binding.adRoot.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) binding.adRoot.getStoreView()).setText(ad.getStore());
        }

        if (ad.getStarRating() == null) {
            binding.adRoot.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) binding.adRoot.getStarRatingView()).setRating(ad.getStarRating().floatValue());
            binding.adRoot.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (ad.getAdvertiser() == null) {
            binding.adRoot.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) binding.adRoot.getAdvertiserView()).setText(ad.getAdvertiser());
            binding.adRoot.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        binding.adRoot.setNativeAd(ad);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = ad.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        }
    }

    public void onDestroy() {
        if( ad == null )
            return;
        listener.onHideAd(ad);
        ad.destroy();
    }

    public interface OnAdDisplayListener {
        void onAdReady(UnifiedNativeAd ad);
        void onHideAd(UnifiedNativeAd ad);
    }
}
