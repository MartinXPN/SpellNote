package com.xpn.spellnote.fragments;

import android.os.Bundle;
import android.view.MenuItem;

import com.xpn.spellnote.adapters.BaseAdapterDocumentList;

import java.util.ArrayList;

public class BaseFragmentDocumentList extends BaseSearchableSortableFragment {

    protected ArrayList documentList = new ArrayList<>();      /// the list of created documents
    protected BaseAdapterDocumentList adapter;
    protected OnListFragmentInteractionListener onInteractionListener;

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
    public boolean onOptionsItemSelected( MenuItem item ) {

        /// some calls are handled in BaseFragmentDocumentList superclass
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
