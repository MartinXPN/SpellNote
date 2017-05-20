package com.xpn.spellnote.services.document.local;

import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.services.BeanMapper;


public class DocumentMapper implements BeanMapper<DocumentModel, DocumentSchema> {

    @Override
    public DocumentModel mapFrom(DocumentSchema source) {
        return new DocumentModel(
                source.id,
                source.title,
                source.content,
                source.dateModified,
                source.languageLocale,
                source.color,
                source.category
        );
    }

    @Override
    public DocumentSchema mapTo(DocumentModel source) {
        DocumentSchema result = new DocumentSchema(
                source.getId(),
                source.getTitle(),
                source.getContent(),
                source.getDateModified(),
                source.getLanguageLocale(),
                source.getColor(),
                source.getCategory()
        );

        // update model
        if( source.getId() == -1 )
            source.setId(result.id);

        return result;
    }
}
