package com.xpn.spellnote.ui.document.edit;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.xpn.spellnote.DiContext;
import com.xpn.spellnote.R;
import com.xpn.spellnote.SpellNoteApp;
import com.xpn.spellnote.databinding.ActivityEditDocumentBinding;
import com.xpn.spellnote.models.DictionaryModel;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.document.edit.options.FragmentChooseEditingLanguage;
import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.TagsUtil;
import com.xpn.spellnote.util.Util;

import java.util.ArrayList;


public class ActivityEditDocument extends AppCompatActivity implements EditDocumentVM.ViewContract, FragmentChooseEditingLanguage.OnLanguageSelectedListener {

    private static final String EXTRA_DOCUMENT_ID = "doc_id";
    private static final Integer SPEECH_RECOGNIZER_CODE = 1;
    private boolean showSuggestions;
    private boolean checkSpelling;

    private ActivityEditDocumentBinding binding;
    private EditDocumentVM viewModel;


    public static void launchForResult(Activity context, Long documentId, int requestCode) {
        Intent i = new Intent( context, ActivityEditDocument.class );
        i.putExtra( EXTRA_DOCUMENT_ID, documentId );
        context.startActivityForResult( i, requestCode );
    }

    public static void launchForResult(Activity context, String category, int requestCode) {
        DocumentModel document = new DocumentModel();
        document.setCategory(category);

        DiContext diContext = ((SpellNoteApp) context.getApplication()).getDiContext();
        diContext.getDocumentService().saveDocument(document).subscribe();
        launchForResult(context, document.getId(), requestCode );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_document);

        DiContext diContext = ((SpellNoteApp) getApplication()).getDiContext();
        viewModel = new EditDocumentVM(
                this,
                getIntent().getExtras().getLong(EXTRA_DOCUMENT_ID),
                diContext.getDocumentService());
        binding.setModel(viewModel);

        /// set-up the actionbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        viewModel.onStart();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        viewModel.onSaveDocument();
        viewModel.onDestroy();
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SPEECH_RECOGNIZER_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList <String> results = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
            String spokenText = results.get(0);
            binding.content.replaceSelection(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDocumentAvailable(DocumentModel document) {
    }

    @Override
    public void onLanguageSelected(DictionaryModel dictionary) {

    }
}
