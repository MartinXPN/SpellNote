package com.xpn.spellnote.ui.document.edit;

import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.ui.BaseViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


class EditDocumentVM extends BaseViewModel {

    private ViewContract viewContract;
    private DocumentService documentService;
    private DocumentModel document;
    private Long documentId;

    EditDocumentVM(ViewContract viewContract, Long documentId, DocumentService documentService) {
        this.viewContract = viewContract;
        this.documentId = documentId;
        this.documentService = documentService;
    }

    @Override
    public void onStart() {
        super.onStart();
        onLoadDocument();
    }

    void onLoadDocument() {
        addSubscription(documentService
                .getDocument(documentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        document -> {
                            this.document = document;
                            viewContract.onDocumentAvailable(document);
                        },
                        Timber::e
                ));
    }

    void onSaveDocument() {
        addSubscription(documentService.saveDocument(document).subscribe());
    }

    void setContent(String content) {
        document.setContent(content);
    }

    public interface ViewContract {
        void onDocumentAvailable(DocumentModel document);
    }
}
