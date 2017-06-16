package com.xpn.spellnote.services.word.local;

import com.xpn.spellnote.models.WordModel;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class WordSchema extends RealmObject {

    @PrimaryKey @Required String word;
    @Index int usage;
    boolean isUserDefined;


    public WordSchema() {
        super();
    }

    WordSchema(WordModel wordModel) {
        super();
        this.word = wordModel.getWord();
        this.usage = wordModel.getUsage();
        this.isUserDefined = wordModel.getUserDefined();
    }
}
