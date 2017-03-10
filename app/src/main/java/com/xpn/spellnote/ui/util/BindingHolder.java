package com.xpn.spellnote.ui.util;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
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