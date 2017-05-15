package com.xpn.spellnote.ui.language;

import android.util.Pair;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.dictionary.all.DictionariesService;
import com.xpn.spellnote.services.dictionary.saved.SavedDictionaryService;
import com.xpn.spellnote.ui.BaseViewModel;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class SelectLanguagesVM extends BaseViewModel {

    private final ViewContract viewContract;
    private final DictionariesService dictionariesService;
    private final SavedDictionaryService savedDictionaryService;
    private ArrayList <DictionaryModel> allDictionaries;
    private ArrayList <DictionaryModel> savedDictionaries;

    SelectLanguagesVM(ViewContract viewContract, DictionariesService dictionariesService, SavedDictionaryService savedDictionaryService) {
        this.viewContract = viewContract;
        this.dictionariesService = dictionariesService;
        this.savedDictionaryService = savedDictionaryService;
    }

    void loadDictionaries() {
        addSubscription(Observable.zip(
                dictionariesService.loadAllDictionaries(),
                savedDictionaryService.getSavedDictionaries().toObservable(),
                Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            allDictionaries = new ArrayList<>(result.first.values());
                            savedDictionaries = result.second;
                            viewContract.onDictionariesLoaded(allDictionaries, savedDictionaries);
                        },
                        Timber::e
                ));
    }


    interface ViewContract {
        void onDictionariesLoaded(ArrayList <DictionaryModel> allDictionaries, ArrayList <DictionaryModel> savedDictionaries);
    }
}
