package com.xpn.spellnote.ui.document.edit.editinglanguage;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.FragmentEditingLanguageChooserBinding;
import com.xpn.spellnote.models.DictionaryModel;

import java.util.List;


public class EditingLanguageChooserFragment extends Fragment implements EditingLanguageChooserVM.ViewContract {

    private FragmentEditingLanguageChooserBinding binding;
    private EditingLanguageChooserContract contract;
    private EditingLanguageChooserVM viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_editing_language_chooser, container, false);
        contract = (EditingLanguageChooserContract) getActivity();

        /// set-up viewModel
        DiContext diContext = ((SpellNoteApp)getActivity().getApplication()).getDiContext();
        viewModel = new EditingLanguageChooserVM(this, diContext.getSavedDictionaryService());
        binding.setViewModel(viewModel);

        /// set-up editing language chooser
        float languageGridItemSize = getResources().getDimension(R.dimen.language_chooser_grid_item_size);
        float languageGridWidth = getResources().getDimension(R.dimen.language_chooser_grid_width);
        int numberOfItems = (int) (languageGridWidth / languageGridItemSize);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numberOfItems);
        layoutManager.setAutoMeasureEnabled(true);
        binding.supportedLanguagesGrid.setHasFixedSize(true);
        binding.supportedLanguagesGrid.setNestedScrollingEnabled(false);
        binding.supportedLanguagesGrid.setLayoutManager(layoutManager);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    @Override
    public void onDestroy() {
        viewModel.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDictionaryListAvailable(List<DictionaryModel> dictionaries) {
        contract.onDictionaryListAvailable(dictionaries);
    }

    @Override
    public void onLanguageSelected(DictionaryModel dictionary) {
        hideAvailableLanguages();
        viewModel.setCurrentLanguage(dictionary);
        contract.onLanguageSelected(dictionary);
    }

    @Override
    public void onLaunchLanguageChooser() {
        contract.onLaunchDictionaryChooser();
    }

    @Override
    public void showAvailableLanguages() {
        binding.currentLanguage.setVisibility( View.GONE );
        binding.supportedLanguagesCard.setVisibility( View.VISIBLE );
    }

    @Override
    public void hideAvailableLanguages() {
        binding.supportedLanguagesCard.setVisibility( View.GONE );
        binding.currentLanguage.setVisibility( View.VISIBLE );
    }

    @Override
    public boolean isLanguageListOpen() {
        return binding.supportedLanguagesCard.getVisibility() == View.VISIBLE;
    }


    public DictionaryModel getCurrentDictionary() {
        return viewModel.getCurrentLanguage();
    }

    public void loadSupportedDictionaries() {
        viewModel.loadSupportedDictionaries();
    }

    public void setCurrentLanguage(String locale) {
        viewModel.setCurrentLanguage(locale);
    }

    public View getCurrentLanguageLogoView() {
        return binding.currentLanguage;
    }


    public interface EditingLanguageChooserContract {
        void onDictionaryListAvailable(List<DictionaryModel> dictionaries);
        void onLanguageSelected(DictionaryModel dictionary);
        void onLaunchDictionaryChooser();
    }
}
