package com.xpn.spellnote.ui.util.bindingrecyclerview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.Collections;
import java.util.List;

public class BindingRecyclerView extends RecyclerView {

    @LayoutRes
    private int itemLayoutResId;
    private ListItemInitializer itemInitializer;
    private int bindingVarId;

    public BindingRecyclerView(Context context) {
        this(context, null);
    }

    public BindingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BindingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        new Configurer(this, attrs).configure();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof BindingAdapter))
            throw new IllegalStateException("BindingRecyclerView operates only on BindingAdapter");
        super.setAdapter(adapter);
    }

    @Override
    public BindingAdapter<?> getAdapter() {
        return (BindingAdapter<?>) super.getAdapter();
    }

    void setBindingVarId(int bindingVarId){
        this.bindingVarId = bindingVarId;
    }

    public void setViewModelList(List<?> viewModelList) {
        if (viewModelList == null)
            viewModelList = Collections.emptyList();
        if (this.itemInitializer == null) {
            this.itemInitializer = new ListItemInitializer(viewModelList, bindingVarId);
            setAdapter(new BindingAdapter<>(itemLayoutResId, itemInitializer));
            return;
        }
        if (viewModelList != this.itemInitializer.getViewModelList()){
            itemInitializer.setViewModelList(viewModelList);
        }
        getAdapter().notifyDataSetChanged();
    }

    void setItemLayoutResId(@LayoutRes int itemLayoutResId) {
        this.itemLayoutResId = itemLayoutResId;
    }


}