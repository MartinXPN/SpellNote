package com.xpn.spellnote.databasemodels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Table(name = "DocumentSchema")
public class DocumentSchema extends Model {

    @Column private String title;
    @Column private String content;
    @Column private Date dateAdded;
    @Column private Date dateModified;
    @Column private String languageLocale;
    @Column private String color;
    @Column(index = true) private String category;


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

    public String getFormattedDate(Date date ) {
        return new SimpleDateFormat( "MMM d\nHH:MM", Locale.US ).format( date );
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

    public Date getDateAdded() {
        return dateAdded;
    }
    public String getDateAddedValue() {
        return getFormattedDate( getDateAdded() );
    }
    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Date getDateModified() {
        return dateModified;
    }
    public String getDateModifiedValue() { return getFormattedDate( getDateModified() ); }
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
                dateAdded + '\n' +
                dateModified + '\n' +
                languageLocale + '\n' +
                color + '\n' +
                category + '\n';
    }
}