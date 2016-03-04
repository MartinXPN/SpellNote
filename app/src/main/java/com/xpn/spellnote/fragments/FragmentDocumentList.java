package com.xpn.spellnote.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.xpn.spellnote.R;

import java.util.ArrayList;
import java.util.List;


public class FragmentDocumentList extends Fragment {

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
    public FragmentDocumentList() {
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
        documentListView.setAdapter(adapter);

        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnListFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

        void onArchiveClick( int listPosition, View v );
        void onTrashClick( int listPosition, View v );
        void onSendClick( int listPosition, View v );
        void onContentClick( int listPosition, View v );

        String getArchiveExplanation();
        String getTrashExplanation();
        String getSendExplanation();
    }



    public int getIdAt( int position ) {
        return 0;
        //return (String) documentList.get( position ).get( 0 );
    }
    public String getTitleAt( int position ) {
        return "Hello";
        //return (String) documentList.get( position ).get( 1 );
    }
    public String getTextAt( int position ) {
        return "world!";
        //return (String) documentList.get( position ).get( 2 );
    }
    public String getDateAt( int position ) {
        return "date";
        //return (String) documentList.get( position ).get( 3 );
    }
    public ArrayList getRowAt( int position ) {
        return new ArrayList();
        //return documentList.get( position );
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
        public void fillValues( final int position, final View convertView ) {

            RelativeLayout contentPart = (RelativeLayout) convertView.findViewById( R.id.content_part );
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
            final String title_value = "Title No: " + String.valueOf( position );
            final String text_value = "Text No: ";
            final String creationDate = "date\n12:30";

            title.setText( title_value.matches("") ? "Untitled" : title_value );
            text.setText( text_value );
            date.setText( creationDate );


            archive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.closeAllItems();
                    mListener.onArchiveClick(position, v);
                }
            });
            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.closeAllItems();
                    mListener.onTrashClick(position, v);
                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onSendClick(position, v);
                }
            });
            contentPart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onContentClick(position, v);
                }
            });



            archive.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText( getActivity(), mListener.getArchiveExplanation(), Toast.LENGTH_SHORT ).show();
                    return true;
                }
            });
            trash.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText( getActivity(), mListener.getTrashExplanation(), Toast.LENGTH_SHORT ).show();
                    return true;
                }
            });
            send.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getActivity(), mListener.getSendExplanation(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });




            contentPart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    adapter.closeAllItems();
                    return false;
                }
            });
        }

        @Override
        public int getCount() {
            return 100000;/*documentList.size();*/
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