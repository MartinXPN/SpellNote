package com.xpn.spellnote.databasehelpers;


import com.activeandroid.query.Select;
import com.xpn.spellnote.entities.document.DocumentModel;
import com.xpn.spellnote.util.TagsUtil;

import java.util.List;

public class CreatedDocuments {

    public static List<DocumentModel> getAllDocuments(String category,
                                                      String orderBy,
                                                      boolean ascending) {
        return new Select()
                        .from(DocumentModel.class)
                        .where("Category = ?", category)
                        .orderBy( orderBy + " " + ( ascending ? TagsUtil.ORDER_ASCENDING : TagsUtil.ORDER_DESCENDING ) )
                        .execute();
    }


    public static void moveDocument( DocumentModel document,
                                     String newCategory ) {

        document.setCategory( newCategory );
        document.save();
    }

    public static DocumentModel getDocument(Long id ) {
        return new Select()
                .from( DocumentModel.class )
                .where( "Id = ?", id )
                .executeSingle();
    }
}