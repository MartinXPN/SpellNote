package com.xpn.spellnote.databasehelpers;


import com.activeandroid.query.Select;
import com.xpn.spellnote.models.DocumentSchema;
import com.xpn.spellnote.util.TagsUtil;

import java.util.List;

public class CreatedDocuments {

    public static List<DocumentSchema> getAllDocuments(String category,
                                                       String orderBy,
                                                       boolean ascending) {
        return new Select()
                        .from(DocumentSchema.class)
                        .where("Category = ?", category)
                        .orderBy( orderBy + " " + ( ascending ? TagsUtil.ORDER_ASCENDING : TagsUtil.ORDER_DESCENDING ) )
                        .execute();
    }


    public static void moveDocument( DocumentSchema document,
                                     String newCategory ) {

        document.setCategory( newCategory );
        document.save();
    }

    public static DocumentSchema getDocument( Long id ) {
        return new Select()
                .from( DocumentSchema.class )
                .where( "Id = ?", id )
                .executeSingle();
    }
}