package com.xpn.spellnote.ui.document.list;

import android.support.v4.app.Fragment;

import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.TagsUtil;


public abstract class BaseSortableFragment extends Fragment {

    private static final String SORT_ORDER = "sort_order";
    private static final String ORDER_ASCENDING = "ASC";


    public abstract void updateDocumentList();

    public abstract String getCategory();
    public String getSortingOrder() {
        return CacheUtil.getCache( getActivity(), getCategory() + SORT_ORDER, TagsUtil.ORDER_DATE_MODIFIED );
    }
    public void setSortingOrder( String order ) {
        CacheUtil.setCache( getActivity(), getCategory() + SORT_ORDER, order );
        updateDocumentList();
    }


    public boolean getAscending() {
        return CacheUtil.getCache( getActivity(), getCategory() + ORDER_ASCENDING, false );
    }
    public void setAscending( boolean ascending ) {
        CacheUtil.setCache( getActivity(), getCategory() + ORDER_ASCENDING, ascending );
        updateDocumentList();
    }
}
