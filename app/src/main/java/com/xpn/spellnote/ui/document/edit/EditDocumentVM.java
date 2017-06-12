package com.xpn.spellnote.ui.document.edit;

import android.databinding.Bindable;
import android.util.Pair;

import com.xpn.spellnote.BR;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.services.spellcheck.SpellCheckerService;
import com.xpn.spellnote.ui.BaseViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class EditDocumentVM extends BaseViewModel {

    private ViewContract viewContract;
    private final DocumentService documentService;
    private final SpellCheckerService spellCheckerService;

    private DocumentModel document = new DocumentModel();
    private Long documentId;


    EditDocumentVM(ViewContract viewContract,
                   Long documentId,
                   DocumentService documentService,
                   SpellCheckerService spellCheckerService) {

        this.viewContract = viewContract;
        this.documentId = documentId;
        this.documentService = documentService;
        this.spellCheckerService = spellCheckerService;
    }

    @Override
    public void onStart() {
        super.onStart();
        onLoadDocument();
    }

    @Override
    public void onDestroy() {
        /// synchronously save the document if content or title are not empty
        if( !document.getTitle().isEmpty() || !document.getContent().isEmpty() ) {
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
    }


    @Bindable
    public String getContent() {
        return document.getContent();
    }
    public void setContent(String content) {
        document.setContent(content);
        notifyPropertyChanged(BR.content);
    }

    void setLanguageLocale(String locale) {
        document.setLanguageLocale(locale);
    }


    /// check spelling of the text on the interval [left, right]
    void checkSpelling(int left, int right, List <String> words) {
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
                            viewContract.markIncorrect(left, right, wrongCorrectWords.first);
                            viewContract.markCorrect(left, right, wrongCorrectWords.second);
                        },
                        Timber::e
                ));
    }

    public interface ViewContract {
        DictionaryModel getCurrentDictionary();
        void markIncorrect(int left, int right, List <String> incorrectWords);
        void markCorrect(int left, int right, List <String> correctWords);
    }
}
