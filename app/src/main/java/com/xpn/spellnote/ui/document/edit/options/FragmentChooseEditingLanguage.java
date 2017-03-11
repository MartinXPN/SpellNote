package com.xpn.spellnote.ui.document.edit.options;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.FragmentChooseEditingLanguageBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.ui.util.BindingHolder;

import java.util.ArrayList;


public class FragmentChooseEditingLanguage extends Fragment implements EditingLanguageListItemVM.ViewContract {

    OnLanguageSelectedListener listener;
    ArrayList<DictionaryModel> supportedDictionaries = new ArrayList<>();
    ChooseEditingLanguageAdapter adapter = new ChooseEditingLanguageAdapter();
    FragmentChooseEditingLanguageBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (OnLanguageSelectedListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_editing_language, container, false);
        int numberOfItems = 3; /// number of dictionaries shown in one row

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numberOfItems);
        layoutManager.setAutoMeasureEnabled(true);
        binding.supportedLanguagesGrid.setHasFixedSize(true);
        binding.supportedLanguagesGrid.setNestedScrollingEnabled(false);
        binding.supportedLanguagesGrid.setLayoutManager(layoutManager);
        binding.supportedLanguagesGrid.setAdapter(adapter);

        binding.currentLanguage.setOnClickListener(view -> showAvailableLanguages());
        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            if( isLanguageListOpen() ) {
                hideAvailableLanguages();
                return true;
            }
            return false;
        });
        return binding.getRoot();
    }


    public void showAvailableLanguages() {
        binding.currentLanguage.setVisibility( View.GONE );
        binding.supportedLanguagesCard.setVisibility( View.VISIBLE );
    }
    public void hideAvailableLanguages() {
        binding.supportedLanguagesCard.setVisibility( View.GONE );
        binding.currentLanguage.setVisibility( View.VISIBLE );
    }
    public boolean isLanguageListOpen() {
        return binding.supportedLanguagesCard.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onLanguageSelected(DictionaryModel dictionary) {
        hideAvailableLanguages();
        listener.onLanguageSelected(dictionary);
    }


    private class ChooseEditingLanguageAdapter extends RecyclerView.Adapter<BindingHolder> {

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder(LayoutInflater.from( parent.getContext() ).inflate(R.layout.item_editing_language_grid, parent, false));
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            EditingLanguageListItemVM listItemVM = new EditingLanguageListItemVM(FragmentChooseEditingLanguage.this, supportedDictionaries.get(position));
            holder.getBinding().setVariable( BR.model, listItemVM );
            holder.getBinding().executePendingBindings();
        }

        @Override
        public int getItemCount() {
            return supportedDictionaries.size();
        }
    }


    public interface OnLanguageSelectedListener {
        void onLanguageSelected(DictionaryModel dictionary);
    }
}
