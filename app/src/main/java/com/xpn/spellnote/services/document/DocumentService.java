package com.xpn.spellnote.services.document;

import com.xpn.spellnote.models.DocumentModel;

import java.util.List;


public interface DocumentService {

    /**
     * Create a document either locally or in other place depending on implementation
     */
    void saveDocument(DocumentModel document );


    /**
     * Remove document
     */
    void removeDocument( DocumentModel document );


    /**
     * Move document to another category
     */
    void moveDocument( DocumentModel document, String newCategory );


    /**
     * Get document with the specified id
     */
    DocumentModel getDocument( Long id );


    /**
     * Get all documents in the specified category
     */
    List<DocumentModel> getAllDocuments( String category, String orderBy, boolean ascending );
}
