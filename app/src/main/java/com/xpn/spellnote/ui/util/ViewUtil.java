package com.xpn.spellnote.ui.util;

import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.DimenRes;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class ViewUtil {

    public static int dpToPx( float dp, Context context ) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
    public static int dpToPx(@DimenRes int dp, Context context ) {
        float res = context.getResources().getDimension(dp);
        return dpToPx(res, context);
    }

    public static int getWindowWidth( Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    public static int getWindowHeight( Context context ) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }


    public static void hideKeyboard( AppCompatActivity activity ) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if( imm != null )
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void showKeyboard( AppCompatActivity activity, View v ) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        if( imm != null )
            imm.showSoftInput(v, 0);
    }
}
