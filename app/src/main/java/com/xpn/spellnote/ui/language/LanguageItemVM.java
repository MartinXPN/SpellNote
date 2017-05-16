package com.xpn.spellnote.ui.language;

import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.word.all.WordsService;
import com.xpn.spellnote.ui.BaseViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class LanguageItemVM extends BaseViewModel {

    private DictionaryModel dictionaryModel;
    private Boolean isDownloaded;
    private final WordsService wordsService;

    LanguageItemVM(DictionaryModel dictionaryModel, Boolean isDownloaded, WordsService wordsService) {
        this.dictionaryModel = dictionaryModel;
        this.isDownloaded = isDownloaded;
        this.wordsService = wordsService;
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

    public void downloadLanguage() {
        addSubscription(wordsService
                .getWords(dictionaryModel.getLocale())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        words -> Timber.d("Loaded " + words.size() + " words!"),
                        Timber::e
                ));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LanguageItemVM &&
                ((LanguageItemVM) obj).dictionaryModel.equals(dictionaryModel);
    }
}
