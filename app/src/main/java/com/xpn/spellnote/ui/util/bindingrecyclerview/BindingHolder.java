package com.xpn.spellnote.ui.util.bindingrecyclerview;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class BindingHolder<VDB extends ViewDataBinding> extends RecyclerView.ViewHolder {

    private VDB binding;

    public BindingHolder(VDB binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public VDB getBinding() {
        return binding;
    }
}