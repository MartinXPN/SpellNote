package com.xpn.spellnote.ui.document.list.trash;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpn.spellnote.R;
import com.xpn.spellnote.ui.document.list.BaseFragmentDocumentList;
import com.xpn.spellnote.util.TagsUtil;


public class FragmentViewTrash extends BaseFragmentDocumentList {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_trash, container, false);
        RecyclerView documentList = (RecyclerView) rootView.findViewById(R.id.list);
        documentList.setAdapter(adapter);

        return rootView;
    }

    @Override
    public String getCategory() {
        return TagsUtil.CATEGORY_TRASH;
    }
}
