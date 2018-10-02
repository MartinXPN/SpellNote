package com.xpn.spellnote.ui.dictionary;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.ActivitySelectLanguagesBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.ui.dictionary.LanguageItemVM.DictionaryListener;
import com.xpn.spellnote.ui.util.ViewUtil;


public class ActivitySelectLanguages extends AppCompatActivity implements SelectLanguagesVM.ViewContract {

    private ActivitySelectLanguagesBinding binding;
    private SelectLanguagesVM viewModel;
    private FirebaseAnalytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_languages);

        /// set-up analytics
        analytics = FirebaseAnalytics.getInstance(this);

        /// setup the toolbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });

        DiContext diContext = ((SpellNoteApp) getApplication()).getDiContext();
        viewModel = new SelectLanguagesVM(this,
                diContext.getAvailableDictionariesService(),
                diContext.getSavedDictionaryService(),
                diContext.getSavedWordsService());

        binding.setViewModel(viewModel);
        int numberOfColumns = (int) (ViewUtil.getWindowWidth(this) / getResources().getDimension(R.dimen.language_grid_column_width));
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
            newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            int numberOfColumns = (int) (ViewUtil.getWindowWidth(this) / getResources().getDimension(R.dimen.language_grid_column_width));
            binding.languagesGrid.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        }
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
    public void onUpdatingDictionary(DictionaryModel dictionary) {
        Bundle bundle = new Bundle();
        bundle.putString("languageName", dictionary.getLanguageName());
        bundle.putString("locale", dictionary.getLocale());
        analytics.logEvent("update_dictionary", bundle);
    }

    @Override
    public void onAskUpdateOrRemove(DictionaryModel dictionary, DictionaryListener listener) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)  builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        else                                                        builder = new AlertDialog.Builder(this);
        builder.setTitle(dictionary.getLanguageName())
                .setMessage(R.string.request_update_or_delete_dictionary)
                .setPositiveButton(R.string.update_or_delete_dictionary_option_update, (dialog, which) -> listener.onUpdate(dictionary))
                .setNegativeButton(R.string.update_or_delete_dictionary_option_delete, (dialog, which) -> listener.onRemove(dictionary))
                .show();
    }

    @Override
    public void showError(@StringRes int message) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(@StringRes int message) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
    }
}
