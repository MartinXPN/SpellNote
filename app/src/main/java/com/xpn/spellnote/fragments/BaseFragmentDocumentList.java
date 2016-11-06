package com.xpn.spellnote.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.xpn.spellnote.R;
import com.xpn.spellnote.adapters.BaseAdapterDocumentList.DocumentMoveListener;
import com.xpn.spellnote.util.Codes;
import com.xpn.spellnote.util.TagsUtil;


public abstract class BaseFragmentDocumentList
        extends BaseSortableFragment
        implements DocumentMoveListener {

    /// empty public constructor ( documentation-required )
    public BaseFragmentDocumentList() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance( true );
        setHasOptionsMenu( true );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == Codes.EDIT_DOCUMENT_CODE ) {
            updateDocumentList();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.menu_view_documents, menu );

        menu.findItem( R.id.action_sort_ascending ).setChecked( getAscending() );
        String sortingOrder = getSortingOrder();
        if( sortingOrder.equals( TagsUtil.ORDER_DATE_MODIFIED ) )   menu.findItem( R.id.action_sort_by_date_modified ).setChecked( true );
        if( sortingOrder.equals( TagsUtil.ORDER_TITLE ) )           menu.findItem( R.id.action_sort_by_title ).setChecked( true );
        if( sortingOrder.equals( TagsUtil.ORDER_LANUAGE ) )         menu.findItem( R.id.action_sort_by_language ).setChecked( true );
        if( sortingOrder.equals( TagsUtil.ORDER_COLOR ) )           menu.findItem( R.id.action_sort_by_color ).setChecked( true );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        if( id == R.id.action_sort_by_date_modified )   { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_DATE_MODIFIED);    return true; }
        if( id == R.id.action_sort_by_title )           { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_TITLE);            return true; }
        if( id == R.id.action_sort_by_language )        { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_LANUAGE);          return true; }
        if( id == R.id.action_sort_by_color )           { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_COLOR);            return true; }

        if( id == R.id.action_sort_ascending )          { item.setChecked( !item.isChecked() );  setAscending( !getAscending() );     return true; }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDocumentMoved() {
        updateDocumentList();
    }
}
