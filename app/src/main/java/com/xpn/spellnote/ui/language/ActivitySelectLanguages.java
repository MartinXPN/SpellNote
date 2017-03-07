package com.xpn.spellnote.ui.language;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.adapters.AdapterChooseLanguage;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.services.restapi.DictionaryGetterService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


public class ActivitySelectLanguages extends AppCompatActivity implements AdapterChooseLanguage.ItemGetter {

    AdapterChooseLanguage adapter;
    ArrayList <DictionaryModel> dictionaries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_languages);

        /// setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivitySelectLanguages.this.finish();
            }
        });


        /// get list of all dictionaries available on a server
        DictionaryGetterService.loadDictionaries();

        if( adapter == null ) {
            adapter = new AdapterChooseLanguage( this );
        }
        GridView restaurantGrid = (GridView) findViewById( R.id.language_grid );
        restaurantGrid.setAdapter( adapter );


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// call to EventBus
                /// TODO Download dictionaries
                Toast.makeText( ActivitySelectLanguages.this, "Started downloading dictionaries", Toast.LENGTH_LONG ).show();

                /// TODO Remove redundant ones
                /// TODO Update obsolete ones
                ActivitySelectLanguages.this.finish();
            }
        });

//        Log.d( "Saved dictionaries", "" + SavedDictionaries.getSavedDictionaries().size() );
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onDictionariesLoaded( ArrayList <DictionaryModel> dictionaries ) {
        this.dictionaries = dictionaries;
        Toast.makeText( this, "Success", Toast.LENGTH_SHORT ).show();

        if( adapter != null )
            adapter.notifyDataSetChanged();
        else
            Toast.makeText(this, "Adapter is null", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public ArrayList<DictionaryModel> getAllDictionaries() {
        return dictionaries;
    }

    @Override
    public ArrayList<String> getSavedLocales() {
        return new ArrayList<>();
    }
}
