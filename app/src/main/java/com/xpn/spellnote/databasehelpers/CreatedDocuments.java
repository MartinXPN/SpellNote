package com.xpn.spellnote.databasehelpers;


import android.util.Log;

import com.activeandroid.query.Select;
import com.xpn.spellnote.databasemodels.DocumentSchema;

import java.util.List;


public class CreatedDocuments {

    public static List<DocumentSchema> getAllDocuments(String category,
                                                       String orderBy,
                                                       boolean ascending) {
        return new Select()
                        .from(DocumentSchema.class)
                        .where("Category = ?", category)
                        .orderBy( orderBy + " " + ( ascending ? "ASC" : "DESC" ) )
                        .execute();
    }


    public static void moveDocument( DocumentSchema document,
                                     String newCategory ) {

        document.setCategory( newCategory );
        document.save();
    }
}