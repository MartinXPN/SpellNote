package com.xpn.spellnote.ui.util.tutorial;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.tooltip.Tooltip;
import com.xpn.spellnote.R;
import com.xpn.spellnote.util.CacheUtil;


public class Tutorial {

    protected Context context;
    protected String tag;
    private View viewTarget;
    private MenuItem menuTarget;
    private @StringRes int textId;
    private int gravity;
    private Tooltip tooltip;


    public Tutorial(Context context, String tag, @StringRes int textId, int gravity) {
        this.context = context;
        this.tag = tag;
        this.textId = textId;
        this.gravity = gravity;
    }

    public Tutorial setTarget(View target) {
        viewTarget = target;
        return this;
    }

    public Tutorial setTarget(MenuItem target) {
        if(isDisplayed()) {
            target.getActionView().setOnClickListener(view -> ((AppCompatActivity)context).onOptionsItemSelected(target));
        }
        menuTarget = target;
        return this;
    }


    public void showTutorial(boolean redraw) {
        if(isDisplayed())
            return;

        if(isShowing() && !redraw)
            return;

        if(tooltip != null)
            tooltip.dismiss();

        Tooltip.Builder result;
        if( menuTarget != null )        result = new Tooltip.Builder(menuTarget);
        else if( viewTarget != null )   result = new Tooltip.Builder(viewTarget);
        else                            throw new IllegalStateException("Target must be initialized!");

        tooltip = result.setText(textId)
                .setGravity(gravity)
                .setBackgroundColor(context.getResources().getColor(R.color.tutorial_background_color))
                .setTextColor(context.getResources().getColor(android.R.color.white))
                .setPadding(R.dimen.tutorial_padding)
                .setCornerRadius(R.dimen.tutorial_corner_radius)
                .show();

        if( menuTarget != null ) {
            menuTarget.getActionView().setOnClickListener(view -> {
                ((AppCompatActivity)context).onOptionsItemSelected(menuTarget);
                setDisplayed();
            });
        }

        tooltip.setOnClickListener(tooltip1 -> setDisplayed());
    }


    public void showTutorial() {
        showTutorial(false);
    }

    public boolean isShowing() {
        return tooltip != null && tooltip.isShowing();
    }


    /**
     * @return Weather or not the tutorial had been displayed. Fetched from cache
     */
    private boolean isDisplayed() {
        return CacheUtil.getCache(context, tag, false);
    }

    /**
     * Save in cache that tutorial had been displayed
     */
    public void setDisplayed() {
        CacheUtil.setCache(context, tag, true);
        if(tooltip != null)
            tooltip.dismiss();
    }

    /**
     * Reset cache to prepareTutorial tutorial on next request
     */
    public void reset() {
        reset(context, tag);
    }
    private static void reset(Context context, String tag) {
        CacheUtil.setCache(context, tag, false);
    }
}
