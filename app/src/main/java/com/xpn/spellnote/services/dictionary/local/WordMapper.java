package com.xpn.spellnote.services.dictionary.local;

import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.BeanMapper;


public class WordMapper implements BeanMapper <WordModel, WordSchema> {

    @Override
    public WordModel mapFrom(WordSchema source) {
        return new WordModel(source.getWord(), source.getUsage(), source.getLocale(), source.getUserDefined());
    }

    @Override
    public WordSchema mapTo(WordModel source) {
        return new WordSchema(source);
    }
}
