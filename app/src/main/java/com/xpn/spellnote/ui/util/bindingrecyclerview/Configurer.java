package com.xpn.spellnote.ui.util.bindingrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.xpn.spellnote.R;

import java.lang.reflect.Field;


class Configurer {

    private final BindingRecyclerView view;
    private final AttributeSet attrs;
    private final Context context;

    Configurer(BindingRecyclerView view, AttributeSet attrs) {
        this.view = view;
        this.attrs = attrs;
        this.context = view.getContext();
    }

    void configure() {
        if (attrs == null)
            return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BindingRecyclerView);
        try {
            configureRowResourceId(typedArray);
            configureDataBindingVarId(typedArray);
        } finally {
            typedArray.recycle();
        }
    }

    private void configureRowResourceId(TypedArray typedArray) {
        int resId = typedArray.getResourceId(R.styleable.BindingRecyclerView_itemLayoutResId, 0);
        if (resId == 0)
            throw new IllegalStateException("BindingRecyclerView.itemLayoutResId attribute is not present");
        view.setItemLayoutResId(resId);
    }

    private void configureDataBindingVarId(TypedArray typedArray) {
        if (!typedArray.hasValue(R.styleable.BindingRecyclerView_bindingVarPath))
            throw new IllegalStateException("BindingRecyclerView.bindingVarPath attribute is not present");

        String path = typedArray.getString(R.styleable.BindingRecyclerView_bindingVarPath);
        String className = path.substring(0, path.lastIndexOf("."));
        String fieldName = path.substring(path.lastIndexOf(".") + 1);

        try {
            Class brClass = Class.forName(className);
            Field varField = brClass.getField(fieldName);
            int varId = (int) varField.get(null);
            view.setBindingVarId(varId);
        } catch (Exception e) {
            throw new IllegalStateException("Could not find binding variable id with path " + path, e);
        }
    }
}