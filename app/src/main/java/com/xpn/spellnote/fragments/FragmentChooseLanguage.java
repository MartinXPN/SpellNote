package com.xpn.spellnote.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.xpn.spellnote.R;
import com.xpn.spellnote.adapters.AdapterChooseLanguage;

public class FragmentChooseLanguage extends Fragment {


    AdapterChooseLanguage adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance( true );
        adapter = new AdapterChooseLanguage( getActivity() );
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate( R.layout.fragment_choose_language, container, false );
        GridView restaurantGrid = (GridView) rootView.findViewById( R.id.language_grid );
        restaurantGrid.setAdapter( adapter );

        return rootView;
    }
}
