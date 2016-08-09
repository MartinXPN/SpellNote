package com.xpn.spellnote.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xpn.spellnote.R;
import com.xpn.spellnote.fragments.FragmentViewArchive;
import com.xpn.spellnote.fragments.FragmentViewDocumentList;
import com.xpn.spellnote.fragments.FragmentViewTrash;
import com.xpn.spellnote.util.TagsUtil;
import com.xpn.spellnote.util.Util;

public class ActivityViewDocuments
        extends     AppCompatActivity
        implements  FragmentViewDocumentList.OnListFragmentInteractionListener,
                    NavigationView.OnNavigationItemSelectedListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_view_documents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /// show the latest chosen fragment
        if( savedInstanceState == null )    showFragment( TagsUtil.FRAGMENT_DOCUMENTS );
        else                                showFragment( savedInstanceState.getString( TagsUtil.SAVED_STATE_FRAGMENT_TAG ) );


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
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fm = getFragmentManager();
        String currentFragmentTag = fm.findFragmentById( R.id.list_of_documents ).getTag();
        outState.putString( TagsUtil.SAVED_STATE_FRAGMENT_TAG, currentFragmentTag );
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if( id == R.id.nav_documents )      { showFragment( TagsUtil.FRAGMENT_DOCUMENTS ); }
        else if( id == R.id.nav_archive )   { showFragment( TagsUtil.FRAGMENT_ARCHIVE ); }
        else if( id == R.id.nav_trash )     { showFragment( TagsUtil.FRAGMENT_TRASH ); }
        else if( id == R.id.nav_languages ) { Intent i = new Intent( this, ActivitySelectLanguages.class );     startActivity( i ) ; }
        else if( id == R.id.nav_feedback )  { Util.sendFeedback( this ); }
        else if( id == R.id.nav_about )     { Intent i = new Intent( this, ActivityAbout.class );               startActivity( i ); }

        /// close the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void showFragment( String fragmentTag ) {

        // get fragment manager
        FragmentManager fm = getFragmentManager();
        // Make sure the current transaction finishes first
        fm.executePendingTransactions();

        // If there is a fragment with this tag...
        if( fm.findFragmentByTag( fragmentTag ) != null )
            return;

        /// set up navigation drawer
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;

        Fragment fragment = null;
        if( fragmentTag.matches( TagsUtil.FRAGMENT_DOCUMENTS ) )        { fragment = new FragmentViewDocumentList();    navigationView.setCheckedItem( R.id.nav_documents ); }
        else if( fragmentTag.matches( TagsUtil.FRAGMENT_ARCHIVE ) )     { fragment = new FragmentViewArchive();         navigationView.setCheckedItem( R.id.nav_archive ); }
        else if( fragmentTag.matches( TagsUtil.FRAGMENT_TRASH ) )       { fragment = new FragmentViewTrash();           navigationView.setCheckedItem( R.id.nav_trash ); }

        // Add the fragment
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace( R.id.list_of_documents, fragment, fragmentTag );
        ft.commit();
    }
}
