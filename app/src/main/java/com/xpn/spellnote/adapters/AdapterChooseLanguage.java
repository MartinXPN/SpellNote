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

    private Fragment fragment;
    private ArrayList <LanguagePackage> languages;
    private ViewHolder holder;

    public AdapterChooseLanguage( Fragment fragment ) {
        this.fragment = fragment;

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

        if( view == null ) {

            LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate( R.layout.language_grid_item, viewGroup, false );
            holder = new ViewHolder( view );
            view.setTag( holder );
        }
        else {
            holder = (ViewHolder) view.getTag();
        }


        final ImageView flag = holder.languageFlag;
        final ImageView status = holder.languagePackageStatus;
        final TextView language = holder.languageName;

        final LanguagePackage currentItem = languages.get( i );

        flag.setImageResource( currentItem.flagDrawableResId );
        language.setText( currentItem.name );
        status.setImageResource( currentItem.statusDrawableResId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentItem.flipStatus();
                status.setImageResource( currentItem.statusDrawableResId);
            }
        });

        return view;
    }

    public ArrayList<LanguagePackage> getAllLanguages() {
        ArrayList <LanguagePackage> res = new ArrayList<>();

        for( int i=0; i < 5; ++i )
            res.add( new LanguagePackage( LanguagePackage.Status.SUPPORTED, "English" ) );

        for( int i=0; i < 20; ++i )
            res.add( new LanguagePackage(LanguagePackage.Status.NEUTRAL, "Armenian" ) );
        return res;
    }

    private static class ViewHolder {
        private ImageView languageFlag;
        private ImageView languagePackageStatus;
        private TextView languageName;

        ViewHolder(View v) {
            languageFlag = (ImageView) v.findViewById( R.id.language_flag );
            languagePackageStatus = (ImageView) v.findViewById( R.id.language_package_status );
            languageName = (TextView) v.findViewById( R.id.language_name );
        }
    }


    private static class LanguagePackage {

        enum Status {
            NEUTRAL,
            SUPPORTED,
            TOBEREMOVED,
            TOBEDOWNLOADED
        }

        LanguagePackage(Status status, String name) {
            this.status = status;
            this.name = name;
            this.flagDrawableResId = R.mipmap.ic_flag_arm;

            switch ( status ) {
                case SUPPORTED:         statusDrawableResId = R.drawable.ic_supported;    break;
                case TOBEREMOVED:       statusDrawableResId = R.drawable.ic_remove;       break;
                case TOBEDOWNLOADED:    statusDrawableResId = R.drawable.ic_download;     break;
                case NEUTRAL:           statusDrawableResId = R.drawable.ic_nothing;      break;
            }
        }

        void flipStatus() {

            switch ( status ) {
                case SUPPORTED:         status = Status.TOBEREMOVED;    statusDrawableResId = R.drawable.ic_remove;       break;
                case TOBEREMOVED:       status = Status.SUPPORTED;      statusDrawableResId = R.drawable.ic_supported;    break;
                case TOBEDOWNLOADED:    status = Status.NEUTRAL;        statusDrawableResId = R.drawable.ic_nothing;      break;
                case NEUTRAL:           status = Status.TOBEDOWNLOADED; statusDrawableResId = R.drawable.ic_download;     break;
            }
        }

        int statusDrawableResId;
        Status status = Status.NEUTRAL;
        int flagDrawableResId;
        String name = "Armenian";
    }
}
