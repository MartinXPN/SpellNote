package com.xpn.spellnote.ui.util;


import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class BindingHolder extends RecyclerView.ViewHolder {
    private ViewDataBinding binding;

    public BindingHolder(View view) {
        super(view);
        binding = DataBindingUtil.bind(view);
    }
    public ViewDataBinding getBinding() {
        return binding;
    }
}