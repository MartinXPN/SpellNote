package com.xpn.spellnote.ui.dictionary;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;
import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.ActivitySelectLanguagesBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.ui.util.Util;


public class ActivitySelectLanguages extends AppCompatActivity implements SelectLanguagesVM.ViewContract {

    private SelectLanguagesVM viewModel;
    private FirebaseAnalytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectLanguagesBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_select_languages);

        /// set-up analytics
        analytics = FirebaseAnalytics.getInstance(this);

        /// setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        DiContext diContext = ((SpellNoteApp) getApplication()).getDiContext();
        viewModel = new SelectLanguagesVM(this,
                diContext.getAvailableDictionariesService(),
                diContext.getSavedDictionaryService());

        binding.setViewModel(viewModel);
        int numberOfColumns = (int) (Util.getWindowWidth(this) / getResources().getDimension(R.dimen.language_grid_column_width));
        binding.languagesGrid.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
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


    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Picasso.with(view.getContext())
                .load(url)
                .placeholder(ContextCompat.getDrawable(view.getContext(), R.mipmap.ic_placeholder))
                .resizeDimen(R.dimen.language_flag_size, R.dimen.language_flag_size)
                .centerInside()
                .into(view);
    }


    @Override
    public void onDownloadingDictionary(DictionaryModel dictionary) {
        Bundle bundle = new Bundle();
        bundle.putString("languageName", dictionary.getLanguageName());
        bundle.putString("locale", dictionary.getLocale());
        analytics.logEvent("download_dictionary", bundle);
    }

    @Override
    public void onRemovingDictionary(DictionaryModel dictionary) {
        Bundle bundle = new Bundle();
        bundle.putString("languageName", dictionary.getLanguageName());
        bundle.putString("locale", dictionary.getLocale());
        analytics.logEvent("remove_dictionary", bundle);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
