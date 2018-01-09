package com.xpn.spellnote.ui.util.bindingrecyclerview;


import android.databinding.ViewDataBinding;

public interface ItemViewModelInitializer<VDB extends ViewDataBinding> {

    /**
     * @param position  index of the row
     * @param vdb data binding object on which the subclass is responsible to set binding variables
     */
    void onInitBinding(int position, VDB vdb);

    int getCount();
}