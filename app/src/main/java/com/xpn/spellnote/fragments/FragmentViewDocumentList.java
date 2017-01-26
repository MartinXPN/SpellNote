package com.xpn.spellnote.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpn.spellnote.R;
import com.xpn.spellnote.activities.ActivityEditDocument;
import com.xpn.spellnote.util.Codes;
import com.xpn.spellnote.util.TagsUtil;


public class FragmentViewDocumentList extends BaseFragmentDocumentList {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_document_list, container, false);
        RecyclerView documentList = (RecyclerView) rootView.findViewById(R.id.list);
        documentList.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_add_document);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent( getActivity(), ActivityEditDocument.class );
                i.putExtra( TagsUtil.EXTRA_CATEGORY, getCategory() );
                startActivityForResult( i, Codes.EDIT_DOCUMENT_CODE );
            }
        });

        return rootView;
    }

    @Override
    public String getCategory() {
        return TagsUtil.CATEGORY_DOCUMENTS;
    }
}