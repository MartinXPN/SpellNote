package com.xpn.spellnote.adapters;

import android.content.Context;
import android.content.Intent;
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

public abstract class BaseAdapterDocumentList extends BaseSwipeAdapter {

    protected Context context;
    protected ArrayList <DocumentData> documentList = new ArrayList<>();

    public interface ItemInteractionListener {
        void onClick( int listPosition, View v );
        int getDrawableResId();
        String getExplanation();
    }
    ItemInteractionListener archive;
    ItemInteractionListener trash;
    ItemInteractionListener send;
    public abstract ItemInteractionListener getArchiveListener();
    public abstract ItemInteractionListener getTrashListener();
    public abstract ItemInteractionListener getSendListener();



    public BaseAdapterDocumentList( Context context ) {
        this.context = context;
        documentList = getDocumentList();

        archive = getArchiveListener();
        trash = getTrashListener();
        send = getSendListener();
    }

    public abstract ArrayList <DocumentData> getDocumentList();

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_document_list_item, null);
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

        final String titleValue = documentList.get( position ).getTitle();
        final String textValue = documentList.get( position ).getText();
        final String dateValue = documentList.get( position ).getDate();

        title.setText( titleValue.matches("") ? "Untitled" : titleValue );
        text.setText( textValue );
        date.setText( dateValue );

        archive.setImageResource( this.archive.getDrawableResId() );
        trash.setImageResource( this.trash.getDrawableResId() );
        send.setImageResource( this.send.getDrawableResId() );


        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAdapterDocumentList.this.closeAllItems();
                BaseAdapterDocumentList.this.archive.onClick( position, v );
            }
        });
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAdapterDocumentList.this.closeAllItems();
                BaseAdapterDocumentList.this.trash.onClick( position, v );
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAdapterDocumentList.this.send.onClick( position, v );
            }
        });
        contentPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContentClick( position, v );
            }
        });



        archive.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText( context, BaseAdapterDocumentList.this.archive.getExplanation(), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });
        trash.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText( context, BaseAdapterDocumentList.this.trash.getExplanation(), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });
        send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, BaseAdapterDocumentList.this.send.getExplanation(), Toast.LENGTH_SHORT).show();
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
        return documentList.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return documentList.get( position ).getId();
    }




    private void onContentClick(int position, View v) {

        Intent i = new Intent( context, ActivityEditDocument.class );
        i.putExtra( "id", documentList.get( position ).getId() );
        i.putExtra( "title", documentList.get( position ).getTitle() );
        i.putExtra( "text", documentList.get( position ).getText() );
        context.startActivity(i);
    }



    public class DocumentData {
        private String title;
        private String text;
        private String date;
        private Long id;

        public DocumentData( String title, String text, String date, Long id ) {
            this.title = title;
            this.text = text;
            this.date = date;
            this.id = id;
        }

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }

        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
    }
}