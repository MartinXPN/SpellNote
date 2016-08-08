package com.xpn.spellnote.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.xpn.spellnote.R;
import com.xpn.spellnote.activities.ActivityEditDocument;

import java.util.ArrayList;

public class BaseAdapterDocumentList extends BaseSwipeAdapter {

    protected Context context;

    public BaseAdapterDocumentList( Context context ) {
        this.context = context;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.fragment_document_list_item, null);
    }

    @Override
    public void fillValues( final int position, final View convertView ) {

        final RelativeLayout contentPart = (RelativeLayout) convertView.findViewById( R.id.content_part );
        final TextView title = (TextView) convertView.findViewById( R.id.title );
        final TextView text = (TextView) convertView.findViewById( R.id.text );
        final TextView date = (TextView) convertView.findViewById( R.id.date );
        final ImageView archive = (ImageView) convertView.findViewById( R.id.archive );
        final ImageView trash = (ImageView) convertView.findViewById( R.id.trash );
        final ImageView send = (ImageView) convertView.findViewById( R.id.send );

        final String titleValue = getTitleAt( position );
        final String textValue = getTextAt( position );
        final String creationDate = getDateAt( position );

        title.setText( titleValue.matches("") ? "Untitled" : titleValue );
        text.setText( textValue );
        date.setText( creationDate );


        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAdapterDocumentList.this.closeAllItems();
                onArchiveClick(position, v);
            }
        });
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAdapterDocumentList.this.closeAllItems();
                onTrashClick(position, v);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClick(position, v);
            }
        });
        contentPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContentClick(position, v);
            }
        });



        archive.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText( context, getArchiveExplanation(), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });
        trash.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText( context, getTrashExplanation(), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });
        send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getSendExplanation(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });




        contentPart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                BaseAdapterDocumentList.this.closeAllItems();
                return false;
            }
        });
    }

    @Override
    public int getCount() {
        return 100;/*documentList.size();*/
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }






    public int getIdAt( int position ) {
        return 0;
        //return (String) documentList.get( position ).get( 0 );
    }
    public String getTitleAt( int position ) {
        return "Title No: " + String.valueOf( position );
        //return (String) documentList.get( position ).get( 1 );
    }
    public String getTextAt( int position ) {
        return "Text No: ";
        //return (String) documentList.get( position ).get( 2 );
    }
    public String getDateAt( int position ) {
        return "date\n12:30";
        //return (String) documentList.get( position ).get( 3 );
    }
    public ArrayList getRowAt(int position ) {
        return new ArrayList();
        //return documentList.get( position );
    }






    public void onArchiveClick(int listPosition, View v) {

        Snackbar.make( v, "Archived", Snackbar.LENGTH_LONG ).setAction( "UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        } ).show();
    }

    public void onTrashClick(int listPosition, View v) {

        Snackbar.make( v, "Moved to trash", Snackbar.LENGTH_LONG ).setAction( "UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        } ).show();
    }

    public void onSendClick(int listPosition, View v) {

        Intent i = new Intent( Intent.ACTION_SEND );
        i.setType( "text/plain" );
        i.putExtra( Intent.EXTRA_SUBJECT, getTitleAt( listPosition ) );
        i.putExtra( Intent.EXTRA_TEXT, getTextAt( listPosition ) );
        try {
            context.startActivity( Intent.createChooser( i, "Send Message...") );
        }
        catch( android.content.ActivityNotFoundException ex ) {
            Toast.makeText( context, "There are no messaging applications installed", Toast.LENGTH_SHORT ).show();
        }
    }

    public void onContentClick(int listPosition, View v) {

        Intent i = new Intent( context, ActivityEditDocument.class );
        i.putExtra( "id", getIdAt( listPosition ) );
        i.putExtra( "title", getTitleAt( listPosition ) );
        i.putExtra( "content", getTitleAt( listPosition ) );
        context.startActivity(i);
    }

    public String getArchiveExplanation() {
        return "Archive";
    }

    public String getTrashExplanation() {
        return "Move to trash";
    }

    public String getSendExplanation() {
        return "Send";
    }
}