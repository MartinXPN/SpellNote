package com.xpn.spellnote.services.word.saved;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.xpn.spellnote.models.WordModel;


@Table(name = "SavedWords")
public class WordSchema extends Model {

    @Column(index = true) private String word;
    @Column(index = true) private Integer usage;
    @Column(index = true) private String locale;
    @Column private Boolean isUserDefined = false;


    public WordSchema() {
        super();
    }

    public WordSchema(WordModel wordModel) {
        super();
        this.word = wordModel.getWord();
        this.usage = wordModel.getUsage();
        this.locale = wordModel.getLocale();
        this.isUserDefined = wordModel.getUserDefined();
    }


    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }

    public Integer getUsage() {
        return usage;
    }
    public void setUsage(Integer usage) {
        this.usage = usage;
    }

    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getUserDefined() {
        return isUserDefined;
    }
    public void setUserDefined(Boolean userDefined) {
        isUserDefined = userDefined;
    }
}
