package com.xpn.spellnote.ui.document.list.trash;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.FragmentViewTrashBinding;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.list.BaseFragmentDocumentList;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.TagsUtil;


public class FragmentViewTrash extends BaseFragmentDocumentList {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentViewTrashBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_trash, container, false);
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
            Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public String getCategory() {
        return TagsUtil.CATEGORY_TRASH;
    }

    @Override
    public DocumentListItemVM getListItemVM(DocumentModel model, DocumentListItemVM.ViewContract viewContract) {
        return new TrashListItemVM(model,
                ((SpellNoteApp)getActivity().getApplication()).getDiContext().getDocumentService(),
                viewContract);
    }
}
