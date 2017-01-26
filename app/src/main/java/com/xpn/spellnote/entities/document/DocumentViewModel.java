package com.xpn.spellnote.entities.document;

import android.content.Context;
import android.databinding.BaseObservable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xpn.spellnote.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DocumentViewModel extends BaseObservable {

    private DocumentModel document;
    private Context context;

    public DocumentViewModel( DocumentModel document, Context context ) {
        this.document = document;
        this.context = context;
    }


    private String getFormattedDate(Date date) {
        return new SimpleDateFormat( "MMM d\nHH:mm", Locale.US ).format( date );
    }
    public String getTitle() {
        return document.getTitle();
    }
    public String getContent() {
        return document.getContent();
    }
    public String getDate() {
        return getFormattedDate( document.getDateModified() );
    }




    public View.OnClickListener onContentClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( context, "Document clicked", Toast.LENGTH_SHORT ).show();
            }
        };
    }


    /// First item in the swipe layout
    public int getFirstItemDrawable() {
        return R.drawable.ic_archive;
    }
    public View.OnClickListener onFirstItemClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( "DocumentVM", "On first item clicked" );
            }
        };
    }
    public View.OnLongClickListener onFirstItemLongClicked() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText( context, "First item long clicked", Toast.LENGTH_SHORT ).show();
                return true;
            }
        };
    }


    /// Second item in the swipe layout
    public int getSecondItemDrawable() {
        return R.drawable.ic_trash;
    }
    public View.OnClickListener onSecondItemClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( "DocumentVM", "On second item clicked" );
            }
        };
    }
    public View.OnLongClickListener onSecondItemLongClicked() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText( context, "Second item long clicked", Toast.LENGTH_SHORT ).show();
                return true;
            }
        };
    }


    /// Third item in the swipe layout
    public int getThirdItemDrawable() {
        return R.drawable.ic_send;
    }
    public View.OnClickListener onThirdItemClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( "DocumentVM", "On third item clicked" );
            }
        };
    }
    public View.OnLongClickListener onThirdItemLongClicked() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText( context, "Third item long clicked", Toast.LENGTH_SHORT ).show();
                return true;
            }
        };
    }
}
