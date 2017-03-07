package com.xpn.spellnote.ui.document.list.documents;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.ui.document.edit.ActivityEditDocument;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.util.TagsUtil;
import com.xpn.spellnote.util.Util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DocumentListItemVM extends BaseObservable {

    protected DocumentModel document;
    protected Activity activity;
    protected ViewContract viewContract;

    public interface ViewContract {
        void onPrepareDocumentToMove(DocumentModel document);
    }

    public DocumentListItemVM(DocumentModel document, Activity context, ViewContract listener ) {
        this.document = document;
        this.activity = context;
        this.viewContract = listener;
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


    public void onContentClicked() {
        ActivityEditDocument.launch( activity, document.getId() );
    }


    /// First item in the swipe layout
    public int getFirstItemDrawable() {
        return R.drawable.ic_archive;
    }
    public void onFirstItemClicked() {
        viewContract.onPrepareDocumentToMove( document );
        document.setCategory(TagsUtil.CATEGORY_ARCHIVE);
//        document.save();
    }
    public boolean onFirstItemLongClicked() {
        Toast.makeText(activity, activity.getString(R.string.hint_move_to_archive), Toast.LENGTH_SHORT ).show();
        return true;
    }


    /// Second item in the swipe layout
    public int getSecondItemDrawable() {
        return R.drawable.ic_trash;
    }
    public void onSecondItemClicked() {
        viewContract.onPrepareDocumentToMove( document );
        document.setCategory( TagsUtil.CATEGORY_TRASH );
//        document.save();
    }
    public boolean onSecondItemLongClicked() {
        Toast.makeText(activity, activity.getString(R.string.hint_move_to_trash), Toast.LENGTH_SHORT ).show();
        return true;
    }


    /// Third item in the swipe layout
    public int getThirdItemDrawable() {
        return R.drawable.ic_send;
    }
    public void onThirdItemClicked() {
        Util.sendDocument( activity, document.getTitle(), document.getCategory() );
    }
    public boolean onThirdItemLongClicked() {
        Toast.makeText(activity, activity.getString(R.string.hint_send), Toast.LENGTH_SHORT ).show();
        return true;
    }
}
