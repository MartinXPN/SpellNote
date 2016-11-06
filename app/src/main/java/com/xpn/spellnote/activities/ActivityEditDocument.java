package com.xpn.spellnote.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xpn.spellnote.R;
import com.xpn.spellnote.databasehelpers.CreatedDocuments;
import com.xpn.spellnote.databasemodels.DocumentSchema;
import com.xpn.spellnote.fragments.FragmentEditCorrectText;
import com.xpn.spellnote.util.TagsUtil;

import java.util.Date;

public class ActivityEditDocument extends AppCompatActivity implements FragmentEditCorrectText.OnTextChangedListener {

    private FragmentEditCorrectText fragmentContent;
    private DocumentSchema document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_document);

        /// document was already created in the database
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey( TagsUtil.EXTRA_DOCUMENT_ID ) ) {
            Long documentId = extras.getLong( TagsUtil.EXTRA_DOCUMENT_ID);
            document = CreatedDocuments.getDocument( documentId );
        }
        /// create a new document
        else {
            String category = extras.getString( TagsUtil.EXTRA_CATEGORY );
            String languageLocale = extras.getString( TagsUtil.EXTRA_LANGUAGE_LOCALE );
            document = new DocumentSchema( "", "", new Date(), languageLocale, "#FFFFFF", category );
        }

        /// set-up the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /// get edit correct text fragment
        FragmentManager fm = getFragmentManager();
        fm.executePendingTransactions();
        fragmentContent = (FragmentEditCorrectText) fm.findFragmentById( R.id.edit_correct_text_fragment);
    }

    @Override
    public void finish() {
        /// we just modified the document so we have to update the date
        document.setDateModified( new Date() );
        document.save();
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == android.R.id.home ) { finish();   return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTextChanged(String text) {
        document.setContent( text );
    }
}
