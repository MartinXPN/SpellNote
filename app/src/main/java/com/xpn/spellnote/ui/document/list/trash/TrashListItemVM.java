package com.xpn.spellnote.ui.document.list.trash;


import android.app.Activity;
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.TagsUtil;

public class TrashListItemVM extends DocumentListItemVM {

    public TrashListItemVM(DocumentModel document, Activity context, ViewContract listener) {
        super(document, context, listener);
    }


    @Override
    public int getSecondItemDrawable() {
        return R.drawable.ic_restore;
    }

    @Override
    public void onFirstItemClicked() {
        viewContract.onPrepareDocumentToMove(document);
        document.setCategory( TagsUtil.CATEGORY_DOCUMENTS );
//        document.save();
    }

    @Override
    public boolean onFirstItemLongClicked() {
        Toast.makeText(activity, activity.getString(R.string.hint_restore), Toast.LENGTH_SHORT ).show();
        return true;
    }


    @Override
    public int getThirdItemDrawable() {
        return R.drawable.ic_delete_forever;
    }

    @Override
    public void onThirdItemClicked() {
        viewContract.onPrepareDocumentToMove(document);
//        document.delete();
        Toast.makeText( activity, "Document deleted", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public boolean onThirdItemLongClicked() {
        Toast.makeText(activity, activity.getString(R.string.hint_remove_forever), Toast.LENGTH_SHORT ).show();
        return true;
    }
}
