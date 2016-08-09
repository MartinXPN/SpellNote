package com.xpn.spellnote.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xpn.spellnote.R;
import com.xpn.spellnote.activities.ActivityEditDocument;
import com.xpn.spellnote.adapters.AdapterDocumentList;


public class FragmentViewDocumentList extends BaseFragmentDocumentList {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new AdapterDocumentList( getActivity() );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_document_list, container, false);
        ListView documentListView = (ListView) rootView.findViewById(R.id.list);
        documentListView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_add_document);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent( getActivity(), ActivityEditDocument.class );
                startActivity( i );
            }
        });

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.menu_view_documents, menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /// some calls are handled in BaseFragmentDocumentList superclass
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}