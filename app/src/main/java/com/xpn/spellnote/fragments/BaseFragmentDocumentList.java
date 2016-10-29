package com.xpn.spellnote.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.xpn.spellnote.adapters.BaseAdapterDocumentList;
import com.xpn.spellnote.adapters.BaseAdapterDocumentList.DocumentMoveListener;
import com.xpn.spellnote.util.Codes;


public abstract class BaseFragmentDocumentList
        extends BaseSearchableSortableFragment
        implements DocumentMoveListener {

    /// the list of created documents
    protected BaseAdapterDocumentList adapter;
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

        /// some calls are handled in BaseFragmentDocumentList superclass
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDocumentMoved() {
        updateDocumentList();
    }
}
