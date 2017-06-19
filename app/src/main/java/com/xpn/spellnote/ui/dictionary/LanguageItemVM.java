package com.xpn.spellnote.ui.dictionary;

import android.databinding.Bindable;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.dictionary.SavedDictionaryService;
import com.xpn.spellnote.services.word.SavedWordsService;
import com.xpn.spellnote.ui.BaseViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import timber.log.Timber;


public class LanguageItemVM extends BaseViewModel {

    private DictionaryModel dictionaryModel;
    private Status status;
    private int progress;
    private final ViewContract viewContract;
    private final SavedDictionaryService savedDictionaryService;
    private final SavedWordsService savedWordsService;

    public enum Status { NOT_PRESENT, SAVE_IN_PROGRESS, SAVED, DELETE_IN_PROGRESS }

    LanguageItemVM(ViewContract viewContract, DictionaryModel dictionaryModel, Status status, SavedDictionaryService savedDictionaryService, SavedWordsService savedWordsService) {
        this.viewContract = viewContract;
        this.dictionaryModel = dictionaryModel;
        this.status = status;
        this.savedDictionaryService = savedDictionaryService;
        this.savedWordsService = savedWordsService;
    }

    public String getLanguageName() {
        return dictionaryModel.getLanguageName();
    }

    public String getLogoUrl() {
        return dictionaryModel.getLogoURL();
    }

    public void onClick() {
        if( status == Status.NOT_PRESENT ) {
            saveDictionary();
        }
        else if( status == Status.SAVE_IN_PROGRESS) {
            subscriptions.clear();
            viewContract.showMessage("Download canceled");
            setStatus(Status.NOT_PRESENT);
        }
        else if(status == Status.SAVED) {
            viewContract.onAskUpdateOrRemove(dictionaryModel, new DictionaryListener() {
                @Override
                public void onUpdate(DictionaryModel dictionary) {
                    updateDictionary();
                }

                @Override
                public void onRemove(DictionaryModel dictionary) {
                    removeDictionary();
                }
            });
        }
    }

    private String getDictionaryPath() {
        return Realm.getDefaultInstance().getPath()
                .replace("default.realm", dictionaryModel.getLocale() + ".realm");
    }

    private void saveDictionary() {
        saveDictionary( new ArrayList<>() );
    }
    private void saveDictionary(List<WordModel> defaultWords) {
        viewContract.onDownloadingDictionary(dictionaryModel);
        setStatus(Status.SAVE_IN_PROGRESS);
        StorageReference storage = FirebaseStorage.getInstance().getReferenceFromUrl(dictionaryModel.getDownloadURL());
        File file = new File(getDictionaryPath());
        Timber.d("Saving file to: " + getDictionaryPath());

        storage.getFile(file)
                .addOnProgressListener(snapshot -> {
                    setStatus(Status.SAVE_IN_PROGRESS);
                    setProgress((int) ((float) (snapshot.getBytesTransferred()) / snapshot.getTotalByteCount() * 100));
                    Timber.d( "Saved " + snapshot.getBytesTransferred() + " from " + snapshot.getTotalByteCount() );
                })
                .addOnCompleteListener(task -> {
                    addSubscription(Completable.mergeArray(
                            savedDictionaryService.saveDictionary(dictionaryModel),
                            savedWordsService.saveWords(dictionaryModel.getLocale(), defaultWords)
                    )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> setStatus(Status.SAVED),
                                    throwable -> {
                                        setStatus(Status.NOT_PRESENT);
                                        Timber.e(throwable);
                                        viewContract.showError("Couldn't save dictionary");
                                    }));
                    Timber.d("Download complete");
                })
                .addOnFailureListener(e -> {
                    Timber.e(e);
                    setStatus(Status.NOT_PRESENT);
                    viewContract.showError("Couldn't save dictionary");
                });
    }

    private void removeDictionary() {
        viewContract.onRemovingDictionary(dictionaryModel);
        setStatus(Status.DELETE_IN_PROGRESS);

        /// delete database file from file system
        File file = new File(getDictionaryPath());
        boolean deleted = file.delete();
        if( !deleted ) {
            setStatus(Status.SAVED);
            return;
        }

        addSubscription(savedDictionaryService.removeDictionary(dictionaryModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> setStatus(Status.NOT_PRESENT),
                        throwable -> {
                            setStatus(Status.NOT_PRESENT);
                            viewContract.showError("Couldn't delete dictionary");
                        }
                ));
    }


    private void updateDictionary() {
        viewContract.onUpdatingDictionary(dictionaryModel);
        setStatus(Status.SAVE_IN_PROGRESS);

        addSubscription( savedWordsService.getUserDefinedWords(dictionaryModel.getLocale())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userDefinedWords -> {
                            removeDictionary();
                            saveDictionary(userDefinedWords);
                        },
                        Timber::e
                ));
    }


    @Bindable
    public Status getStatus() {
        return status;
    }
    private void setStatus(Status status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public int getProgress() {
        return progress;
    }
    private void setProgress(int progress) {
        this.progress = progress;
        notifyPropertyChanged(BR.progress);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LanguageItemVM &&
                ((LanguageItemVM) obj).dictionaryModel.equals(dictionaryModel);
    }


    interface DictionaryListener {
        void onUpdate(DictionaryModel dictionary);
        void onRemove(DictionaryModel dictionary);
    }

    interface ViewContract {
        void onDownloadingDictionary(DictionaryModel dictionary);
        void onRemovingDictionary(DictionaryModel dictionary);
        void onUpdatingDictionary(DictionaryModel dictionary);
        void onAskUpdateOrRemove(DictionaryModel dictionary, DictionaryListener listener);
        void showError(String message);
        void showMessage(String message);
    }
}
