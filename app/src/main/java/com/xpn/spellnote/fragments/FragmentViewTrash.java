package com.xpn.spellnote.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xpn.spellnote.R;
import com.xpn.spellnote.adapters.AdapterTrash;
import com.xpn.spellnote.util.TagsUtil;


public class FragmentViewTrash extends BaseFragmentDocumentList {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new AdapterTrash( getActivity(), this );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_trash, container, false);
        ListView documentListView = (ListView) rootView.findViewById(R.id.list);
        documentListView.setAdapter(adapter);

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

    @Override
    public String getCategory() {
        return TagsUtil.CATEGORY_TRASH;
    }
}
