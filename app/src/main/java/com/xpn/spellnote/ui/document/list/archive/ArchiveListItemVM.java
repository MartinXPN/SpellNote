package com.xpn.spellnote.ui.document.list.archive;

import android.app.Activity;
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.TagsUtil;


public class ArchiveListItemVM extends DocumentListItemVM {

    public ArchiveListItemVM(DocumentModel document, Activity context, ViewContract listener) {
        super(document, context, listener);
    }


    @Override
    public int getFirstItemDrawable() {
        return R.drawable.ic_unarchive;
    }

    @Override
    public void onFirstItemClicked() {
        viewContract.onPrepareDocumentToMove(document);
        document.setCategory( TagsUtil.CATEGORY_DOCUMENTS );
//        document.save();
    }

    @Override
    public boolean onFirstItemLongClicked() {
        Toast.makeText(activity, activity.getString(R.string.hint_unarchive), Toast.LENGTH_SHORT ).show();
        return true;
    }
}
