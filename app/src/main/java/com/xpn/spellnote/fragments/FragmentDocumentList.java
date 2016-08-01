package com.xpn.spellnote.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xpn.spellnote.R;
import com.xpn.spellnote.adapters.AdapterDocumentList;

import java.util.ArrayList;
import java.util.List;


public class FragmentDocumentList extends Fragment {

    private LayoutInflater currentInflater = null;
    private List <ArrayList> documentList = new ArrayList<>();      /// the list of created documents
    private AdapterDocumentList adapter;
    private OnListFragmentInteractionListener mListener;

    public FragmentDocumentList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance( true );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_document_list, container, false);
        currentInflater = inflater;
        ListView documentListView = (ListView) rootView.findViewById(R.id.list);
        adapter = new AdapterDocumentList( getActivity() );
        documentListView.setAdapter(adapter);

        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnListFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnListFragmentInteractionListener {
    }
}