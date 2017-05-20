package com.xpn.spellnote.services.word.local;

import com.xpn.spellnote.models.WordModel;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;


public class WordSchema extends RealmObject {

    @PrimaryKey private String word;
    @Index private int usage;
    @Index private String locale;
    private boolean isUserDefined;


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

    public int getUsage() {
        return usage;
    }
    public void setUsage(int usage) {
        this.usage = usage;
    }

    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean getUserDefined() {
        return isUserDefined;
    }
    public void setUserDefined(boolean userDefined) {
        isUserDefined = userDefined;
    }
}
