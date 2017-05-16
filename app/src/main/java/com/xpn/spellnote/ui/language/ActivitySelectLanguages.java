package com.xpn.spellnote.ui.language;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.ActivitySelectLanguagesBinding;
import com.xpn.spellnote.ui.util.Util;


public class ActivitySelectLanguages extends AppCompatActivity implements SelectLanguagesVM.ViewContract {

    private ActivitySelectLanguagesBinding binding;
    private SelectLanguagesVM viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_languages);

        /// setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        DiContext diContext = ((SpellNoteApp) getApplication()).getDiContext();
        viewModel = new SelectLanguagesVM(this,
                diContext.getDictionariesService(),
                diContext.getSavedDictionaryService(),
                diContext.getWordsService());

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
                .centerCrop()
                .into(view);
    }
}
