package com.xpn.spellnote.services.dictionary.local;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.BeanMapper;


public class DictionaryMapper implements BeanMapper<DictionaryModel, DictionarySchema> {

    @Override
    public DictionaryModel mapFrom(DictionarySchema source) {
        if( source == null )
            return new DictionaryModel("", "", "null", 0, "", "");
        return new DictionaryModel(
                source.locale,
                source.languageName,
                source.logoURL,
                source.version,
                source.alphabet,
                source.downloadURL);
    }

    @Override
    public DictionarySchema mapTo(DictionaryModel source) {
        return new DictionarySchema(source);
    }
}
