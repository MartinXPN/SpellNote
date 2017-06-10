package com.xpn.spellnote.services.document.local;

import com.xpn.spellnote.models.DocumentModel;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class DocumentSchema extends RealmObject {

    @PrimaryKey long id;
    String title;
    String content;
    @Index @Required Date dateModified;
    @Index @Required String languageLocale;
    @Index String category;

    public DocumentSchema() {
        super();
    }

    public DocumentSchema(Long id, String title, String content, Date dateModified, String languageLocale, String category) {
        super();
        /// generate new id if it's not present yet
        if( id == -1L )
            id = new Date().getTime();
        this.id = id;
        this.title = title;
        this.content = content;
        this.dateModified = dateModified;
        this.languageLocale = languageLocale;
        this.category = category;
    }

    public DocumentSchema(DocumentModel model) {
        this( model.getId(),
                model.getTitle(),
                model.getContent(),
                model.getDateModified(),
                model.getLanguageLocale(),
                model.getCategory() );
    }

    public String toString() {
        return  title + '\n' +
                content + '\n' +
                dateModified + '\n' +
                languageLocale + '\n' +
                category + '\n';
    }
}