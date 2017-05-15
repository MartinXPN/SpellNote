package com.xpn.spellnote.ui.document.list.trash;

import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.ui.document.list.documents.DocumentListItemVM;
import com.xpn.spellnote.util.TagsUtil;


class TrashListItemVM extends DocumentListItemVM {

    TrashListItemVM(DocumentModel document, DocumentService documentService, ViewContract listener) {
        super(document, documentService, listener);
    }


    @Override
    public int getSecondItemDrawable() {
        return R.drawable.ic_restore;
    }

    @Override
    public void onSecondItemClicked() {
        viewContract.onRemoveDocumentFromShownList( document );
        viewContract.onShowUndoOption(document.clone(), "Restored");
        addSubscription(documentService.moveDocument(document, TagsUtil.CATEGORY_DOCUMENTS).subscribe());
    }

    @Override
    public boolean onSecondItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_restore);
        return true;
    }


    @Override
    public int getThirdItemDrawable() {
        return R.drawable.ic_delete_forever;
    }

    @Override
    public void onThirdItemClicked() {
        viewContract.onRemoveDocumentFromShownList(document);
        viewContract.onShowUndoOption(document.clone(), "Removed Forever");
        addSubscription(documentService.removeDocument(document).subscribe());
    }

    @Override
    public boolean onThirdItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_remove_forever);
        return true;
    }


    @Override
    public void onContentClicked() {
        viewContract.onShowExplanation(R.string.explanation_document_not_editable);
    }
}
