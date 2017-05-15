package com.xpn.spellnote.ui.language;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.ui.BaseViewModel;


public class LanguageItemVM extends BaseViewModel {

    private DictionaryModel dictionaryModel;
    private Boolean isDownloaded;

    LanguageItemVM(DictionaryModel dictionaryModel, Boolean isDownloaded) {
        this.dictionaryModel = dictionaryModel;
        this.isDownloaded = isDownloaded;
    }

    public String getLanguageName() {
        return dictionaryModel.getLanguageName();
    }

    public String getLogoUrl() {
        return dictionaryModel.getLogoURL();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LanguageItemVM &&
                ((LanguageItemVM) obj).dictionaryModel.equals(dictionaryModel);
    }
}
