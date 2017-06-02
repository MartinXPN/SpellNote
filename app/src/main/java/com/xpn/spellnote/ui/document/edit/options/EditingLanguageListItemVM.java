package com.xpn.spellnote.ui.document.edit.options;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.ui.BaseViewModel;


public class EditingLanguageListItemVM extends BaseViewModel {

    private ViewContract viewContract;
    private DictionaryModel dictionary;

    EditingLanguageListItemVM(ViewContract viewContract, DictionaryModel dictionary) {
        this.viewContract = viewContract;
        this.dictionary = dictionary;
    }

    public String getLogoUrl() {
        return dictionary.getLogoURL();
    }

    public String getLanguageName() {
        return dictionary.getLanguageName();
    }

    public void onLanguageSelected() {
        viewContract.onLanguageSelected(dictionary);
    }

    public interface ViewContract {
        void onLanguageSelected(DictionaryModel dictionary);
    }
}
