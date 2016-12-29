package com.xpn.spellnote.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DictionarySchema;

import java.util.ArrayList;

public class AdapterChooseLanguage extends BaseAdapter {

    private Activity activity;
    private ArrayList <DictionarySchema> dictionaries;
    private ItemGetter listener;

    public interface ItemGetter {
        ArrayList<DictionarySchema> getAllDictionaries();
    }

    public AdapterChooseLanguage( Activity activity ) {
        this.activity = activity;
        this.listener = (ItemGetter) activity;
        this.dictionaries = listener.getAllDictionaries();
    }

    @Override
    public int getCount() {
        Log.d( "getCount -> ", "" + dictionaries.size() );
        return dictionaries.size();
    }

    @Override
    public void notifyDataSetChanged() {
        dictionaries = listener.getAllDictionaries();
        super.notifyDataSetChanged();
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

        ViewHolder holder;
        if( view == null ) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate( R.layout.language_grid_item, viewGroup, false );
            holder = new ViewHolder( view );
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }


        final ImageView flag = holder.languageFlag;
        final ImageView status = holder.languagePackageStatus;
        final TextView language = holder.languageName;
        final DictionarySchema currentItem = dictionaries.get( i );

        Picasso.with(activity)
                .load(currentItem.logoURL)
                .placeholder(ContextCompat.getDrawable(activity, R.mipmap.ic_placeholder))
                .resizeDimen(R.dimen.language_flag_size, R.dimen.language_flag_size)
                .centerCrop()
                .into(flag);

        language.setText( currentItem.languageName );
        status.setImageResource( R.drawable.ic_supported );
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                currentItem.flipStatus();
//                status.setImageResource( currentItem.statusDrawableResId);
            }
        });

        return view;
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
