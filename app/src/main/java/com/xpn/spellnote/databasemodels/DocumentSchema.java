package com.xpn.spellnote.databasemodels;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


@Table(name = "DocumentSchema")
public class DocumentSchema extends Model {

    @Column public String title;
    @Column public String content;
    @Column public Date dateAdded;
    @Column public Date dateModified;
    @Column public String languageLocale;
    @Column public String color;
    @Column(index = true) public String category;


    public DocumentSchema() {
        super();
    }

    public DocumentSchema(String title,
                          String content,
                          Date dateAdded,
                          Date dateModified,
                          String languageLocale,
                          String color,
                          String category ) {
        super();
        this.title = title;
        this.content = content;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.languageLocale = languageLocale;
        this.color = color;
        this.category = category;
    }
}
