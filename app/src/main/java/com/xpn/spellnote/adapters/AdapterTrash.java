package com.xpn.spellnote.adapters;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.databasehelpers.CreatedDocuments;
import com.xpn.spellnote.databasemodels.DocumentSchema;
import com.xpn.spellnote.fragments.BaseFragmentDocumentList;
import com.xpn.spellnote.util.TagsUtil;

import java.util.ArrayList;

public class AdapterTrash extends BaseAdapterDocumentList {

    public AdapterTrash( Context context, BaseFragmentDocumentList fragmentDocumentList ) {
        super( context, fragmentDocumentList );
    }

    @Override
    public ItemInteractionListener getArchiveListener() {
        return new ItemInteractionListener() {
            @Override
            public void onClick( int listPosition, View v ) {
                final DocumentSchema document = documentList.get( listPosition );
                CreatedDocuments.moveDocument( document, TagsUtil.CATEGORY_ARCHIVE );
                documentMoveListener.onDocumentMoved();

                Snackbar.make( v, "Archived", Snackbar.LENGTH_LONG ).setAction( "UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CreatedDocuments.moveDocument( document, TagsUtil.CATEGORY_TRASH );
                        documentMoveListener.onDocumentMoved();
                    }
                } ).show();
            }

            @Override
            public int getDrawableResId() {
                return R.drawable.ic_archive;
            }

            @Override
            public String getExplanation() {
                return "Archive";
            }
        };
    }

    @Override
    public ItemInteractionListener getTrashListener() {
        return new ItemInteractionListener() {
            @Override
            public void onClick(int listPosition, View v) {
                final DocumentSchema document = documentList.get( listPosition );
                CreatedDocuments.moveDocument( document, TagsUtil.CATEGORY_DOCUMENTS );
                documentMoveListener.onDocumentMoved();

                Snackbar.make( v, "Restored", Snackbar.LENGTH_LONG ).setAction( "UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CreatedDocuments.moveDocument( document, TagsUtil.CATEGORY_TRASH );
                        documentMoveListener.onDocumentMoved();
                    }
                } ).show();
            }

            @Override
            public int getDrawableResId() {
                return R.drawable.ic_undo_delete;
            }

            @Override
            public String getExplanation() {
                return "Restore";
            }
        };
    }

    @Override
    public ItemInteractionListener getSendListener() {
        return new ItemInteractionListener() {
            @Override
            public void onClick(int listPosition, View v) {
                documentList.get( listPosition ).delete();
                documentMoveListener.onDocumentMoved();
                closeAllItems();
                Toast.makeText( context, "Document Deleted", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public int getDrawableResId() {
                return R.drawable.ic_delete_forever;
            }

            @Override
            public String getExplanation() {
                return "Delete Forever";
            }
        };
    }

    @Override
    public String getDocumentCategory() {
        return TagsUtil.CATEGORY_TRASH;
    }

    @Override
    public ArrayList<DocumentSchema> getDocumentList() {

        return (ArrayList<DocumentSchema>) CreatedDocuments.getAllDocuments( getDocumentCategory(), fragmentDocumentList.getSortingOrder(), true );
    }
}
