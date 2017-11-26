package com.xpn.spellnote.ui.util;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xpn.spellnote.R;


public class BindingAdapters {

    @BindingAdapter({"app:imageUrl", "app:placeholder"})
    public static void loadImage(ImageView view, String url, Drawable placeholder) {
        Picasso.with(view.getContext())
                .load(url)
                .placeholder(placeholder)
                .resizeDimen(R.dimen.language_flag_size, R.dimen.language_flag_size)
                .centerInside()
                .into(view);
    }
}
