package com.xpn.spellnote.ui.language;

import android.databinding.Bindable;
import android.util.Pair;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.dictionary.all.DictionariesService;
import com.xpn.spellnote.services.dictionary.saved.SavedDictionaryService;
import com.xpn.spellnote.services.word.all.WordsService;
import com.xpn.spellnote.services.word.saved.SavedWordsService;
import com.xpn.spellnote.ui.BaseViewModel;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class SelectLanguagesVM extends BaseViewModel {

    private final ViewContract viewContract;
    private final DictionariesService dictionariesService;
    private final WordsService wordsService;
    private final SavedDictionaryService savedDictionaryService;
    private final SavedWordsService savedWordsService;
    private ArrayList <LanguageItemVM> listViewModels = new ArrayList<>();

    SelectLanguagesVM(ViewContract viewContract, DictionariesService dictionariesService, SavedDictionaryService savedDictionaryService, WordsService wordsService, SavedWordsService savedWordsService) {
        this.viewContract = viewContract;
        this.dictionariesService = dictionariesService;
        this.savedDictionaryService = savedDictionaryService;
        this.wordsService = wordsService;
        this.savedWordsService = savedWordsService;
    }

    void loadDictionaries() {
        addSubscription(Observable.zip(
                dictionariesService.getAllDictionaries(),
                savedDictionaryService.getSavedDictionaries().toObservable(),
                Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> populateViewModelList(new ArrayList<>(result.first.values()), result.second),
                        Timber::e
                ));
    }

    private void populateViewModelList(ArrayList <DictionaryModel> allDictionaries, ArrayList <DictionaryModel> savedDictionaries) {

        //  Locale -> ItemVM
        Map<String, LanguageItemVM> viewModels = new TreeMap<>();
        for(DictionaryModel dictionary : allDictionaries)       viewModels.put( dictionary.getLocale(), new LanguageItemVM(dictionary, LanguageItemVM.Status.NOT_PRESENT, wordsService, savedWordsService) );
        for(DictionaryModel dictionary : savedDictionaries)     viewModels.put( dictionary.getLocale(), new LanguageItemVM(dictionary, LanguageItemVM.Status.SAVED,       wordsService, savedWordsService) );

        listViewModels = new ArrayList<>(viewModels.values());
        notifyPropertyChanged(BR.listViewModels);
    }

    @Bindable
    public ArrayList<LanguageItemVM> getListViewModels() {
        return listViewModels;
    }


    interface ViewContract {
    }
}
