package com.xpn.spellnote.ui.document.list.documents;

import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xpn.spellnote.BR;
import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.services.dictionary.SavedDictionaryService;
import com.xpn.spellnote.services.document.DocumentService;
import com.xpn.spellnote.ui.BaseViewModel;
import com.xpn.spellnote.util.TagsUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class DocumentListItemVM extends BaseViewModel {

    protected DocumentModel document;
    protected DictionaryModel dictionary;
    protected ViewContract viewContract;
    protected DocumentService documentService;
    protected SavedDictionaryService dictionaryService;

    public DocumentListItemVM(DocumentModel document, DocumentService documentService, SavedDictionaryService dictionaryService, ViewContract viewContract ) {
        this.document = document;
        this.documentService = documentService;
        this.dictionaryService = dictionaryService;
        this.viewContract = viewContract;

        onFetchDictionary();
    }


    public String getTitle() {
        return document.getTitle();
    }
    public String getContent() {
        return document.getContent();
    }
    public String getDate() {
        return new SimpleDateFormat( "MMM d\nHH:mm", Locale.US ).format( document.getDateModified() );
    }
    @Bindable
    public String getDictionaryLogoURL() {
        if( dictionary == null )    return "error";
        else                        return dictionary.getLogoURL();
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Picasso.with(view.getContext())
                .load(url)
                .placeholder(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_language))
                .resizeDimen(R.dimen.language_flag_size, R.dimen.language_flag_size)
                .centerInside()
                .into(view);
    }


    public void onContentClicked() {
        viewContract.onEditDocument(document.getId());
    }


    /// First item in the swipe layout
    public int getFirstItemDrawable() {
        return R.drawable.ic_archive;
    }
    public void onFirstItemClicked() {
        viewContract.onRemoveDocumentFromShownList( document );
        viewContract.onShowUndoOption(document.clone(), R.string.explanation_archived);
        addSubscription(documentService.moveDocument(document, TagsUtil.CATEGORY_ARCHIVE).subscribe());
    }
    public boolean onFirstItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_move_to_archive);
        return true;
    }


    /// Second item in the swipe layout
    public int getSecondItemDrawable() {
        return R.drawable.ic_trash;
    }
    public void onSecondItemClicked() {
        viewContract.onRemoveDocumentFromShownList( document );
        viewContract.onShowUndoOption(document.clone(), R.string.explanation_moved_to_trash);
        addSubscription(documentService.moveDocument(document, TagsUtil.CATEGORY_TRASH).subscribe());
    }
    public boolean onSecondItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_move_to_trash);
        return true;
    }


    /// Third item in the swipe layout
    public int getThirdItemDrawable() {
        return R.drawable.ic_send;
    }
    public void onThirdItemClicked() {
        viewContract.onSendDocument(document.getTitle(), document.getContent());
    }
    public boolean onThirdItemLongClicked() {
        viewContract.onShowExplanation(R.string.hint_send);
        return true;
    }


    private void onFetchDictionary() {
        addSubscription(dictionaryService.getDictionary(document.getLanguageLocale())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dictionaryModel -> {
                            this.dictionary = dictionaryModel;
                            notifyPropertyChanged(BR.dictionaryLogoURL);
                        },
                        Timber::e
                ));
    }

    public interface ViewContract {
        void onRemoveDocumentFromShownList(DocumentModel document);
        void onShowUndoOption(DocumentModel previousDocument, @StringRes int messageResourceId);
        void onEditDocument(Long documentId);
        void onShowExplanation(@StringRes int messageResourceId);
        void onSendDocument(String title, String content);
    }
}
