package com.xpn.spellnote.ui.document.edit;

import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.ActivityEditDocumentBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.edit.editinglanguage.EditingLanguageChooserVM;
import com.xpn.spellnote.ui.document.edit.suggestions.SuggestionsVM;
import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.TagsUtil;
import com.xpn.spellnote.util.Util;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class ActivityEditDocument extends AppCompatActivity
        implements EditDocumentVM.ViewContract,
        EditingLanguageChooserVM.ViewContract,
        SuggestionsVM.ViewContract {

    private static final String EXTRA_DOCUMENT_ID = "doc_id";
    private static final Integer SPEECH_RECOGNIZER_CODE = 1;
    private boolean showSuggestions;
    private boolean checkSpelling;

    private ActivityEditDocumentBinding binding;
    private EditDocumentVM viewModel;
    private EditingLanguageChooserVM editingLanguageChooserVM;
    private SuggestionsVM suggestionsVM;


    public static void launchForResult(Fragment fragment, Long documentId, int requestCode) {
        Intent i = new Intent( fragment.getActivity(), ActivityEditDocument.class );
        i.putExtra( EXTRA_DOCUMENT_ID, documentId );
        Timber.d("Starting activity for result with request code: " + requestCode);
        fragment.startActivityForResult( i, requestCode );
    }

    public static void launchForResult(Fragment fragment, String category, int requestCode) {
        DocumentModel document = new DocumentModel();
        document.setCategory(category);

        DiContext diContext = ((SpellNoteApp) fragment.getActivity().getApplication()).getDiContext();
        diContext.getDocumentService()
                .saveDocument(document)
                .subscribe(
                        () -> launchForResult(fragment, document.getId(), requestCode ),
                        throwable -> {
                            Toast.makeText(fragment.getActivity(), "Couldn't create document", Toast.LENGTH_SHORT).show();
                            Timber.e(throwable);
                        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_document);

        /// set-up view-models
        DiContext diContext = ((SpellNoteApp) getApplication()).getDiContext();
        viewModel = new EditDocumentVM(this,
                getIntent().getExtras().getLong(EXTRA_DOCUMENT_ID),
                diContext.getDocumentService(),
                diContext.getSpellCheckerService());

        editingLanguageChooserVM = new EditingLanguageChooserVM(this, diContext.getSavedDictionaryService());
        suggestionsVM = new SuggestionsVM(this, diContext.getSuggestionService());

        binding.setModel(viewModel);
        binding.setEditingLanguageChooserVM(editingLanguageChooserVM);
        binding.setSuggestionsVM(suggestionsVM);

        /// set-up the actionbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());


        /// set-up editing language chooser
        int numberOfItems = 3; /// number of dictionaries shown in one row
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfItems);
        layoutManager.setAutoMeasureEnabled(true);
        binding.editingLanguageChooser.supportedLanguagesGrid.setHasFixedSize(true);
        binding.editingLanguageChooser.supportedLanguagesGrid.setNestedScrollingEnabled(false);
        binding.editingLanguageChooser.supportedLanguagesGrid.setLayoutManager(layoutManager);


        /// set-up edit-correct text
        binding.content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int left = start - 10;
                int right = start + count + 10;
                List<String> words = binding.content.getWords(
                        binding.content.getWordStart(left),
                        binding.content.getWordEnd(right)
                );

                /// show suggestions only if the current word has more than one character
                if( getCurrentWord().length() > 1 ) suggestionsVM.suggest(getCurrentWord());
                else                                onHideSuggestions();
                viewModel.checkSpelling(left, right, words);
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onSaveDocument();
            }
        });
        binding.content.setOnClickListener(v -> {
            /// show suggestions only if the current word has more than one character
            if( getCurrentWord().length() > 1 ) suggestionsVM.suggest(getCurrentWord());
            else                                onHideSuggestions();
        });
    }

    @Override
    protected void onStart() {
        viewModel.onStart();
        editingLanguageChooserVM.onStart();
        suggestionsVM.onStart();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        viewModel.onSaveDocument();
        viewModel.onDestroy();
        editingLanguageChooserVM.onDestroy();
        suggestionsVM.onDestroy();
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_document, menu);
        updateShowSuggestions( CacheUtil.getCache( this, TagsUtil.USER_PREFERENCE_SHOW_SUGGESTIONS, true ), menu.findItem( R.id.action_show_suggestions ) );
        updateSpellChecking( CacheUtil.getCache( this, TagsUtil.USER_PREFERENCE_CHECK_SPELLING, true ), menu.findItem( R.id.action_check_spelling ) );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.action_show_suggestions )    { updateShowSuggestions( !showSuggestions, item );                          return true; }
        else if( id == R.id.action_record )         { Util.displaySpeechRecognizer( this, SPEECH_RECOGNIZER_CODE );             return true; }
        else if( id == R.id.action_send )           { Util.sendDocument( this, "", binding.content.getText().toString() );      return true; }
        else if( id == R.id.action_copy )           { Util.copyTextToClipboard( this, binding.content.getText().toString() );   return true; }
        else if( id == R.id.action_check_spelling ) { updateSpellChecking( !checkSpelling, item );                              return true; }

        return super.onOptionsItemSelected(item);
    }


    public void updateShowSuggestions( boolean showSuggestions, MenuItem item ) {
        this.showSuggestions = showSuggestions;
        CacheUtil.setCache( this, TagsUtil.USER_PREFERENCE_SHOW_SUGGESTIONS, showSuggestions );

        if( showSuggestions )   item.setIcon( R.mipmap.ic_show_suggestions );
        else                    item.setIcon( R.mipmap.ic_hide_suggestions );
    }
    public void updateSpellChecking( boolean checkSpelling, MenuItem item ) {
        this.checkSpelling = checkSpelling;
        CacheUtil.setCache( this, TagsUtil.USER_PREFERENCE_CHECK_SPELLING, checkSpelling );

        item.setChecked( checkSpelling );
        binding.content.setSpellCheckingEnabled(checkSpelling);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SPEECH_RECOGNIZER_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList <String> results = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
            String spokenText = results.get(0);
            binding.content.replaceSelection(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public DictionaryModel getCurrentDictionary() {
        return editingLanguageChooserVM.getCurrentLanguage();
    }

    @Override
    public void markIncorrect(int left, int right, List<String> incorrectWords) {
        Timber.d("Mark Incorrect: " + incorrectWords);
        for( String word : incorrectWords ) {
            binding.content.markWord( word, left, right, ContextCompat.getColor(this, R.color.text_wrong));
        }
    }

    @Override
    public void markCorrect(int left, int right, List<String> correctWords) {
        Timber.d("Mark Correct: " + correctWords);
        for( String word : correctWords ) {
            binding.content.markWord( word, left, right, ContextCompat.getColor(this, R.color.text_correct));
        }
    }

    @Override
    public void onLanguageSelected(DictionaryModel dictionary) {
        hideAvailableLanguages();
        editingLanguageChooserVM.setCurrentLanguage(dictionary);
    }

    @Override
    public void showAvailableLanguages() {
        binding.editingLanguageChooser.currentLanguage.setVisibility( View.GONE );
        binding.editingLanguageChooser.supportedLanguagesCard.setVisibility( View.VISIBLE );
    }

    @Override
    public void hideAvailableLanguages() {
        binding.editingLanguageChooser.supportedLanguagesCard.setVisibility( View.GONE );
        binding.editingLanguageChooser.currentLanguage.setVisibility( View.VISIBLE );
    }

    @Override
    public boolean isLanguageListOpen() {
        return binding.editingLanguageChooser.supportedLanguagesCard.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onSuggestionSelected(String suggestion) {
        binding.content.replaceCurrentWord(suggestion);
        onHideSuggestions();
    }

    @Override
    public String getCurrentWord() {
        return binding.content.getCurrentWord().toString();
    }

    @Override
    public DictionaryModel getCurrentLanguage() {
        return editingLanguageChooserVM.getCurrentLanguage();
    }

    @Override
    public void onShowSuggestions() {
        if( !showSuggestions || suggestionsVM.getSuggestionVMs().isEmpty() ) {
            onHideSuggestions();
            return;
        }

        float h = binding.contentScroll.getScrollY();
        float x = binding.content.getCursorPosition().first;
        float y = binding.content.getCursorPosition().second;

        y -= h + binding.suggestions.getPaddingTop();
        x -= binding.suggestions.getWidth() / 2;

        x = Math.min( x, binding.content.getWidth() - binding.suggestions.getWidth() );
        x = Math.max( x, 0 );
        y = Math.max( y, 0 );

        binding.suggestions.setX(x);
        binding.suggestions.setY(y);
        binding.suggestions.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideSuggestions() {
        binding.suggestions.setVisibility(View.GONE);
    }
}
