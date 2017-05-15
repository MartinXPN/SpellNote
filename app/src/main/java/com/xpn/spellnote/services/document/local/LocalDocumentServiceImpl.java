package com.xpn.spellnote.services.document.local;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.services.BeanMapper;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.util.TagsUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


public class LocalDocumentServiceImpl implements DocumentService {

    private BeanMapper <DocumentModel, DocumentSchema> mapper;

    public LocalDocumentServiceImpl(BeanMapper<DocumentModel, DocumentSchema> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Completable saveDocument(DocumentModel document) {
        return Completable.fromAction(() -> {
            /// delete the document if it's present
            /// to avoid conflicts with ID
            if( document.getId() != -1 ) {
                new Delete().from(DocumentSchema.class).where("id = ?", document.getId()).execute();
            }

            DocumentSchema documentSchema = mapper.mapTo(document);
            documentSchema.save();
            document.setId(documentSchema.getId());
        });
    }

    @Override
    public Completable removeDocument(DocumentModel document) {
        return Completable.fromAction(() -> new Delete().from(DocumentSchema.class)
                .where("id = ?", document.getId())
                .execute());
    }

    @Override
    public Completable moveDocument(DocumentModel document, String newCategory) {
        return Completable.fromAction(() -> {
            new Update(DocumentSchema.class)
                    .set("category = ?", newCategory)
                    .where("id = ?", document.getId())
                    .execute();
            document.setCategory( newCategory );
        });
    }

    @Override
    public Single<DocumentModel> getDocument(Long id) {
        return Single.defer(() -> {
            DocumentSchema document =  new Select()
                    .from( DocumentSchema.class )
                    .where( "id = ?", id )
                    .executeSingle();

            return Single.just(mapper.mapFrom(document));
        });
    }

    @Override
    public Single<List<DocumentModel>> getAllDocuments(String category, String orderBy, boolean ascending) {

        return Single.defer(() -> {
            List <DocumentSchema> documents = new Select()
                    .from(DocumentSchema.class)
                    .where("category = ?", category)
                    .orderBy( orderBy + " " + ( ascending ? TagsUtil.ORDER_ASCENDING : TagsUtil.ORDER_DESCENDING ) )
                    .execute();

            return Single.just( Stream.of(documents)
                    .map(document -> mapper.mapFrom(document))
                    .collect(Collectors.toCollection(ArrayList::new)));
        });
    }
}
