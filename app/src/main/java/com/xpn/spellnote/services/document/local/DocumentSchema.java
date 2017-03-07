package com.xpn.spellnote.services.document.local;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


@Table(name = "SavedDocuments")
public class DocumentSchema extends Model {

    @Column String title;
    @Column String content;
    @Column Date dateModified;
    @Column String languageLocale;
    @Column String color;
    @Column(index = true) String category;


    public DocumentSchema() {
        super();
    }

    public DocumentSchema(String title, String content, Date dateModified, String languageLocale, String color, String category) {
        super();
        this.title = title;
        this.content = content;
        this.dateModified = dateModified;
        this.languageLocale = languageLocale;
        this.color = color;
        this.category = category;
    }

    public String toString() {
        return  title + '\n' +
                content + '\n' +
                dateModified + '\n' +
                languageLocale + '\n' +
                color + '\n' +
                category + '\n';
    }
}