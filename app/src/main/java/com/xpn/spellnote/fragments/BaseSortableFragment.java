package com.xpn.spellnote.fragments;


import android.app.Fragment;

import com.xpn.spellnote.adapters.BaseAdapterDocumentList;
import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.TagsUtil;

public abstract class BaseSortableFragment extends Fragment {

    protected BaseAdapterDocumentList adapter;
    public void updateDocumentList() {
        adapter.notifyDataSetChanged();
    }

    public abstract String getCategory();
    public String getSortingOrder() {
        return CacheUtil.getCache( getActivity(), getCategory() + TagsUtil.SORT_ORDER, TagsUtil.ORDER_DATE_MODIFIED );
    }
    public void setSortingOrder( String order ) {
        CacheUtil.setCache( getActivity(), getCategory() + TagsUtil.SORT_ORDER, order );
        updateDocumentList();
    }


    public boolean getAscending() {
        return CacheUtil.getCache( getActivity(), getCategory() + TagsUtil.ORDER_ASCENDING, false );
    }
    public void setAscending( boolean ascending ) {
        CacheUtil.setCache( getActivity(), getCategory() + TagsUtil.ORDER_ASCENDING, ascending );
        updateDocumentList();
    }
}
