package com.xpn.spellnote.ui.language;

import android.databinding.Bindable;
import android.util.Pair;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.dictionary.SavedDictionaryService;
import com.xpn.spellnote.services.word.SavedWordsService;
import com.xpn.spellnote.services.word.WordsService;
import com.xpn.spellnote.ui.BaseViewModel;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class LanguageItemVM extends BaseViewModel {

    private DictionaryModel dictionaryModel;
    private Status status;
    private final ViewContract viewContract;
    private final WordsService wordsService;
    private final SavedWordsService savedWordsService;
    private final SavedDictionaryService savedDictionaryService;

    public enum Status {SAVED, IN_PROGRESS, NOT_PRESENT}

    LanguageItemVM(ViewContract viewContract, DictionaryModel dictionaryModel, Status status, WordsService wordsService, SavedWordsService savedWordsService, SavedDictionaryService savedDictionaryService) {
        this.viewContract = viewContract;
        this.dictionaryModel = dictionaryModel;
        this.status = status;
        this.wordsService = wordsService;
        this.savedWordsService = savedWordsService;
        this.savedDictionaryService = savedDictionaryService;
    }

    public String getLanguageName() {
        return dictionaryModel.getLanguageName();
    }

    public String getLogoUrl() {
        return dictionaryModel.getLogoURL();
    }

    public void onClick() {
        if( status == Status.NOT_PRESENT ) {
            downloadLanguage();
        }
        else if( status == Status.IN_PROGRESS ) {
            subscriptions.clear();
            viewContract.showMessage("Download canceled");
            status = Status.NOT_PRESENT;
            notifyPropertyChanged(BR.status);
        }
        else if(status == Status.SAVED) {
            viewContract.showMessage("Removing dictionary...");
        }
    }


    private void downloadLanguage() {
        status = Status.IN_PROGRESS;
        notifyPropertyChanged(BR.status);

        addSubscription(wordsService
                .getWords(dictionaryModel.getLocale())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        words -> {
                            Timber.d("Loaded " + words.size() + " words!");
                            ArrayList <WordModel> resultWords = new ArrayList<>(words.values());
                            words.clear();  // free up memory
                            saveLanguage(resultWords);
                        },
                        throwable -> {
                            status = Status.NOT_PRESENT;
                            notifyPropertyChanged(BR.status);
                            Timber.e(throwable);
                            viewContract.showError("Couldn't download dictionary");
                        }
                ));
    }

    private void saveLanguage(ArrayList <WordModel> words) {

        addSubscription(Single.zip(
                savedWordsService.saveAllWords(words).toSingleDefault(""),
                savedDictionaryService.saveDictionary(dictionaryModel).toSingleDefault(""),
                Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (res) -> {
                            status = Status.SAVED;
                            notifyPropertyChanged(BR.status);
                            words.clear();
                            Timber.d("SAVED ALL WORDS!!!");
                        },
                        throwable -> {
                            status = Status.NOT_PRESENT;
                            notifyPropertyChanged(BR.status);
                            Timber.e(throwable);
                            viewContract.showError("Couldn't save dictionary");
                        }
                ));
    }


    @Bindable
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LanguageItemVM &&
                ((LanguageItemVM) obj).dictionaryModel.equals(dictionaryModel);
    }


    interface ViewContract {
        void showError(String message);
        void showMessage(String message);
    }
}
