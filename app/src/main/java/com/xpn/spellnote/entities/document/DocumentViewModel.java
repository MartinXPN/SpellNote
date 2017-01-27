package com.xpn.spellnote.entities.document;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.activities.ActivityEditDocument;
import com.xpn.spellnote.util.TagsUtil;
import com.xpn.spellnote.util.Util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DocumentViewModel extends BaseObservable {

    protected DocumentModel document;
    protected Activity activity;
    protected OnModifyDocumentListener listener;

    public interface OnModifyDocumentListener {
        void onMoveToArchive( DocumentModel document );
        void onMoveToDocuments( DocumentModel document );
        void onMoveToTrash( DocumentModel document );
    }

    public DocumentViewModel( DocumentModel document, Activity context, OnModifyDocumentListener listener ) {
        this.document = document;
        this.activity = context;
        this.listener = listener;
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
        document.setCategory(TagsUtil.CATEGORY_ARCHIVE);
        document.save();
        listener.onMoveToArchive( document );
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
        document.setCategory(TagsUtil.CATEGORY_TRASH);
        document.save();
        listener.onMoveToTrash( document );
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
