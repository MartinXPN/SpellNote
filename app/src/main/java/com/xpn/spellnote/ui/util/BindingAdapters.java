package com.xpn.spellnote.ui.util;

import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.widget.ImageView;

import com.xpn.spellnote.ui.util.svg.SvgSoftwareLayerSetter;
import com.xpn.spellnote.GlideApp;


public class BindingAdapters {

    @BindingAdapter({"imageUrl", "placeholder"})
    public static void loadImage(ImageView imageView, String url, Drawable placeholder) {
        loadImage(imageView, url, placeholder, "image");
    }

    @BindingAdapter({"imageUrl", "placeholder", "format"})
    public static void loadImage(ImageView imageView, String url, Drawable placeholder, String format) {
        if( format.matches( "svg" ) )
            GlideApp.with(imageView)
                    .as(PictureDrawable.class)
                    .listener(new SvgSoftwareLayerSetter())
                    .load(url)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .centerInside()
                    .into(imageView);
        else
            GlideApp.with(imageView)
                    .load(url)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .centerInside()
                    .into(imageView);
    }


    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource){
        imageView.setImageResource(resource);
    }

    @BindingAdapter("src")
    public static void loadImage(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @BindingAdapter("src")
    public static void loadImage(ImageView imageView, byte[] image) {
        if( image == null )
            return;
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        imageView.setImageBitmap(bitmap);
    }
}
