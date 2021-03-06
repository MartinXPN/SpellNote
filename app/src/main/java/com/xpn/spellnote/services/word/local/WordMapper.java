package com.xpn.spellnote.services.word.local;

import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;


public class WordMapper implements BeanMapper <WordModel, WordSchema> {

    @Override
    public WordModel mapFrom(WordSchema source) {
        return new WordModel(source.word, source.usage, source.isUserDefined);
    }

    @Override
    public WordSchema mapTo(WordModel source) {
        return new WordSchema(source);
    }
}
