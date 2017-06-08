package com.xpn.spellnote.ui.document.edit.editinglanguage;

import android.databinding.Bindable;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.dictionary.SavedDictionaryService;
import com.xpn.spellnote.ui.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class EditingLanguageChooserVM extends BaseViewModel {

    private final ViewContract viewContract;
    private final SavedDictionaryService savedDictionaryService;
    private ArrayList<DictionaryModel> supportedDictionaries = new ArrayList<>();
    private DictionaryModel currentLanguage;

    public EditingLanguageChooserVM(ViewContract viewContract, SavedDictionaryService savedDictionaryService) {
        this.viewContract = viewContract;
        this.savedDictionaryService = savedDictionaryService;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadSupportedDictionaries();
    }

    @Bindable
    public String getCurrentLanguageLogoUrl() {
        if( currentLanguage == null )
            return "error";
        return currentLanguage.getLogoURL();
    }
    public void setCurrentLanguage(DictionaryModel language) {
        currentLanguage = language;
        notifyPropertyChanged(BR.currentLanguageLogoUrl);
    }
    public DictionaryModel getCurrentLanguage() {
        return currentLanguage;
    }

    public boolean onHideLanguageList() {
        if( viewContract.isLanguageListOpen() ) {
            viewContract.hideAvailableLanguages();
            return true;
        }
        return false;
    }

    public void onShowAvailableLanguages() {
        viewContract.showAvailableLanguages();
    }

    public void loadSupportedDictionaries() {
        addSubscription(savedDictionaryService
                .getSavedDictionaries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dictionaryModels -> {
                            supportedDictionaries = dictionaryModels;
                            notifyPropertyChanged(BR.listViewModels);
                        },
                        Timber::e
                ));
    }

    @Bindable
    public List<EditingLanguageListItemVM> getListViewModels() {
        ArrayList <EditingLanguageListItemVM> viewModels = new ArrayList<>();
        for( DictionaryModel model : supportedDictionaries )
            viewModels.add( new EditingLanguageListItemVM(viewContract, model) );

        return viewModels;
    }


    public interface ViewContract extends EditingLanguageListItemVM.ViewContract {
        void showAvailableLanguages();
        void hideAvailableLanguages();
        boolean isLanguageListOpen();
    }
}
