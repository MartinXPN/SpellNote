package com.xpn.spellnote.entities.document;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


@Table(name = "DocumentModel")
public class DocumentModel extends Model {

    @Column private String title;
    @Column private String content;
    @Column private Date dateModified;
    @Column private String languageLocale;
    @Column private String color;
    @Column(index = true) private String category;


    public DocumentModel() {
        super();
    }

    public DocumentModel(String title,
                         String content,
                         Date dateModified,
                         String languageLocale,
                         String color,
                         String category ) {
        super();
        this.title = title;
        this.content = content;
        this.dateModified = dateModified;
        this.languageLocale = languageLocale;
        this.color = color;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateModified() {
        return dateModified;
    }
    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getLanguageLocale() {
        return languageLocale;
    }
    public void setLanguageLocale(String languageLocale) {
        this.languageLocale = languageLocale;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }


    public String toString() {
        return title + '\n' +
                content + '\n' +
                dateModified + '\n' +
                languageLocale + '\n' +
                color + '\n' +
                category + '\n';
    }
}