package com.xpn.spellnote.adapters;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.xpn.spellnote.R;
import com.xpn.spellnote.util.Util;

import java.util.ArrayList;

public class AdapterTrash extends BaseAdapterDocumentList {

    @Override
    public ItemInteractionListener getArchiveListener() {
        return new ItemInteractionListener() {
            @Override
            public void onClick( int listPosition, View v ) {
                Snackbar.make( v, "Unarchived", Snackbar.LENGTH_LONG ).setAction( "UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                } ).show();
            }

            @Override
            public int getDrawableResId() {
                return R.drawable.ic_unarchive;
            }

            @Override
            public String getExplanation() {
                return "Unarchive";
            }
        };
    }

    @Override
    public ItemInteractionListener getTrashListener() {
        return new ItemInteractionListener() {
            @Override
            public void onClick(int listPosition, View v) {
                Snackbar.make( v, "Moved to documents", Snackbar.LENGTH_LONG ).setAction( "UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                } ).show();
            }

            @Override
            public int getDrawableResId() {
                return R.drawable.ic_undo_delete;
            }

            @Override
            public String getExplanation() {
                return "Move to documents";
            }
        };
    }

    @Override
    public ItemInteractionListener getSendListener() {
        return new ItemInteractionListener() {
            @Override
            public void onClick(int listPosition, View v) {
                Util.sendEmail( context, new String[]{}, documentList.get( listPosition ).getTitle(), documentList.get( listPosition ).getText() );
            }

            @Override
            public int getDrawableResId() {
                return R.drawable.ic_send;
            }

            @Override
            public String getExplanation() {
                return "Send";
            }
        };
    }

    @Override
    public ArrayList<DocumentData> getDocumentList() {

        ArrayList <DocumentData> documentList = new ArrayList<>();
        for( int i=0; i < 10; ++i ) {
            documentList.add( new DocumentData( "Title No:" + i, "This item is in trash for a long long time and I I I I I I don't think it will get out from here soon... Bla bla bla, bla bla bla bla!!!", "Aug 09\n13:16", (long)i ) );
        }
        return documentList;
    }


    public AdapterTrash(Context context ) {
        super( context );
    }
}
