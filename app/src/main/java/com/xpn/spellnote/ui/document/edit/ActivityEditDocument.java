package com.xpn.spellnote.ui.document.edit;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xpn.spellnote.R;
import com.xpn.spellnote.models.DocumentModel;
import com.xpn.spellnote.ui.fragments.FragmentEditCorrectText;
import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.Codes;
import com.xpn.spellnote.util.TagsUtil;
import com.xpn.spellnote.util.Util;

import java.util.ArrayList;
import java.util.Date;


public class ActivityEditDocument extends AppCompatActivity implements FragmentEditCorrectText.OnTextChangedListener {

    private static final String EXTRA_DOCUMENT_ID = "doc_id";
    private static final Integer SPEECH_RECOGNIZER_CODE = 1;
    private FragmentEditCorrectText fragmentContent;
    private DocumentModel document;
    private boolean showSuggestions;
    private boolean checkSpelling;

    public static void launch(Activity context, Long documentId) {
        Intent i = new Intent( context, ActivityEditDocument.class );
        i.putExtra( EXTRA_DOCUMENT_ID, documentId );
        context.startActivityForResult( i, Codes.EDIT_DOCUMENT_CODE );
    }

    public static void launch(Activity context, String category) {
        DocumentModel document = new DocumentModel();
        document.setCategory(category);
//        document.save();
        launch(context, document.getId() );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_document);

        /// document was already created in the database
        document = new DocumentModel(); //CreatedDocuments.getDocument( getIntent().getExtras().getLong( EXTRA_DOCUMENT_ID) );

        /// set-up the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /// get fragments
        FragmentManager fm = getFragmentManager();
        fm.executePendingTransactions();
        fragmentContent = (FragmentEditCorrectText) fm.findFragmentById( R.id.fragment_content);
    }

    @Override
    public void finish() {
        /// we just modified the document so we have to update the date
        document.setDateModified( new Date() );
//        document.save();
        super.finish();
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

        if( id == R.id.action_show_suggestions )    { updateShowSuggestions( !showSuggestions, item );              return true; }
        else if( id == R.id.action_record )         { Util.displaySpeechRecognizer( this, SPEECH_RECOGNIZER_CODE ); return true; }
        else if( id == R.id.action_send )           { Util.sendDocument( this, "", fragmentContent.getText() );     return true; }
        else if( id == R.id.action_copy )           { Util.copyTextToClipboard( this, fragmentContent.getText() );  return true; }
        else if( id == R.id.action_check_spelling ) { updateSpellChecking( !checkSpelling, item );                  return true; }

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
        fragmentContent.setSpellCheckingEnabled( checkSpelling );
    }



    @Override
    public void onTextChanged(String text) {
        document.setContent( text );
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SPEECH_RECOGNIZER_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList <String> results = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
            String spokenText = results.get(0);
            fragmentContent.replaceSelection( spokenText );
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
