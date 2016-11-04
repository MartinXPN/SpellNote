package com.xpn.spellnote.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

        View view = inflater.inflate(R.layout.fragment_choose_editing_language, container, false);
        int numberOfItems = 3; /// number of dictionaries shown in one row

        supportedLanguagesCard = (CardView) view.findViewById( R.id.supported_languages_card );
        supportedLanguagesGrid = (RecyclerView) view.findViewById( R.id.supported_languages_grid);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numberOfItems);
        layoutManager.setAutoMeasureEnabled(true);
        assert supportedLanguagesGrid != null;
        supportedLanguagesGrid.setHasFixedSize(true);
        supportedLanguagesGrid.setNestedScrollingEnabled(false);
        supportedLanguagesGrid.setLayoutManager(layoutManager);
        supportedLanguagesGrid.setAdapter(adapter);


        currentLanguage = (ImageButton) view.findViewById( R.id.current_language );
        currentLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAvailableDictionaries();
            }
        });
        return view;
    }


    @Override
    public void onItemClicked(int position) {
        hideAvailableDictionaries();
        // EventBus.getDefault().post( new LanguageSchema("hy") );
    }


    public void showAvailableDictionaries() {
        currentLanguage.setVisibility( View.GONE );
        supportedLanguagesCard.setVisibility( View.VISIBLE );
    }
    public void hideAvailableDictionaries() {
        supportedLanguagesCard.setVisibility( View.GONE );
        currentLanguage.setVisibility( View.VISIBLE );
    }
}
