package com.xpn.spellnote.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xpn.spellnote.R;
import com.xpn.spellnote.databasemodels.DocumentSchema;
import com.xpn.spellnote.fragments.FragmentEditCorrectText;

import java.util.Date;

public class ActivityEditDocument extends AppCompatActivity {

    private static final String TAG_FRAGMENT_TEXT = "tag_fragment_text";
    private FragmentEditCorrectText fragmentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_document);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // get fragment manager
        FragmentManager fm = getFragmentManager();
        // Make sure the current transaction finishes first
        fm.executePendingTransactions();

        // If there is no fragment yet with this tag...
        if( fm.findFragmentByTag( TAG_FRAGMENT_TEXT ) == null ) {
            // Add fragment
            FragmentTransaction ft = fm.beginTransaction();
            fragmentContent = new FragmentEditCorrectText();
            ft.replace( R.id.text, fragmentContent, TAG_FRAGMENT_TEXT );
            ft.commit();
        }
    }

    @Override
    public void finish() {

        DocumentSchema document = new DocumentSchema("", fragmentContent.getText(), new Date(), new Date(), "en", "White", "Archive" );
        document.save();
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == android.R.id.home ) { finish();   return true; }
        return super.onOptionsItemSelected(item);
    }
}
