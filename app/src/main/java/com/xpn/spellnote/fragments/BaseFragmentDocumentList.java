package com.xpn.spellnote.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.xpn.spellnote.adapters.BaseAdapterDocumentList;

import java.util.ArrayList;

public class BaseFragmentDocumentList extends Fragment {

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
}
