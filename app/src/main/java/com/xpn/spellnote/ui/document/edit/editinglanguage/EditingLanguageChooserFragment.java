package com.xpn.spellnote.ui.document.edit.editinglanguage;

import androidx.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.FragmentEditingLanguageChooserBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.ui.util.tutorial.Tutorial;

import java.util.List;


public class EditingLanguageChooserFragment extends Fragment implements EditingLanguageChooserVM.ViewContract {

    private FragmentEditingLanguageChooserBinding binding;
    private EditingLanguageChooserContract contract;
    private EditingLanguageChooserVM viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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


        /// handle keyboard show-hide
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            Rect r = new Rect();
            binding.getRoot().getWindowVisibleDisplayFrame(r);
            int screenHeight = binding.getRoot().getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                showSelectDictionariesTutorial();   /// redraw tutorial
            }
            else {
                if( selectDictionariesTutorial != null )
                    showSelectDictionariesTutorial();   /// redraw tutorial
            }
        });
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
        if(selectDictionariesTutorial != null)
            selectDictionariesTutorial.setDisplayed();
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

    private Tutorial selectDictionariesTutorial = null;
    private void showSelectDictionariesTutorial() {
        if( selectDictionariesTutorial == null ) {
            selectDictionariesTutorial = new Tutorial(getActivity(), "select_lang_tutorial", R.string.tutorial_select_dictionaries, Gravity.TOP);
            selectDictionariesTutorial.setTarget(binding.currentLanguage);
        }
        selectDictionariesTutorial.showTutorial(true);
    }


    public interface EditingLanguageChooserContract {
        void onDictionaryListAvailable(List<DictionaryModel> dictionaries);
        void onLanguageSelected(DictionaryModel dictionary);
        void onLaunchDictionaryChooser();
    }
}
