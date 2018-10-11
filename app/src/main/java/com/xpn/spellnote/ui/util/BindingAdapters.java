package com.xpn.spellnote.ui.util;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xpn.spellnote.R;


public class BindingAdapters {

    @BindingAdapter({"app:imageUrl", "app:placeholder"})
    public static void loadImage(ImageView imageView, String url, Drawable placeholder) {
        Picasso.get()
                .load(url)
                .placeholder(placeholder)
                .resizeDimen(R.dimen.language_flag_size, R.dimen.language_flag_size)
                .centerInside()
                .into(imageView);
    }


    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource){
        imageView.setImageResource(resource);
    }

    @BindingAdapter("app:src")
    public static void loadImage(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @BindingAdapter("app:src")
    public static void loadImage(ImageView imageView, byte[] image) {
        if( image == null )
            return;
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        imageView.setImageBitmap(bitmap);
    }
}
