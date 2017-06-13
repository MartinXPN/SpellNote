package com.xpn.spellnote.services.dictionary.local;

import com.xpn.spellnote.models.WordModel;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class WordSchema extends RealmObject {

    @PrimaryKey @Required private String word;
    @Index private int usage;
    private boolean isUserDefined;


    public WordSchema() {
        super();
    }

    public WordSchema(WordModel wordModel) {
        super();
        this.word = wordModel.getWord();
        this.usage = wordModel.getUsage();
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

    public boolean getUserDefined() {
        return isUserDefined;
    }
    public void setUserDefined(boolean userDefined) {
        isUserDefined = userDefined;
    }
}
