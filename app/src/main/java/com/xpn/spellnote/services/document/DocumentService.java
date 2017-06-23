package com.xpn.spellnote.services.document;

import com.xpn.spellnote.models.DocumentModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


public interface DocumentService {

    Completable saveDocument(DocumentModel document );
    Completable removeDocument( DocumentModel document );
    Completable removeDocumentCategory( String category );
    Completable moveDocument( DocumentModel document, String newCategory );
    Single<DocumentModel> getDocument( Long id );
    Single <List<DocumentModel>> getAllDocuments( String category, String orderBy, boolean ascending );
}
