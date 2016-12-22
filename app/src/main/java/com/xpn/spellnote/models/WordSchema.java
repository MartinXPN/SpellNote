package com.xpn.spellnote.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "WordSchema")
public class WordSchema extends Model {
    @Column(index = true) public String word;
    @Column(index = true) public Integer usage;
    @Column(index = true) public String locale;
    @Column public Boolean isUserDefined = false;
}
