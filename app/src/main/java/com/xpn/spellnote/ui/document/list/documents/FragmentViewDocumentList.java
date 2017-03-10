package com.xpn.spellnote.ui.document.list.documents;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.FragmentViewDocumentListBinding;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.edit.ActivityEditDocument;
import com.xpn.spellnote.ui.document.list.BaseFragmentDocumentList;
import com.xpn.spellnote.util.Codes;
import com.xpn.spellnote.util.TagsUtil;


public class FragmentViewDocumentList extends BaseFragmentDocumentList {

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentViewDocumentListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_document_list, container, false);
        binding.list.setAdapter(adapter);

        binding.addDocument.setOnClickListener(view -> ActivityEditDocument.launchForResult(getActivity(), getCategory(), Codes.EDIT_DOCUMENT_CODE));
        return binding.getRoot();
    }

    @Override
    public String getCategory() {
        return TagsUtil.CATEGORY_DOCUMENTS;
    }

    @Override
    public DocumentListItemVM getListItemVM(DocumentModel model, DocumentListItemVM.ViewContract viewContract) {
        return new DocumentListItemVM(model, viewContract);
    }
}