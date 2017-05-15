package com.xpn.spellnote.services.dictionary.saved.local;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.BeanMapper;


public class DictionaryMapper implements BeanMapper<DictionaryModel, DictionarySchema> {

    @Override
    public DictionaryModel mapFrom(DictionarySchema source) {
        return new DictionaryModel(
                source.getId(),
                source.locale,
                source.languageName,
                source.logoURL,
                source.version);
    }

    @Override
    public DictionarySchema mapTo(DictionaryModel source) {
        return new DictionarySchema(
                source.getLocale(),
                source.getLanguageName(),
                source.getLogoURL(),
                source.getVersion());
    }
}
