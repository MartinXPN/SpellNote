package com.xpn.spellnote.ui.language;

import android.databinding.Bindable;
import android.util.Pair;

import com.xpn.spellnote.BR;
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
    private ArrayList <LanguageItemVM> listViewModels = new ArrayList<>();

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
                        result -> populateViewModelList(new ArrayList<>(result.first.values()), result.second),
                        Timber::e
                ));
    }

    private void populateViewModelList(ArrayList <DictionaryModel> allDictionaries, ArrayList <DictionaryModel> savedDictionaries) {

        listViewModels.clear();
        for( DictionaryModel dictionary : savedDictionaries ) {
            listViewModels.add( new LanguageItemVM(dictionary, true));
        }

        for(DictionaryModel dictionary : allDictionaries) {
            if( !listViewModels.contains( new LanguageItemVM(dictionary, true)))
                listViewModels.add( new LanguageItemVM(dictionary, false));
        }

        notifyPropertyChanged(BR.listViewModels);
    }

    @Bindable
    public ArrayList<LanguageItemVM> getListViewModels() {
        return listViewModels;
    }


    interface ViewContract {
    }
}
