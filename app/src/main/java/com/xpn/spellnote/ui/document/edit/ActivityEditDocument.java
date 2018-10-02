package com.xpn.spellnote.ui.document.edit;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tooltip.TooltipActionView;
import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.ActivityEditDocumentBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.models.WordModel;
import com.xpn.spellnote.ui.ads.InterstitialAdHelper;
import com.xpn.spellnote.ui.ads.RemoveAdsBilling;
import com.xpn.spellnote.ui.dictionary.ActivitySelectLanguages;
import com.xpn.spellnote.ui.document.edit.editinglanguage.EditingLanguageChooserFragment;
import com.xpn.spellnote.ui.document.edit.suggestions.SuggestionsVM;
import com.xpn.spellnote.ui.util.EditCorrectText.WordCorrectness;
import com.xpn.spellnote.ui.util.ViewUtil;
import com.xpn.spellnote.ui.util.tutorial.Tutorial;
import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import timber.log.Timber;


public class ActivityEditDocument extends AppCompatActivity
        implements EditDocumentVM.ViewContract,
        EditingLanguageChooserFragment.EditingLanguageChooserContract,
        SuggestionsVM.ViewContract {

    private static final String USER_PREFERENCE_SHOW_SUGGESTIONS = "show_sugg";
    private static final String USER_PREFERENCE_CHECK_SPELLING = "spell_check";
    private static final String EXTRA_DOCUMENT_ID = "doc_id";
    private static final String CACHE_DEFAULT_LOCALE = "default_locale";
    private static final Integer SPEECH_RECOGNIZER_CODE = 1;
    private static final Integer LANGUAGE_SELECTION_CODE = 2;
    private boolean checkSpelling;

    private FirebaseAnalytics analytics;
    private InterstitialAdHelper ads;

    private ActivityEditDocumentBinding binding;
    private Menu menu;
    private EditDocumentVM viewModel;
    private SuggestionsVM suggestionsVM;
    private EditingLanguageChooserFragment editingLanguageChooserFragment;


    public static void launchForResult(Fragment fragment, Long documentId, int requestCode) {
        Intent i = new Intent( fragment.getActivity(), ActivityEditDocument.class );
        i.putExtra( EXTRA_DOCUMENT_ID, documentId );
        Timber.d("Starting activity for result with request code: %s", requestCode);
        fragment.startActivityForResult( i, requestCode );
    }

    @SuppressLint("CheckResult")
    public static void launchForResult(Fragment fragment, String category, int requestCode) {
        DocumentModel document = new DocumentModel();
        document.setCategory(category);

        DiContext diContext = ((SpellNoteApp) fragment.getActivity().getApplication()).getDiContext();
        diContext.getDocumentService()
                .saveDocument(document)
                .subscribe(
                        () -> launchForResult(fragment, document.getId(), requestCode ),
                        throwable -> {
                            Toast.makeText(fragment.getActivity(), R.string.error_couldnt_create_document, Toast.LENGTH_SHORT).show();
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

        /// set-up advertisement helper
        RemoveAdsBilling billing = new RemoveAdsBilling(null, this, getString(R.string.license_key), getString(R.string.remove_ads_id));
        ads = new InterstitialAdHelper(this, billing);

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
        binding.toolbar.setNavigationOnClickListener(v -> showAdOrFinish());


        /// set-up edit-correct text
        binding.content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                hideRemoveAddWordToDictionaryButtons();

                /// show suggestions only if the current word has more than one character
                if( getCurrentWord().length() > 1 ) suggestionsVM.suggest(getCurrentWord());
                else                                onHideSuggestions();
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.notifyDocumentChanged();
            }
        });
        binding.title.setOnClickListener(view -> ViewUtil.showKeyboard(this, binding.title));
        binding.content.setOnClickListener(view -> {
            ViewUtil.showKeyboard(this, binding.content);
            /// show suggestions only if the current word has more than one character
            if (getCurrentWord().length() > 1)  suggestionsVM.suggest(getCurrentWord());
            else                                onHideSuggestions();

            hideRemoveAddWordToDictionaryButtons();
            if( menu != null && getCurrentDictionary() != null && getCurrentDictionary().getLocale() != null ) {
                if(binding.content.isCurrentWordCorrect() == WordCorrectness.CORRECT) {
                    menu.findItem(R.id.action_remove_word_from_dictionary).setVisible(true);
                }
                else if( binding.content.isCurrentWordCorrect() == WordCorrectness.INCORRECT ) {
                    menu.findItem(R.id.action_add_word_to_dictionary).setVisible(true);
                    showAddToDictionaryTutorial(menu.findItem(R.id.action_add_word_to_dictionary));
                }
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

    private void showAdOrFinish() {
        /// show ads in 50% of all cases
        int number = new Random().nextInt(2);
        if(number == 0) {
            try {
                ads.showAd(this::finish);
            }
            catch (IllegalStateException e) {
                Timber.e(e);
                finish();
            }
        }
        else {
            finish();
        }
    }

    private void refreshActivity() {
        Intent refresh = new Intent(this, ActivityEditDocument.class);
        if( getIntent().getExtras() != null )
            refresh.putExtras(getIntent().getExtras());
        startActivity(refresh);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_document, menu);
        this.menu = menu;
        updateShowSuggestions( CacheUtil.getCache( this, USER_PREFERENCE_SHOW_SUGGESTIONS, true ) );
        updateSpellChecking( CacheUtil.getCache( this, USER_PREFERENCE_CHECK_SPELLING, true ), menu.findItem( R.id.action_check_spelling ) );

        MenuItem suggestions = menu.findItem(R.id.action_show_suggestions);
        MenuItem addToDictionary = menu.findItem(R.id.action_add_word_to_dictionary);
        ((TooltipActionView) suggestions.getActionView()).setMenuItem(suggestions);
        ((TooltipActionView) addToDictionary.getActionView()).setMenuItem(addToDictionary);

        if( suggestionTutorial == null )
            suggestionTutorial = new Tutorial(this, "suggestion_tutorial", R.string.tutorial_show_suggestions, Gravity.BOTTOM).setTarget(menu.findItem(R.id.action_show_suggestions));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.action_show_suggestions )    { updateShowSuggestions( false );               return true; }
        if( id == R.id.action_hide_suggestions )    { updateShowSuggestions( true );                return true; }
        else if( id == R.id.action_record )         { Util.displaySpeechRecognizer( this, SPEECH_RECOGNIZER_CODE, this.getCurrentDictionary().getLocale() );   return true; }
        else if( id == R.id.action_send )           { Util.sendDocument( this, "", binding.content.getText().toString() );      return true; }
        else if( id == R.id.action_copy )           { Util.copyTextToClipboard( this, binding.content.getText().toString() );   return true; }
        else if( id == R.id.action_check_spelling ) { updateSpellChecking( !checkSpelling, item );                              return true; }
        else if( id == R.id.action_add_word_to_dictionary )         { viewModel.addWordToDictionary( binding.content.getCurrentWord().toString() );         hideRemoveAddWordToDictionaryButtons();     return true; }
        else if( id == R.id.action_remove_word_from_dictionary )    { viewModel.removeWordFromDictionary( binding.content.getCurrentWord().toString() );    hideRemoveAddWordToDictionaryButtons();     return true; }

        return super.onOptionsItemSelected(item);
    }


    public void updateShowSuggestions( boolean showSuggestions ) {
        Timber.d("updateShowSuggestions(%b)", showSuggestions);
        CacheUtil.setCache( this, USER_PREFERENCE_SHOW_SUGGESTIONS, showSuggestions );

        menu.findItem(R.id.action_show_suggestions).setVisible(showSuggestions);
        menu.findItem(R.id.action_hide_suggestions).setVisible(!showSuggestions);

        if( showSuggestions )   suggestionsVM.suggest(binding.content.getCurrentWord().toString());
        else                    onHideSuggestions();
    }
    public void updateSpellChecking( boolean checkSpelling, MenuItem item ) {
        this.checkSpelling = checkSpelling;
        CacheUtil.setCache( this, USER_PREFERENCE_CHECK_SPELLING, checkSpelling );

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

    private void hideRemoveAddWordToDictionaryButtons() {
        if( menu == null )
            return;
        menu.findItem(R.id.action_remove_word_from_dictionary).setVisible(false);
        menu.findItem(R.id.action_add_word_to_dictionary).setVisible(false);
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
        Toast.makeText(this, getString(R.string.dictionary_message_updated) + ": " + word.getWord(), Toast.LENGTH_SHORT).show();
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

        /// update locale for EditCorrectText
        binding.content.setLocale(new Locale(dictionary.getLocale()));

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
        binding.content.setLocale(new Locale(locale));
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
        boolean showSuggestions = CacheUtil.getCache( this, USER_PREFERENCE_SHOW_SUGGESTIONS, true );
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

        /// Show suggestions tutorial if not shown yet
        showSuggestionsTutorial();
    }

    @Override
    public void onHideSuggestions() {
        binding.suggestions.setVisibility(View.GONE);
    }



    /****** Tutorials ******/
    private Tutorial suggestionTutorial = null;
    private void showSuggestionsTutorial() {
        if( suggestionTutorial != null )
        suggestionTutorial.showTutorial();
    }

    private Tutorial addWordTutorial = null;
    private void showAddToDictionaryTutorial(MenuItem target) {
        if( addWordTutorial == null )
            addWordTutorial = new Tutorial(this, "add_word_tutorial", R.string.tutorial_add_word_to_dictionary, Gravity.BOTTOM).setTarget(target);
        addWordTutorial.showTutorial();
    }
}
