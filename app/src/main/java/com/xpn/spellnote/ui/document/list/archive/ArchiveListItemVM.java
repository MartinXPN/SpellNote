package com.xpn.spellnote.ui.document.list.archive;

import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.TagsUtil;


public class ArchiveListItemVM extends DocumentListItemVM {

    public ArchiveListItemVM(DocumentModel document, ViewContract listener) {
        super(document, listener);
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
        viewContract.onShowExplanation(R.string.hint_unarchive);
        return true;
    }
}
