package com.xpn.spellnote.ui.document.list.documents;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.FragmentViewDocumentListBinding;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.edit.ActivityEditDocument;
import com.xpn.spellnote.ui.document.list.BaseFragmentDocumentList;
import com.xpn.spellnote.util.TagsUtil;


public class FragmentViewDocumentList extends BaseFragmentDocumentList {

    private FragmentViewDocumentListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_document_list, container, false);
        binding.list.setAdapter(adapter);

        binding.addDocument.setOnClickListener(view -> ActivityEditDocument.launchForResult(this, getCategory(), EDIT_DOCUMENT_CODE));
        binding.emptyLogo.setOnClickListener(view -> ActivityEditDocument.launchForResult(this, getCategory(), EDIT_DOCUMENT_CODE));
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate( R.menu.menu_view_documents, menu );
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public String getCategory() {
        return TagsUtil.CATEGORY_DOCUMENTS;
    }

    @Override
    public String getTitle() {
        return getString(R.string.nav_documents);
    }

    @Override
    public DocumentListItemVM getListItemVM(DocumentModel model, DocumentListItemVM.ViewContract viewContract) {
        DiContext diContext = ((SpellNoteApp)getActivity().getApplication()).getDiContext();
        return new DocumentListItemVM(model,
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