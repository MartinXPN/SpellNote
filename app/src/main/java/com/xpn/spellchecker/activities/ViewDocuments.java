package com.xpn.spellchecker.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xpn.spellchecker.R;
import com.xpn.spellchecker.fragments.DocumentList;

public class ViewDocuments extends AppCompatActivity implements DocumentList.OnListFragmentInteractionListener {

    private static final String TAG_DOCUMENT_LIST = "document_list";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

        setContentView(R.layout.activity_view_documents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent i = new Intent( ViewDocuments.this, EditDocument.class );
                startActivity( i );
            }
        });


        // get fragment manager
        FragmentManager fm = getFragmentManager();
        // Make sure the current transaction finishes first
        fm.executePendingTransactions();

        // If there is no fragment yet with this tag...
        if( fm.findFragmentByTag( TAG_DOCUMENT_LIST ) == null ) {
            // Add fragment
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace( R.id.list_of_documents, new DocumentList(), TAG_DOCUMENT_LIST );
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_documents, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrashClick(int listPosition) {
    }

    @Override
    public void onArchiveClick(int listPosition) {
    }
}
