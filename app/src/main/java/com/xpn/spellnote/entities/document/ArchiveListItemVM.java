package com.xpn.spellnote.entities.document;

import android.app.Activity;
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.util.TagsUtil;


public class ArchiveListItemVM extends DocumentViewModel {

    public ArchiveListItemVM(DocumentModel document, Activity context, OnModifyDocumentListener listener) {
        super(document, context, listener);
    }


    @Override
    public int getFirstItemDrawable() {
        return R.drawable.ic_unarchive;
    }

    @Override
    public void onFirstItemClicked() {
        listener.onPrepareDocumentToMove(document);
        document.setCategory( TagsUtil.CATEGORY_DOCUMENTS );
        document.save();
    }

    @Override
    public boolean onFirstItemLongClicked() {
        Toast.makeText(activity, activity.getString(R.string.hint_unarchive), Toast.LENGTH_SHORT ).show();
        return true;
    }
}
