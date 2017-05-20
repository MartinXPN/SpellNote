package com.xpn.spellnote.ui.language;

import android.databinding.Bindable;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.word.all.WordsService;
import com.xpn.spellnote.services.word.saved.SavedWordsService;
import com.xpn.spellnote.ui.BaseViewModel;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class LanguageItemVM extends BaseViewModel {

    private DictionaryModel dictionaryModel;
    private Status status; /// what part of the dictionary is present (0 = none, 1 = whole)
    private final WordsService wordsService;
    private final SavedWordsService savedWordsService;

    enum Status {SAVED, IN_PROGRESS, NOT_PRESENT}

    LanguageItemVM(DictionaryModel dictionaryModel, Status status, WordsService wordsService, SavedWordsService savedWordsService) {
        this.dictionaryModel = dictionaryModel;
        this.status = status;
        this.wordsService = wordsService;
        this.savedWordsService = savedWordsService;
    }

    public String getLanguageName() {
        return dictionaryModel.getLanguageName();
    }

    public String getLogoUrl() {
        return dictionaryModel.getLogoURL();
    }

    public void onClick() {
        downloadLanguage();
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
                        }
                ));
    }

    private void saveLanguage(ArrayList <WordModel> words) {

        addSubscription( savedWordsService
                .saveAllWords(words)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            status = Status.SAVED;
                            words.clear();
                            notifyPropertyChanged(BR.status);
                            Timber.d("SAVED ALL WORDS!!!");
                        },
                        throwable -> {
                            status = Status.NOT_PRESENT;
                            notifyPropertyChanged(BR.status);
                            Timber.e(throwable);
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
}
