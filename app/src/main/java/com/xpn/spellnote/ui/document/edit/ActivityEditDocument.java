package com.xpn.spellnote.ui.document.edit;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.ActivityEditDocumentBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.ui.ads.AdsActivity;
import com.xpn.spellnote.ui.dictionary.ActivitySelectLanguages;
import com.xpn.spellnote.ui.document.edit.editinglanguage.EditingLanguageChooserFragment;
import com.xpn.spellnote.ui.document.edit.suggestions.SuggestionsVM;
import com.xpn.spellnote.ui.util.BaseShowCaseTutorial;
import com.xpn.spellnote.ui.util.CurrentWordCorrectnessListener;
import com.xpn.spellnote.ui.util.ToolbarActionItemTarget;
import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.TagsUtil;
import com.xpn.spellnote.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import timber.log.Timber;


public class ActivityEditDocument extends AppCompatActivity
        implements EditDocumentVM.ViewContract,
        EditingLanguageChooserFragment.EditingLanguageChooserContract,
        SuggestionsVM.ViewContract {

    private static final String EXTRA_DOCUMENT_ID = "doc_id";
    private static final String CACHE_DEFAULT_LOCALE = "default_locale";
    private static final Integer SPEECH_RECOGNIZER_CODE = 1;
    private static final Integer LANGUAGE_SELECTION_CODE = 2;
    private boolean showSuggestions;
    private boolean checkSpelling;

    private FirebaseAnalytics analytics;

    private ActivityEditDocumentBinding binding;
    private Menu menu;
    private EditDocumentVM viewModel;
    private SuggestionsVM suggestionsVM;
    private EditingLanguageChooserFragment editingLanguageChooserFragment;


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
        editingLanguageChooserFragment = (EditingLanguageChooserFragment) getFragmentManager().findFragmentById(R.id.editing_language_chooser_fragment);

        /// set-up analytics
        analytics = FirebaseAnalytics.getInstance(this);


        /// set-up view-models
        DiContext diContext = ((SpellNoteApp) getApplication()).getDiContext();
        viewModel = new EditDocumentVM(this,
                getIntent().getExtras().getLong(EXTRA_DOCUMENT_ID),
                diContext.getDocumentService(),
                diContext.getSpellCheckerService(),
                diContext.getSavedWordsService(),
                diContext.getDictionaryChangeSuggestingService());

        suggestionsVM = new SuggestionsVM(this, diContext.getSuggestionService());

        binding.setModel(viewModel);
        binding.setSuggestionsVM(suggestionsVM);

        /// set-up the actionbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());


        /// set-up edit-correct text
        binding.content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                /// show suggestions only if the current word has more than one character
                if( getCurrentWord().length() > 1 ) suggestionsVM.suggest(getCurrentWord());
                else                                onHideSuggestions();
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.notifyDocumentChanged();
            }
        });
        binding.content.setOnClickListener(v -> {
            /// show suggestions only if the current word has more than one character
            if( getCurrentWord().length() > 1 ) suggestionsVM.suggest(getCurrentWord());
            else                                onHideSuggestions();

            binding.content.checkCurrentWordCorrectness();
        });

        binding.content.setCurrentWordCorrectnessListener(new CurrentWordCorrectnessListener() {
            @Override
            public void onCurrentWordIsCorrect(String word) {
                hideRemoveAndAddButtons();
                if(binding.content.getSelectionStart() != binding.content.getSelectionEnd() && menu != null) {
                    menu.findItem(R.id.action_remove_word_from_dictionary).setVisible(true);
                }
            }

            @Override
            public void onCurrentWordIsWrong(String word) {
                if( menu != null )
                    menu.findItem(R.id.action_add_word_to_dictionary).setVisible(true);
            }

            @Override
            public void onMultipleWordsSelected() {
                hideRemoveAndAddButtons();
            }

            private void hideRemoveAndAddButtons() {
                if( menu == null )
                    return;
                menu.findItem(R.id.action_remove_word_from_dictionary).setVisible(false);
                menu.findItem(R.id.action_add_word_to_dictionary).setVisible(false);
            }
        });

        /// hide suggestions on scroll
        binding.contentScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            int prevY = 0;

            @Override
            public void onScrollChanged() {
                int scrollY = binding.contentScroll.getScrollY();
                if (Math.abs(prevY - scrollY) > 30) {
                    onHideSuggestions();
                    prevY = scrollY;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.onStart();
        suggestionsVM.onStart();
    }

    @Override
    protected void onDestroy() {
        /// send data to analytics
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString( "title", viewModel.getTitle() );
        analyticsBundle.putString( "content", viewModel.getContent() );
        analytics.logEvent("edit_document", analyticsBundle);

        /// control lifecycle of VMs
        viewModel.onDestroy();
        suggestionsVM.onDestroy();

        super.onDestroy();
    }

    @Override
    public void finish() {
        /// show ads in 50% of all cases
        int number = new Random().nextInt(2);
        if(number == 0)
            AdsActivity.launch(this);

        super.finish();
    }

    private void refreshActivity() {
        Intent refresh = new Intent(this, ActivityEditDocument.class);
        if( getIntent().getExtras() != null )
            refresh.putExtras(getIntent().getExtras());
        startActivity(refresh);
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_document, menu);
        updateShowSuggestions( CacheUtil.getCache( this, TagsUtil.USER_PREFERENCE_SHOW_SUGGESTIONS, true ), menu.findItem( R.id.action_show_suggestions ) );
        updateSpellChecking( CacheUtil.getCache( this, TagsUtil.USER_PREFERENCE_CHECK_SPELLING, true ), menu.findItem( R.id.action_check_spelling ) );

        /// Show suggestions tutorial
        SuggestionTutorial suggestionTutorial = new SuggestionTutorial(this);
        suggestionTutorial.showTutorial();

        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.action_show_suggestions )    { updateShowSuggestions( !showSuggestions, item );                          return true; }
        else if( id == R.id.action_record )         { Util.displaySpeechRecognizer( this, SPEECH_RECOGNIZER_CODE, this.getCurrentDictionary().getLocale() );   return true; }
        else if( id == R.id.action_send )           { Util.sendDocument( this, "", binding.content.getText().toString() );      return true; }
        else if( id == R.id.action_copy )           { Util.copyTextToClipboard( this, binding.content.getText().toString() );   return true; }
        else if( id == R.id.action_check_spelling ) { updateSpellChecking( !checkSpelling, item );                              return true; }
        else if( id == R.id.action_add_word_to_dictionary )         { viewModel.addWordToDictionary( binding.content.getCurrentWord().toString() );         return true; }
        else if( id == R.id.action_remove_word_from_dictionary )    { viewModel.removeWordFromDictionary( binding.content.getCurrentWord().toString() );    return true; }

        return super.onOptionsItemSelected(item);
    }


    public void updateShowSuggestions( boolean showSuggestions, MenuItem item ) {
        this.showSuggestions = showSuggestions;
        CacheUtil.setCache( this, TagsUtil.USER_PREFERENCE_SHOW_SUGGESTIONS, showSuggestions );

        if( showSuggestions )   item.setIcon( R.mipmap.ic_show_suggestions );
        else                    item.setIcon( R.mipmap.ic_hide_suggestions );

        if( showSuggestions )   suggestionsVM.suggest(binding.content.getCurrentWord().toString());
        else                    onHideSuggestions();
    }
    public void updateSpellChecking( boolean checkSpelling, MenuItem item ) {
        this.checkSpelling = checkSpelling;
        CacheUtil.setCache( this, TagsUtil.USER_PREFERENCE_CHECK_SPELLING, checkSpelling );

        item.setChecked( checkSpelling );
        binding.content.setSpellCheckingEnabled(checkSpelling);
        if( checkSpelling )
            viewModel.checkSpelling(0,  binding.content.getText().length(), binding.content.getAllWords(), binding.content );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode != RESULT_OK ) {
            return;
        }

        if( requestCode == LANGUAGE_SELECTION_CODE ) {
            refreshActivity();
        }

        if( requestCode == SPEECH_RECOGNIZER_CODE && data != null ) {
            ArrayList <String> results = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
            String spokenText = results.get(0);
            binding.content.replaceSelection(spokenText);
        }
    }


    @Override
    public DictionaryModel getCurrentDictionary() {
        return editingLanguageChooserFragment.getCurrentDictionary();
    }

    @Override
    public void onDocumentAvailable(DocumentModel document) {
        /// as we have the document lets load all supported languages
        /// to be able to set the default locale for editing
        editingLanguageChooserFragment.loadSupportedDictionaries();
    }

    @Override
    public void onDictionaryChanged(WordModel word) {
        viewModel.checkSpelling(
                0,
                binding.content.getText().length(),
                Collections.singletonList(word.getWord()),
                binding.content
        );
    }

    @Override
    public void onLanguageSelected(DictionaryModel dictionary) {
        viewModel.setLanguageLocale(dictionary.getLocale());

        /// update shared preferences (default locale)
        CacheUtil.setCache(this, CACHE_DEFAULT_LOCALE, dictionary.getLocale());

        /// run spellchecking on the whole text again because the language was changed
        viewModel.checkSpelling(
                0,
                binding.content.getText().length(),
                binding.content.getAllWords(),
                binding.content
        );
    }

    @Override
    public void onLaunchDictionaryChooser() {
        startActivityForResult( new Intent( this, ActivitySelectLanguages.class ), LANGUAGE_SELECTION_CODE );
    }

    @Override
    public void onDictionaryListAvailable(List<DictionaryModel> dictionaries) {

        /// check if the document already has a default locale saved
        if( viewModel.getLanguageLocale() != null ) {
            String savedLocale = viewModel.getLanguageLocale();
            boolean isLocaleAvailable = false;
            for( DictionaryModel dictionary : dictionaries ) {
                if( dictionary.getLocale().equals(savedLocale)) {
                    isLocaleAvailable = true;
                    break;
                }
            }

            if( isLocaleAvailable ) {
                editingLanguageChooserFragment.setCurrentLanguage(savedLocale);
                return;
            }
        }

        /// otherwise lets decide which locale to use
        String locale = CacheUtil.getCache(this, CACHE_DEFAULT_LOCALE, null);
        if( locale == null ) {
            if( dictionaries.isEmpty() )    return;
            else                            locale = dictionaries.get(0).getLocale();
        }

        editingLanguageChooserFragment.setCurrentLanguage(locale);
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
    public void onShowSuggestions() {
        if( !showSuggestions || suggestionsVM.getSuggestionVMs().isEmpty() ) {
            onHideSuggestions();
            return;
        }

        float h = binding.contentScroll.getScrollY();
        float x = binding.content.getCursorPositionX();
        float y = binding.content.getCursorPositionY();

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


    private class SuggestionTutorial extends BaseShowCaseTutorial {

        SuggestionTutorial(Context context) {
            super(context, "suggestion_tutorial");
        }

        @Override
        protected ShowcaseView.Builder display() {
            return new ShowcaseView.Builder(ActivityEditDocument.this)
                    .setTarget(new ToolbarActionItemTarget(binding.toolbar, R.id.action_show_suggestions))
                    .setContentTitle(R.string.tutorial_show_suggestions_title)
                    .setContentText(R.string.tutorial_show_suggestions_content)
                    .withMaterialShowcase();
        }
    }
}
