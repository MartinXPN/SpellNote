package com.xpn.spellnote.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.xpn.spellnote.R;
import com.xpn.spellnote.adapters.AdapterChooseEditingLanguage;


public class FragmentChooseEditingLanguage extends Fragment implements AdapterChooseEditingLanguage.OnItemClickListener {

    AdapterChooseEditingLanguage adapter;
    CardView supportedLanguagesCard;
    RecyclerView supportedLanguagesGrid;
    ImageButton currentLanguage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new AdapterChooseEditingLanguage( this );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_choose_editing_language, container, false);
        int numberOfItems = 3; /// number of dictionaries shown in one row

        supportedLanguagesCard = (CardView) rootView.findViewById( R.id.supported_languages_card );
        supportedLanguagesGrid = (RecyclerView) rootView.findViewById( R.id.supported_languages_grid);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numberOfItems);
        layoutManager.setAutoMeasureEnabled(true);
        assert supportedLanguagesGrid != null;
        supportedLanguagesGrid.setHasFixedSize(true);
        supportedLanguagesGrid.setNestedScrollingEnabled(false);
        supportedLanguagesGrid.setLayoutManager(layoutManager);
        supportedLanguagesGrid.setAdapter(adapter);


        currentLanguage = (ImageButton) rootView.findViewById( R.id.current_language );
        currentLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAvailableLanguages();
            }
        });

        /// set-up the controlling view
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d( "controller", "onTouch" );
                if( isLanguageListOpen() ) {
                    hideAvailableLanguages();
                    return true;
                }
                return false;
            }
        });
        return rootView;
    }


    @Override
    public void onItemClicked(int position) {
        hideAvailableLanguages();
        // EventBus.getDefault().post( new DictionaryModel("hy") );
    }


    public void showAvailableLanguages() {
        currentLanguage.setVisibility( View.GONE );
        supportedLanguagesCard.setVisibility( View.VISIBLE );
    }
    public void hideAvailableLanguages() {
        supportedLanguagesCard.setVisibility( View.GONE );
        currentLanguage.setVisibility( View.VISIBLE );
    }
    public boolean isLanguageListOpen() {
        return supportedLanguagesCard.getVisibility() == View.VISIBLE;
    }
}
