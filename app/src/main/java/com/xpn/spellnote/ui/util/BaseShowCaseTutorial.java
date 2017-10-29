package com.xpn.spellnote.ui.util;

import android.content.Context;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.xpn.spellnote.R;
import com.xpn.spellnote.util.CacheUtil;


public abstract class BaseShowCaseTutorial {

    protected Context context;
    protected String tag;
    private ShowcaseView tutorialView;

    public BaseShowCaseTutorial(Context context, String tag) {
        this.context = context;
        this.tag = tag;
    }

    /**
     * Initialize and construct tutorial to be displayed later
     * @return the constructed ShowcaseView
     */
    protected abstract ShowcaseView.Builder display();

    public void showTutorial() {
        if( !isDisplayed() ) {
            ShowcaseView.Builder builder = display();
            tutorialView = builder.build();
            tutorialView.setStyle(R.style.TutorialTheme);
            tutorialView.hideButton();
            tutorialView.setOnClickListener(view -> {
                setDisplayed();
                hide();
            });
        }
    }


    /**
     * @return Weather or not the tutorial had been displayed. Fetched from cache
     */
    protected boolean isDisplayed() {
        return CacheUtil.getCache(context, tag, false);
    }

    /**
     * Save in cache that tutorial had been displayed
     */
    protected void setDisplayed() {
        setDisplayed(context, tag);
    }
    public static void setDisplayed(Context context, String tag) {
        CacheUtil.setCache(context, tag, true);
    }

    /**
     * Reset cache to display tutorial on next request
     */
    public void reset() {
        reset(context, tag);
    }
    public static void reset(Context context, String tag) {
        CacheUtil.setCache(context, tag, false);
    }

    /**
     * Hide tutorial
     */
    public void hide() {
        if(tutorialView != null)
            tutorialView.hide();
    }
}
