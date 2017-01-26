package com.xpn.spellnote.entities.word;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "WordModel")
public class WordModel extends Model {
    @Column(index = true) public String word;
    @Column(index = true) public Integer usage;
    @Column(index = true) public String locale;
    @Column public Boolean isUserDefined = false;

    public WordModel() {
        super();
    }

    public WordModel(String word,
                     Integer usage,
                     String locale,
                     Boolean isUserDefined ) {
        super();
        this.word = word;
        this.usage = usage;
        this.locale = locale;
        this.isUserDefined = isUserDefined;
    }
}
