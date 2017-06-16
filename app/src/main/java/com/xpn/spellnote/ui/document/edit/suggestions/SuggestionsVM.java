package com.xpn.spellnote.ui.document.edit.suggestions;

import android.databinding.Bindable;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.word.SuggestionService;
import com.xpn.spellnote.ui.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class SuggestionsVM extends BaseViewModel {

    private final ViewContract viewContract;
    private ArrayList <SuggestionListItemVM> suggestionVMs = new ArrayList<>();
    private final SuggestionService suggestionService;

    public SuggestionsVM(ViewContract viewContract, SuggestionService suggestionService ) {
        this.viewContract = viewContract;
        this.suggestionService = suggestionService;
    }


    public void suggest(String word) {
        addSubscription( suggestionService
                .getSuggestions(word, viewContract.getCurrentLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        wordModels -> {
                            if( !viewContract.getCurrentWord().equals(word) ) {
                                viewContract.onHideSuggestions();
                                return;
                            }

                            suggestionVMs.clear();
                            for(WordModel model : wordModels) {
                                suggestionVMs.add( new SuggestionListItemVM(viewContract, model.getWord()));
                            }

                            Timber.d(suggestionVMs.size() + " Suggestions in total");

                            notifyPropertyChanged(BR.suggestionVMs);
                            viewContract.onShowSuggestions();
                        },
                        Timber::e
                ));
    }


    @Bindable
    public List<SuggestionListItemVM> getSuggestionVMs() {
        return suggestionVMs;
    }


    public interface ViewContract extends SuggestionListItemVM.ViewContract {
        String getCurrentWord();
        DictionaryModel getCurrentLanguage();
        void onShowSuggestions();
        void onHideSuggestions();
    }
}
