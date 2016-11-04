package com.xpn.spellnote.fragments;


import android.app.Fragment;
import android.util.Log;

import com.xpn.spellnote.adapters.BaseAdapterDocumentList;
import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.TagsUtil;

public abstract class BaseSortableFragment extends Fragment {

    protected BaseAdapterDocumentList adapter;

    public abstract String getCategory();
    public String getSortingOrder() {
        return CacheUtil.getCache( getActivity(), getCategory() + TagsUtil.SORT_ORDER, TagsUtil.ORDER_DATE_ADDED );
    }
    public void setSortingOrder( String order ) {
        Log.d( "setSortingOrder", order );
        CacheUtil.setCache( getActivity(), getCategory() + TagsUtil.SORT_ORDER, order );
        adapter.notifyDataSetChanged();
    }


    public boolean getAscending() {
        return CacheUtil.getCache( getActivity(), getCategory() + TagsUtil.ORDER_ASCENDING, true );
    }
    public void setAscending( boolean ascending ) {
        CacheUtil.setCache( getActivity(), getCategory() + TagsUtil.ORDER_ASCENDING, ascending );
        adapter.notifyDataSetChanged();
    }
}
