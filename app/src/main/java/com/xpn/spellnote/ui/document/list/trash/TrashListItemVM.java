package com.xpn.spellnote.ui.document.list.trash;

import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.TagsUtil;


public class TrashListItemVM extends DocumentListItemVM {

    public TrashListItemVM(DocumentModel document, ViewContract listener) {
        super(document, listener);
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
        viewContract.onShowExplanation(R.string.hint_restore);
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
        viewContract.onShowExplanation(R.string.explanation_removed);
    }

    @Override
    public boolean onThirdItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_remove_forever);
        return true;
    }
}
