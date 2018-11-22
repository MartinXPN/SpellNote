package com.xpn.spellnote.ui.util.image;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import timber.log.Timber;


public class ImageUtil {

    /**
     * Returns the bitmap position inside an imageView.
     * @param imageView source ImageView
     * @return 0: left, 1: top, 2: width, 3: height
     */
    public static Rect getBitmapPositionInsideImageView(ImageView imageView) {

        if (imageView == null || imageView.getDrawable() == null)
            return null;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        Timber.d("scaleX: %f, scaleY: %f, origW: %d, origH: %d", scaleX, scaleY, origW, origH);

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (imgViewH - actH) / 2;
        int left = (imgViewW - actW) / 2;
        int right = left + actW;
        int bottom = top + actH;

        return new Rect(left, top, right, bottom);
    }
}
