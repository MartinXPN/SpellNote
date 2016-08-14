package com.xpn.spellnote.adapters;


import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpn.spellnote.R;

import java.util.ArrayList;

public class AdapterChooseLanguage extends BaseAdapter {

    Fragment fragment;
    Context context;
    ArrayList <LanguagePackage> languages;

    public AdapterChooseLanguage( Fragment fragment ) {
        this.fragment = fragment;
        this.context = fragment.getActivity();

        if( languages == null ) {
            languages = getAllLanguages();
        }
    }

    @Override
    public int getCount() {
        return languages.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View rootView = inflater.inflate( R.layout.language_grid_item, null );
        final ImageView status = (ImageView) rootView.findViewById( R.id.language_package_status );
        final TextView language = (TextView) rootView.findViewById( R.id.language_name );

        language.setText( languages.get( i ).name );


        if( languages.get(i).isDownloaded ) {

            if( languages.get(i).toBeRemoved )  status.setImageResource( R.drawable.ic_remove );
            else                                status.setImageResource( R.drawable.ic_supported );
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( !languages.get(i).toBeRemoved ) { status.setImageResource(R.drawable.ic_remove);        languages.get(i).toBeRemoved = true; }
                    else                                { status.setImageResource( R.drawable.ic_supported );   languages.get(i).toBeRemoved = false; }
                }
            });
        }
        else {
            status.setImageResource( R.drawable.ic_download );
            if( languages.get(i).toBeDownloaded )   status.setVisibility( View.VISIBLE );
            else                                    status.setVisibility( View.GONE );

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (languages.get(i).toBeDownloaded)    { status.setVisibility(View.GONE);      languages.get(i).toBeDownloaded = false; }
                    else                                    { status.setVisibility(View.VISIBLE);   languages.get(i).toBeDownloaded = true; }
                }
            });
        }

        return rootView;
    }

    public ArrayList<LanguagePackage> getAllLanguages() {
        ArrayList <LanguagePackage> res = new ArrayList<>();
        for( int i=0; i < 20; ++i )
            res.add( new LanguagePackage() );
        return res;
    }


    public class LanguagePackage {
        boolean isDownloaded = false;
        boolean toBeRemoved = false;
        boolean toBeDownloaded = false;
        String name = "Armenian";
    }
}
