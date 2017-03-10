package com.xpn.spellnote.ui.document.list.archive;

import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.TagsUtil;


class ArchiveListItemVM extends DocumentListItemVM {

    ArchiveListItemVM(DocumentModel document, DocumentService documentService, ViewContract listener) {
        super(document, documentService, listener);
    }


    @Override
    public int getFirstItemDrawable() {
        return R.drawable.ic_unarchive;
    }

    @Override
    public void onFirstItemClicked() {
        viewContract.onRemoveDocumentFromShownList(document);
        addSubscription(documentService.moveDocument(document, TagsUtil.CATEGORY_DOCUMENTS).subscribe());
    }

    @Override
    public boolean onFirstItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_unarchive);
        return true;
    }
}
