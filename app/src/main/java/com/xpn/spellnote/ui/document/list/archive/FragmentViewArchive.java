package com.xpn.spellnote.ui.document.list.archive;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.FragmentViewArchiveBinding;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.edit.ActivityEditDocument;
import com.xpn.spellnote.ui.document.list.BaseFragmentDocumentList;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.Codes;
import com.xpn.spellnote.util.TagsUtil;


public class FragmentViewArchive extends BaseFragmentDocumentList {

    private FragmentViewArchiveBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_archive, container, false);
        binding.list.setAdapter(adapter);

        binding.addDocument.setOnClickListener(view -> ActivityEditDocument.launchForResult(this, getCategory(), Codes.EDIT_DOCUMENT_CODE));
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate( R.menu.menu_view_documents, menu );
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public String getCategory() {
        return TagsUtil.CATEGORY_ARCHIVE;
    }

    @Override
    public DocumentListItemVM getListItemVM(DocumentModel model, DocumentListItemVM.ViewContract viewContract) {
        DiContext diContext = ((SpellNoteApp)getActivity().getApplication()).getDiContext();
        return new ArchiveListItemVM(model,
                diContext.getDocumentService(),
                diContext.getSavedDictionaryService(),
                viewContract);
    }

    @Override
    public void onShowEmptyLogo() {
        binding.emptyLogo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideEmptyLogo() {
        binding.emptyLogo.setVisibility(View.GONE);
    }
}
