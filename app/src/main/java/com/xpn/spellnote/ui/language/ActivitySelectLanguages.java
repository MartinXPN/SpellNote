package com.xpn.spellnote.ui.language;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.GridView;
import android.widget.Toast;

import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.models.DictionaryModel;

import java.util.ArrayList;


public class ActivitySelectLanguages extends AppCompatActivity implements AdapterChooseLanguage.ItemGetter, SelectLanguagesVM.ViewContract {

    private AdapterChooseLanguage adapter;
    private SelectLanguagesVM viewModel;
    private ArrayList <DictionaryModel> dictionaries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_languages);

        /// setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        DiContext diContext = ((SpellNoteApp) getApplication()).getDiContext();
        viewModel = new SelectLanguagesVM(this,
                diContext.getDictionariesService(),
                diContext.getSavedDictionaryService());

        if( adapter == null ) {
            adapter = new AdapterChooseLanguage( this );
        }
        GridView restaurantGrid = (GridView) findViewById( R.id.language_grid );
        restaurantGrid.setAdapter( adapter );
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.onStart();
        viewModel.loadDictionaries();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public ArrayList<DictionaryModel> getAllDictionaries() {
        return dictionaries;
    }

    @Override
    public ArrayList<String> getSavedLocales() {
        return new ArrayList<>();
    }

    @Override
    public void onDictionariesLoaded(ArrayList<DictionaryModel> allDictionaries, ArrayList<DictionaryModel> savedDictionaries) {
        this.dictionaries = allDictionaries;
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "There are " + allDictionaries.size() + " dictionaries", Toast.LENGTH_SHORT).show();
    }
}
