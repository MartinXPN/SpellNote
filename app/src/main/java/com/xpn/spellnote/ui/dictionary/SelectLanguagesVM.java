package com.xpn.spellnote.ui.dictionary;

import androidx.databinding.Bindable;
import android.util.Pair;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.dictionary.AvailableDictionariesService;
import com.xpn.spellnote.services.dictionary.SavedDictionaryService;
import com.xpn.spellnote.services.word.SavedWordsService;
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
    private final AvailableDictionariesService availableDictionariesService;
    private final SavedDictionaryService savedDictionaryService;
    private final SavedWordsService savedWordsService;
    private ArrayList <LanguageItemVM> listViewModels = new ArrayList<>();

    SelectLanguagesVM(ViewContract viewContract, AvailableDictionariesService availableDictionariesService, SavedDictionaryService savedDictionaryService, SavedWordsService savedWordsService) {
        this.viewContract = viewContract;
        this.availableDictionariesService = availableDictionariesService;
        this.savedDictionaryService = savedDictionaryService;
        this.savedWordsService = savedWordsService;
    }

    void loadDictionaries() {
        addSubscription(Observable.zip(
                availableDictionariesService.getAllDictionaries(),
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
        for(DictionaryModel dictionary : allDictionaries)       viewModels.put( dictionary.getLocale(), new LanguageItemVM(viewContract, dictionary, LanguageItemVM.Status.NOT_PRESENT, savedDictionaryService, savedWordsService) );
        for(DictionaryModel dictionary : savedDictionaries)     viewModels.put( dictionary.getLocale(), new LanguageItemVM(viewContract, dictionary, LanguageItemVM.Status.SAVED,       savedDictionaryService, savedWordsService) );

        listViewModels = new ArrayList<>(viewModels.values());
        notifyPropertyChanged(BR.listViewModels);
    }

    @Bindable
    public ArrayList<LanguageItemVM> getListViewModels() {
        return listViewModels;
    }


    interface ViewContract extends LanguageItemVM.ViewContract {
    }
}
