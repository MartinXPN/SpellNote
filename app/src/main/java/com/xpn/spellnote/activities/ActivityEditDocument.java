package com.xpn.spellnote.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.activeandroid.query.Select;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databasemodels.DocumentSchema;
import com.xpn.spellnote.fragments.FragmentEditCorrectText;
import com.xpn.spellnote.util.TagsUtil;

import java.util.Date;

public class ActivityEditDocument extends AppCompatActivity {

    private FragmentEditCorrectText fragmentContent;
    private String documentCategory;
    private long documentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_document);

        Bundle extras = getIntent().getExtras();
        documentCategory = extras.getString( TagsUtil.EXTRA_CATEGORY );
        if( extras.containsKey( TagsUtil.EXTRA_ID ) )   documentId = extras.getLong( TagsUtil.EXTRA_ID );
        else                                            documentId = -1;

        /// set-up the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /// get edit correct text fragment
        FragmentManager fm = getFragmentManager();
        fm.executePendingTransactions();
        fragmentContent = (FragmentEditCorrectText) fm.findFragmentById( R.id.text );
    }

    @Override
    public void finish() {

        DocumentSchema document = new DocumentSchema("",
                fragmentContent.getText(),
                new Date(),
                new Date(),
                "en",
                "White",
                getDocumentCategory());

        /// delete previous one
        if( documentId != -1 ) {
            new Select().from( DocumentSchema.class )
                    .where( "Id = ?", documentId )
                    .executeSingle()
                    .delete();
        }

        document.save();
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == android.R.id.home ) { finish();   return true; }
        return super.onOptionsItemSelected(item);
    }

    public String getDocumentCategory() {
        return documentCategory;
    }
}
