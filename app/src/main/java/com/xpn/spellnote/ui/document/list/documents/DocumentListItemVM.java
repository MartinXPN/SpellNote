package com.xpn.spellnote.ui.document.list.documents;

import android.support.annotation.StringRes;

import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.ui.BaseViewModel;
import com.xpn.spellnote.util.TagsUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class DocumentListItemVM extends BaseViewModel {

    protected DocumentModel document;
    protected ViewContract viewContract;
    protected DocumentService documentService;

    public DocumentListItemVM(DocumentModel document, DocumentService documentService, ViewContract viewContract ) {
        this.document = document;
        this.documentService = documentService;
        this.viewContract = viewContract;
    }


    public String getTitle() {
        return document.getTitle();
    }
    public String getContent() {
        return document.getContent();
    }
    public String getDate() {
        return new SimpleDateFormat( "MMM d\nHH:mm", Locale.US ).format( document.getDateModified() );
    }


    public void onContentClicked() {
        viewContract.onEditDocument(document.getId());
    }


    /// First item in the swipe layout
    public int getFirstItemDrawable() {
        return R.drawable.ic_archive;
    }
    public void onFirstItemClicked() {
        viewContract.onRemoveDocumentFromShownList( document );
        addSubscription(documentService.moveDocument(document, TagsUtil.CATEGORY_ARCHIVE).subscribe());
    }
    public boolean onFirstItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_move_to_archive);
        return true;
    }


    /// Second item in the swipe layout
    public int getSecondItemDrawable() {
        return R.drawable.ic_trash;
    }
    public void onSecondItemClicked() {
        viewContract.onRemoveDocumentFromShownList( document );
        addSubscription(documentService.moveDocument(document, TagsUtil.CATEGORY_TRASH).subscribe());

    }
    public boolean onSecondItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_move_to_trash);
        return true;
    }


    /// Third item in the swipe layout
    public int getThirdItemDrawable() {
        return R.drawable.ic_send;
    }
    public void onThirdItemClicked() {
        viewContract.onSendDocument(document.getTitle(), document.getContent());
    }
    public boolean onThirdItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_send);
        return true;
    }

    public interface ViewContract {
        void onRemoveDocumentFromShownList(DocumentModel document);
        void onEditDocument(Long documentId);
        void onShowExplanation(@StringRes int resourceId);
        void onSendDocument(String title, String content);
    }
}
