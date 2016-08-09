package com.xpn.spellnote.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.xpn.spellnote.R;

public class AdapterChooseLanguage extends BaseAdapter {


    Context context;
    public AdapterChooseLanguage( Context context ) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 10;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View rootView = inflater.inflate( R.layout.language_grid_item, null );
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( context, "hello", Toast.LENGTH_SHORT ).show();
            }
        });
        return rootView;
    }
}
