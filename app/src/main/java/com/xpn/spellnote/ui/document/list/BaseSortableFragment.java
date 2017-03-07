package com.xpn.spellnote.ui.document.list;


import android.app.Fragment;

import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.TagsUtil;

public abstract class BaseSortableFragment extends Fragment {


    public abstract void updateDocumentList();

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
