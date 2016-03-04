package com.xpn.spellnote.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.xpn.spellnote.R;
import com.xpn.spellnote.fragments.FragmentEditCorrectText;

public class EditDocument extends AppCompatActivity implements FragmentEditCorrectText.OnFragmentInteractionListener {

    private static final String TAG_FRAGMENT_TEXT = "tag_fragment_text";
    private static FragmentEditCorrectText fragmentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_document);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // get fragment manager
        FragmentManager fm = getFragmentManager();
        // Make sure the current transaction finishes first
        fm.executePendingTransactions();

        // If there is no fragment yet with this tag...
        if( fm.findFragmentByTag( TAG_FRAGMENT_TEXT ) == null ) {
            // Add fragment
            FragmentTransaction ft = fm.beginTransaction();
            fragmentText = new FragmentEditCorrectText();
            ft.replace( R.id.text, fragmentText, TAG_FRAGMENT_TEXT );
            ft.commit();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
