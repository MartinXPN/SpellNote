package com.xpn.spellnote.models;


import java.util.Date;

public class DocumentModel {

    private Long id = -1L;
    private String title;
    private String content;
    private Date dateModified;
    private String languageLocale;
    private String color;
    private String category;


    public DocumentModel(Long id,
                         String title,
                         String content,
                         Date dateModified,
                         String languageLocale,
                         String color,
                         String category) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.dateModified = dateModified;
        this.languageLocale = languageLocale;
        this.color = color;
        this.category = category;
    }
    public DocumentModel() {
        this(-1L, "", "", new Date(), "", "", "");
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    @Override
    public DocumentModel clone() {
        return new DocumentModel(
                this.id,
                this.title,
                this.content,
                this.dateModified,
                this.languageLocale,
                this.color,
                this.category);
    }
}