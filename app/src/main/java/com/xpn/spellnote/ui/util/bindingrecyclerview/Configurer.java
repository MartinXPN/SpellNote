package com.xpn.spellnote.ui.util.bindingrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.xpn.spellnote.R;

import java.lang.reflect.Field;

import timber.log.Timber;


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
        Timber.d("resId: " + resId);
        view.setItemLayoutResId(resId);
    }

    private void configureDataBindingVarId(TypedArray typedArray) {
        if (!typedArray.hasValue(R.styleable.BindingRecyclerView_bindingVarPath))
            throw new IllegalStateException("BindingRecyclerView.bindingVarPath attribute is not present");

        String helloWorld = typedArray.getString(R.styleable.BindingRecyclerView_helloWorld);
        String path = typedArray.getString(R.styleable.BindingRecyclerView_bindingVarPath);
        Timber.d("helloWorld:" + helloWorld);
        String className = path.substring(0, path.lastIndexOf("."));
        String fieldName = path.substring(path.lastIndexOf(".") + 1);
        Timber.d("path:" + path);
        Timber.d("className:" + className);
        Timber.d("fieldName:" + fieldName);

        try {
            Class brClass = Class.forName(className);
            Timber.d("brClass");
            Field varField = brClass.getField(fieldName);
            Timber.d("varField");
            int varId = (int) varField.get(null);
            Timber.d("varId");
            view.setBindingVarId(varId);
            Timber.d("setBindingVarId");
        } catch (Exception e) {
            throw new IllegalStateException("Could not find binding variable id with path " + path, e);
        }
    }
}