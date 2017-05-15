package com.xpn.spellnote.services.document;

import com.xpn.spellnote.models.DocumentModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


public interface DocumentService {

    /**
     * Create a document either locally or in other place depending on implementation
     */
    Completable saveDocument(DocumentModel document );


    /**
     * Remove document
     */
    Completable removeDocument( DocumentModel document );


    /**
     * Move document to another category
     */
    Completable moveDocument( DocumentModel document, String newCategory );


    /**
     * Get document with the specified id
     */
    Single<DocumentModel> getDocument( Long id );


    /**
     * Get all documents in the specified category
     */
    Single <List<DocumentModel>> getAllDocuments( String category, String orderBy, boolean ascending );
}
