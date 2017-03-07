package com.xpn.spellnote.services.document.local;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.util.TagsUtil;

import java.util.ArrayList;
import java.util.List;


public class LocalDocumentServiceImpl implements DocumentService {

    private BeanMapper <DocumentModel, DocumentSchema> mapper;

    public LocalDocumentServiceImpl(BeanMapper<DocumentModel, DocumentSchema> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void saveDocument(DocumentModel document) {
        /// delete the document if it's present
        /// to avoid conflicts with ID
        if( document.getId() != -1 ) {
            new Delete().from(DocumentSchema.class).where("id = ?", document.getId()).executeSingle();
        }

        DocumentSchema documentSchema = mapper.mapTo(document);
        documentSchema.save();
        document.setId(documentSchema.getId());
    }

    @Override
    public void removeDocument(DocumentModel document) {
        new Delete().from(DocumentSchema.class)
                .where("id = ?", document.getId())
                .execute();
    }

    @Override
    public void moveDocument(DocumentModel document, String newCategory) {
        new Update(DocumentSchema.class)
                .set("category = ?", newCategory)
                .where("id = ?", document.getId())
                .execute();
        document.setCategory( newCategory );
    }

    @Override
    public DocumentModel getDocument(Long id) {
        DocumentSchema document =  new Select()
                .from( DocumentSchema.class )
                .where( "id = ?", id )
                .executeSingle();

        return mapper.mapFrom(document);
    }

    @Override
    public List<DocumentModel> getAllDocuments(String category, String orderBy, boolean ascending) {
        List <DocumentSchema> documents = new Select()
                .from(DocumentSchema.class)
                .where("category = ?", category)
                .orderBy( orderBy + " " + ( ascending ? TagsUtil.ORDER_ASCENDING : TagsUtil.ORDER_DESCENDING ) )
                .execute();

        ArrayList<DocumentModel> res = new ArrayList<>();
        for( DocumentSchema document : documents )
            res.add( mapper.mapFrom( document ) );
        return res;
    }
}
