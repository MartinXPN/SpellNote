package com.xpn.spellnote.ui.document.edit.suggestions;

import com.xpn.spellnote.ui.BaseViewModel;


public class SuggestionListItemVM extends BaseViewModel {

    private final String suggestion;
    private final ViewContract viewContract;

    SuggestionListItemVM(ViewContract viewContract, String suggestion) {
        this.viewContract = viewContract;
        this.suggestion = suggestion;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void onClick() {
        viewContract.onSuggestionSelected(suggestion);
    }


    interface ViewContract {
        void onSuggestionSelected(String suggestion);
    }
}
