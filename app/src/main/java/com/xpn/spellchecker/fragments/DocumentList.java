package com.xpn.spellchecker.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.xpn.spellchecker.R;
import com.xpn.spellchecker.activities.EditDocument;

import java.util.ArrayList;
import java.util.List;


public class DocumentList extends Fragment {

    private View rootView = null;
    private LayoutInflater currentInflater = null;
    private List <ArrayList> documentList = new ArrayList<>();      /// the list of created documents
    private ListView documentListView;
    private ListViewAdapter adapter = new ListViewAdapter();                /// adapter for list of created documents
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DocumentList() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SuggestionList newInstance(int columnCount) {
        SuggestionList fragment = new SuggestionList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate( R.layout.fragment_document_list, container, false );
        currentInflater = inflater;
        documentListView = (ListView) rootView.findViewById( R.id.list );
        documentListView.setAdapter( adapter );

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onTrashClick( int listPosition );
        void onArchiveClick( int listPosition );
    }


    private void sendNote( String messageTitle, String messageBody ) {

        Intent i = new Intent( Intent.ACTION_SEND );
        i.setType( "text/plain" );
        i.putExtra( Intent.EXTRA_SUBJECT, messageTitle );
        i.putExtra( Intent.EXTRA_TEXT, messageBody );
        try {
            startActivity( Intent.createChooser( i, "Send Message...") );
        }
        catch( android.content.ActivityNotFoundException ex ) {
            Toast.makeText( getActivity(), "There are no messaging applications installed", Toast.LENGTH_SHORT ).show();
        }
    }



    class ListViewAdapter extends BaseSwipeAdapter {

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

        @Override
        public View generateView(int position, ViewGroup viewGroup) {
            return currentInflater.inflate(R.layout.fragment_document_list_item, null);
        }

        @Override
        public void fillValues( final int position, View convertView ) {

            LinearLayout contentPart = (LinearLayout) convertView.findViewById( R.id.content_part );
            TextView title = (TextView) convertView.findViewById( R.id.title );
            TextView text = (TextView) convertView.findViewById( R.id.text );
            TextView date = (TextView) convertView.findViewById( R.id.date );
            ImageView archive = (ImageView) convertView.findViewById( R.id.copy );
            ImageView trash = (ImageView) convertView.findViewById( R.id.trash );
            ImageView send = (ImageView) convertView.findViewById( R.id.send );
/*
            ArrayList row = documentList.get(position);
            final int id = (Integer) row.get(0);
            final String title_value = (String) row.get(1);
            final String text_value = (String) row.get(2);
            final String creationDate = (String) row.get(3);
*/
            final int id = 1;
            final String title_value = "hello";
            final String text_value = "there";
            final String creationDate = "12:30";

            title.setText( title_value.matches("") ? "Untitled" : title_value );
            text.setText( text_value );
            date.setText(creationDate);

            archive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //getActivity().onArchiveClick( position );
                }
            });

            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.closeAllItems();
                    //mListener.onTrashClick( position );
                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendNote( title_value, text_value );
                }
            });

            contentPart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    adapter.closeAllItems();
                    return false;
                }
            });

            contentPart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent( getActivity(), EditDocument.class );
                    i.putExtra( "id", id );
                    i.putExtra( "title", title_value );
                    i.putExtra( "content", text_value );
                    //startActivityForResult(i, 1);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getCount() {
            return 20;/*documentList.size();*/
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}