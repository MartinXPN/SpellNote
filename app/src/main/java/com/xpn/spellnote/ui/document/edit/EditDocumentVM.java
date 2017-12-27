package com.xpn.spellnote.ui.document.edit;

import android.databinding.Bindable;
import android.util.Pair;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.services.word.DictionaryChangeSuggestingService;
import com.xpn.spellnote.services.word.SavedWordsService;
import com.xpn.spellnote.services.word.SpellCheckerService;
import com.xpn.spellnote.ui.BaseViewModel;
import com.xpn.spellnote.ui.util.EditCorrectText;
import com.xpn.spellnote.ui.util.SpellCheckingListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class EditDocumentVM extends BaseViewModel implements EditCorrectText.SpellChecker {

    private ViewContract viewContract;
    private final DocumentService documentService;
    private final SpellCheckerService spellCheckerService;
    private final DictionaryChangeSuggestingService dictionaryChangeSuggestingService;
    private final SavedWordsService savedWordsService;

    private DocumentModel document = new DocumentModel();
    private Long documentId;


    EditDocumentVM(ViewContract viewContract,
                   Long documentId,
                   DocumentService documentService,
                   SpellCheckerService spellCheckerService,
                   SavedWordsService savedWordsService,
                   DictionaryChangeSuggestingService dictionaryChangeSuggestingService) {

        this.viewContract = viewContract;
        this.documentId = documentId;
        this.documentService = documentService;
        this.spellCheckerService = spellCheckerService;
        this.savedWordsService = savedWordsService;
        this.dictionaryChangeSuggestingService = dictionaryChangeSuggestingService;
    }

    @Override
    public void onStart() {
        super.onStart();
        onLoadDocument();
    }

    @Override
    public void onDestroy() {
        /// synchronously save the document if content or title are not empty
        if( !document.getTitle().equals("") || !document.getContent().equals("") ) {
            onSaveDocument();
        }
        else {
            /// synchronously remove document if both title and content are empty
            addSubscription(documentService
                    .removeDocument(document)
                    .subscribe());
        }
        super.onDestroy();
    }

    private void onLoadDocument() {
        addSubscription(documentService
                .getDocument(documentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        document -> {
                            this.document = document;
                            notifyChange();
                            viewContract.onDocumentAvailable(this.document);
                        },
                        Timber::e
                ));
    }

    void onSaveDocument() {
        addSubscription(documentService.saveDocument(document).subscribe());
    }


    @Bindable
    public String getTitle() {
        return document.getTitle();
    }
    public void setTitle(String title) {
        document.setTitle(title);
        notifyPropertyChanged(BR.title);
        onSaveDocument();
    }


    @Bindable
    public String getContent() {
        return document.getContent();
    }
    public void setContent(String content) {
        document.setContent(content);
        notifyPropertyChanged(BR.content);
        onSaveDocument();
    }


    void notifyDocumentChanged() {
        document.setDateModified(new Date());
        onSaveDocument();
    }

    @Bindable
    public String getLanguageLocale() {
        return document.getLanguageLocale();
    }
    void setLanguageLocale(String locale) {
        document.setLanguageLocale(locale);
        notifyPropertyChanged(BR.languageLocale);
        if( !document.getContent().isEmpty() && !document.getTitle().isEmpty() )
            onSaveDocument();
    }


    void addWordToDictionary(String word) {
        String locale = getLanguageLocale();
        WordModel wordModel = new WordModel(word, 100, true);

        addSubscription(savedWordsService.saveWord(locale, wordModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            viewContract.onDictionaryChanged(wordModel);
                            addSubscription(dictionaryChangeSuggestingService.suggestAdding(locale, wordModel)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            (wordModel1, throwable) -> {}
                                    ));
                        },
                        Timber::e
                ));
    }


    void removeWordFromDictionary(String word) {
        String locale = getLanguageLocale();
        WordModel wordModel = new WordModel(word, 100, true);

        addSubscription(savedWordsService.removeWord(locale, wordModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            viewContract.onDictionaryChanged(wordModel);
                            addSubscription(dictionaryChangeSuggestingService.suggestRemoving(locale, wordModel)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            (wordModel1, throwable) -> {}
                                    ));
                        },
                        Timber::e
                ));
    }


    /// check spelling of the text on the interval [left, right]
    @Override
    public void checkSpelling(int left, int right, List <String> words, SpellCheckingListener listener) {
        if( viewContract.getCurrentDictionary().getLocale() == null )
            return;

        addSubscription(spellCheckerService
                .getCorrectWords(words, viewContract.getCurrentDictionary().getLocale())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(correctWordModels -> {
                    Set <String> wrongWords = new HashSet<>(words);
                    List <String> correctWords = new ArrayList<>();
                    for(WordModel word : correctWordModels ) {
                        wrongWords.remove(word.getWord());
                        correctWords.add(word.getWord());
                    }

                    return new Pair<>( new ArrayList<>(wrongWords), correctWords );
                })
                .subscribe(
                        wrongCorrectWords -> {
                            listener.markIncorrect(left, right, wrongCorrectWords.first);
                            listener.markCorrect(left, right, wrongCorrectWords.second);
                        },
                        Timber::e
                ));
    }

    public interface ViewContract {
        DictionaryModel getCurrentDictionary();
        void onDocumentAvailable(DocumentModel document);
        void onDictionaryChanged(WordModel word);
    }
}
