package com.xpn.spellnote.ui.document.edit.suggestions;

import androidx.databinding.Bindable;

import com.google.firebase.perf.metrics.AddTrace;
import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.word.SuggestionService;
import com.xpn.spellnote.ui.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class SuggestionsVM extends BaseViewModel {

    private Disposable suggestionSubscription;
    private final ViewContract viewContract;
    private ArrayList <SuggestionListItemVM> suggestionVMs = new ArrayList<>();
    private final SuggestionService suggestionService;

    public SuggestionsVM(ViewContract viewContract, SuggestionService suggestionService ) {
        this.viewContract = viewContract;
        this.suggestionService = suggestionService;
    }

    @Override
    public void onDestroy() {
        if( suggestionSubscription != null && !suggestionSubscription.isDisposed() )
            suggestionSubscription.dispose();
        super.onDestroy();
    }

    @AddTrace(name = "suggestWords")
    public void suggest(String word) {
        /// cancel previous suggestion subscription
        if( suggestionSubscription != null && !suggestionSubscription.isDisposed() )
            suggestionSubscription.dispose();

        suggestionSubscription = suggestionService
                .getSuggestions(word, viewContract.getCurrentDictionary())
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

                            Timber.d("%s Suggestions in total", suggestionVMs.size());
                            notifyPropertyChanged(BR.suggestionVMs);
                            viewContract.onShowSuggestions();
                        },
                        Timber::e
                );
    }


    @Bindable
    public List<SuggestionListItemVM> getSuggestionVMs() {
        return suggestionVMs;
    }


    public interface ViewContract extends SuggestionListItemVM.ViewContract {
        String getCurrentWord();
        DictionaryModel getCurrentDictionary();
        void onShowSuggestions();
        void onHideSuggestions();
    }
}
