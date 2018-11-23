package com.xpn.spellnote.ui.util.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

import timber.log.Timber;


public class CompressTransformation extends BitmapTransformation {

    private double targetImageSizeMB;
    private int minQuality;
    private int maxQuality;

    public CompressTransformation(double targetImageSizeMB, int minQuality, int maxQuality) {
        super();
        this.targetImageSizeMB = targetImageSizeMB;
        this.minQuality = minQuality;
        this.maxQuality = maxQuality;
    }

    public CompressTransformation(double targetImageSizeMB) {
        this(targetImageSizeMB, 20, 95);
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        double originalImageSizeMB = toTransform.getByteCount() / 1e6;
        int quality = 100 - (int) (100. * originalImageSizeMB / targetImageSizeMB);
        quality = Math.min(quality, maxQuality);
        quality = Math.max(quality, minQuality);

        toTransform.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        Timber.d("Length changed from %d to %d with quality %d", toTransform.getByteCount(), byteArray.length, quality);
        Timber.d("Original image size: %fMB", originalImageSizeMB);
        Timber.d("Compressed image size: %fMB", byteArray.length / 1e6);
        return compressedBitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(("compress" + targetImageSizeMB).getBytes());
    }
}
