package com.xpn.spellnote.ui.language;

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
import com.xpn.spellnote.models.DictionaryModel;

import java.util.ArrayList;
import java.util.Collections;


public class AdapterChooseLanguage extends BaseAdapter {

    private Activity activity;
    private ArrayList <DictionaryModel> dictionaries;
    private ArrayList <String> savedLocales;
    private ArrayList <String> tobeDownloadedLocales;
    private ArrayList <String> tobeRemovedLocales;
    private ItemGetter listener;

    public interface ItemGetter {
        ArrayList <DictionaryModel> getAllDictionaries();
        ArrayList <String> getSavedLocales();
    }

    public AdapterChooseLanguage( Activity activity ) {
        this.activity = activity;
        this.listener = (ItemGetter) activity;
        this.dictionaries = listener.getAllDictionaries();
        this.savedLocales = listener.getSavedLocales();
        if( tobeDownloadedLocales == null ) tobeDownloadedLocales = new ArrayList<>();
        if( tobeRemovedLocales == null )    tobeRemovedLocales = new ArrayList<>();
        Log.d( "Saved locales", savedLocales.toString() );
    }

    @Override
    public int getCount() {
        return dictionaries.size();
    }

    @Override
    public void notifyDataSetChanged() {
        dictionaries = listener.getAllDictionaries();
        savedLocales = listener.getSavedLocales();
        Collections.sort(dictionaries, (a, b) -> {    /// first show downloaded dictionaries
            int res = 0;
            if( savedLocales.contains( a.getLocale() ) )   res--;
            if( savedLocales.contains( b.getLocale() ) )   res++;
            return res;
        });
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
            view = inflater.inflate( R.layout.item_language_grid, viewGroup, false );
            holder = new ViewHolder( view );
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }


        final ImageView flag = holder.languageFlag;
        final ImageView status = holder.languagePackageStatus;
        final TextView language = holder.languageName;
        final DictionaryModel currentItem = dictionaries.get( i );

        if( tobeDownloadedLocales.contains( currentItem.getLocale() ) )      { status.setImageResource( R.drawable.ic_download ); }
        else if( tobeRemovedLocales.contains( currentItem.getLocale() ) )    { status.setImageResource( R.drawable.ic_remove ); }
        else if( savedLocales.contains( currentItem.getLocale() ) )          { status.setImageResource( R.drawable.ic_supported ); }
        else                                                            { status.setImageResource( R.drawable.ic_nothing ); }

        Picasso.with(activity)
                .load(currentItem.getLogoURL())
                .placeholder(ContextCompat.getDrawable(activity, R.mipmap.ic_placeholder))
                .resizeDimen(R.dimen.language_flag_size, R.dimen.language_flag_size)
                .centerCrop()
                .into(flag);

        language.setText( currentItem.getLanguageName() );
        view.setOnClickListener(view1 -> {
            if( tobeDownloadedLocales.contains( currentItem.getLocale() ) )      { tobeDownloadedLocales.remove( currentItem.getLocale() ); }
            else if( tobeRemovedLocales.contains( currentItem.getLocale() ) )    { tobeRemovedLocales.remove( currentItem.getLocale() ); }
            else if( savedLocales.contains( currentItem.getLocale() ) )          { tobeRemovedLocales.add( currentItem.getLocale() ); }
            else                                                                 { tobeDownloadedLocales.add( currentItem.getLocale() ); }

            notifyDataSetChanged();
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
}
