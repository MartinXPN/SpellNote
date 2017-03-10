package com.xpn.spellnote.ui.document.list;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xpn.spellnote.R;
import com.xpn.spellnote.ui.ActivityAbout;
import com.xpn.spellnote.ui.document.list.archive.FragmentViewArchive;
import com.xpn.spellnote.ui.document.list.documents.FragmentViewDocumentList;
import com.xpn.spellnote.ui.document.list.trash.FragmentViewTrash;
import com.xpn.spellnote.ui.language.ActivitySelectLanguages;
import com.xpn.spellnote.util.Codes;
import com.xpn.spellnote.util.Util;


public class ActivityViewDocuments extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String SAVED_STATE_FRAGMENT_TAG = "curr_f";
    BaseFragmentDocumentList documentFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_view_documents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /// set up navigation-toggle
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /// set up navigation drawer
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        /// show the latest chosen fragment
        Integer navigationId = savedInstanceState == null ? R.id.nav_documents : savedInstanceState.getInt(SAVED_STATE_FRAGMENT_TAG);
        onNavigationItemSelected( navigationView.getMenu().findItem(navigationId));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == Codes.EDIT_DOCUMENT_CODE ) {
            documentFragment = (BaseFragmentDocumentList) getFragmentManager().findFragmentById( R.id.list_of_documents );
            documentFragment.updateDocumentList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Integer currentFragmentTag = Integer.parseInt(getFragmentManager().findFragmentById( R.id.list_of_documents ).getTag());
        outState.putInt( SAVED_STATE_FRAGMENT_TAG, currentFragmentTag );
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if( id == R.id.nav_documents )          showFragment( id, new FragmentViewDocumentList() );
        else if( id == R.id.nav_archive )       showFragment( id, new FragmentViewArchive() );
        else if( id == R.id.nav_trash )         showFragment( id, new FragmentViewTrash() );
        else if( id == R.id.nav_dictionaries)   startActivity( new Intent( this, ActivitySelectLanguages.class ) ) ;
        else if( id == R.id.nav_feedback )      Util.sendFeedback( this );
        else if( id == R.id.nav_about )         startActivity( new Intent( this, ActivityAbout.class ) );

        /// close the drawer
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }


    public void showFragment( Integer navigationId, BaseFragmentDocumentList documentFragment ) {

        /// use navigation id as fragment tag
        String fragmentTag = navigationId.toString();

        // get fragment manager, Make sure the current transaction finishes first
        FragmentManager fm = getFragmentManager();
        fm.executePendingTransactions();
        assert getSupportActionBar() != null;

        // If there is a fragment with this tag...
        if( fm.findFragmentByTag( fragmentTag ) != null ) {
            this.documentFragment = (BaseFragmentDocumentList) fm.findFragmentById( R.id.list_of_documents );
            getSupportActionBar().setTitle( documentFragment.getCategory() );
            return;
        }

        /// set up navigation drawer
        ((NavigationView) findViewById(R.id.nav_view)).setCheckedItem(navigationId);
        this.documentFragment = documentFragment;

        /// Add the fragment and Show category name in actionbar
        fm.beginTransaction().replace( R.id.list_of_documents, documentFragment, fragmentTag ).commit();
        getSupportActionBar().setTitle( documentFragment.getCategory() );
    }
}
