package com.xpn.spellnote.activities;

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
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.fragments.FragmentDocumentList;

public class ViewDocuments extends AppCompatActivity implements FragmentDocumentList.OnListFragmentInteractionListener {

    private static final String TAG_DOCUMENT_LIST = "document_list";
    private static FragmentDocumentList fragmentDocumentList = null;

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

                Intent i = new Intent( ViewDocuments.this, EditDocument.class );
                startActivityForResult( i, 1 );
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
            fragmentDocumentList = new FragmentDocumentList();
            ft.replace( R.id.list_of_documents, fragmentDocumentList, TAG_DOCUMENT_LIST );
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
    public void onArchiveClick(int listPosition, View v) {

        Snackbar.make( v, "Archived", Snackbar.LENGTH_LONG ).setAction( "UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        } ).show();
    }

    @Override
    public void onTrashClick(int listPosition, View v) {

        Snackbar.make( v, "Moved to trash", Snackbar.LENGTH_LONG ).setAction( "UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        } ).show();
    }

    @Override
    public void onSendClick(int listPosition, View v) {

        Intent i = new Intent( Intent.ACTION_SEND );
        i.setType( "text/plain" );
        i.putExtra( Intent.EXTRA_SUBJECT, fragmentDocumentList.getTitleAt( listPosition ) );
        i.putExtra( Intent.EXTRA_TEXT, fragmentDocumentList.getTextAt( listPosition ) );
        try {
            startActivity( Intent.createChooser( i, "Send Message...") );
        }
        catch( android.content.ActivityNotFoundException ex ) {
            Toast.makeText( ViewDocuments.this, "There are no messaging applications installed", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onContentClick(int listPosition, View v) {

        Intent i = new Intent( ViewDocuments.this, EditDocument.class );
        i.putExtra( "id", fragmentDocumentList.getIdAt( listPosition ) );
        i.putExtra( "title", fragmentDocumentList.getTitleAt( listPosition ) );
        i.putExtra( "content", fragmentDocumentList.getTitleAt( listPosition ) );
        startActivityForResult(i, 1);
    }

    @Override
    public String getArchiveExplanation() {
        return "Archive";
    }

    @Override
    public String getTrashExplanation() {
        return "Move to trash";
    }

    @Override
    public String getSendExplanation() {
        return "Send";
    }
}
