package com.xpn.spellnote.ui.document.list.trash;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.FragmentViewTrashBinding;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.list.BaseFragmentDocumentList;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.TagsUtil;


public class FragmentViewTrash extends BaseFragmentDocumentList {

    private FragmentViewTrashBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_trash, container, false);
        binding.list.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate( R.menu.menu_view_trash, menu );
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        if( id == R.id.action_empty_trash ) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)  builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
            else                                                        builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.request_remove_all_documents)
                    .setPositiveButton(R.string.request_remove_all_documents_option_yes, (dialog, which) -> viewModel.removeCategory(getCategory()))
                    .setNegativeButton(R.string.request_remove_all_documents_option_cancel, (dialog, which) -> {})
                    .show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public String getCategory() {
        return TagsUtil.CATEGORY_TRASH;
    }

    @Override
    public String getTitle() {
        return getString(R.string.nav_trash);
    }

    @Override
    public DocumentListItemVM getListItemVM(DocumentModel model, DocumentListItemVM.ViewContract viewContract) {
        DiContext diContext = ((SpellNoteApp)getActivity().getApplication()).getDiContext();
        return new TrashListItemVM(model,
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
