package com.xpn.spellnote.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.xpn.spellnote.R;
import com.xpn.spellnote.adapters.BaseAdapterDocumentList.DocumentMoveListener;
import com.xpn.spellnote.util.Codes;
import com.xpn.spellnote.util.TagsUtil;


public abstract class BaseFragmentDocumentList
        extends BaseSortableFragment
        implements DocumentMoveListener {

    /// the list of created documents
    protected OnListFragmentInteractionListener onInteractionListener;

    public void updateDocumentList() {
        adapter.notifyDataSetChanged();
    }

    /// empty public constructor ( documentation-required )
    public BaseFragmentDocumentList() {}

    public interface OnListFragmentInteractionListener {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance( true );
        setHasOptionsMenu( true );
    }


    @Override
    public void onStart() {
        super.onStart();
        try {
            onInteractionListener = (OnListFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onInteractionListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == Codes.EDIT_DOCUMENT_CODE ) {
            updateDocumentList();
        }
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        Log.d( "onOptionsItemSelected", "" + id );
        if( id == R.id.action_sort_by_date_modified )   { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_DATE_MODIFIED);    return true; }
        if( id == R.id.action_sort_by_date_added )      { item.setChecked( true );  setSortingOrder(TagsUtil.ORDER_DATE_ADDED);       return true; }
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
