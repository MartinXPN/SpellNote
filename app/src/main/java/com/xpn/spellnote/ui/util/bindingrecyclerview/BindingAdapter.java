package com.xpn.spellnote.ui.util.bindingrecyclerview;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


public class BindingAdapter<VDB extends ViewDataBinding> extends RecyclerView.Adapter<BindingHolder<VDB>> {

    private ItemViewModelInitializer<VDB> vmProvider;
    private int layoutResId;

    public BindingAdapter(@LayoutRes int layoutResId, ItemViewModelInitializer<VDB> itemInitializer) {
        this.vmProvider = itemInitializer;
        this.layoutResId = layoutResId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BindingHolder<VDB> onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                layoutResId,
                parent,
                false
        );
        return new BindingHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(BindingHolder<VDB> holder, int position) {
        vmProvider.onInitBinding(position, holder.getBinding());
    }

    @Override
    public int getItemCount() {
        return vmProvider.getCount();
    }


}