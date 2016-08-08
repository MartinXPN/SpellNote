package com.xpn.spellnote.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.xpn.spellnote.R;
import com.xpn.spellnote.fragments.FragmentArchive;
import com.xpn.spellnote.fragments.FragmentDocumentList;
import com.xpn.spellnote.fragments.FragmentTrash;
import com.xpn.spellnote.util.TagsUtil;

public class ActivityViewDocuments
        extends     AppCompatActivity
        implements  FragmentDocumentList.OnListFragmentInteractionListener,
                    NavigationView.OnNavigationItemSelectedListener {


    protected Bundle savedInstanceState = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        if( savedInstanceState == null )
            savedInstanceState = new Bundle();
        this.savedInstanceState = savedInstanceState;

        setContentView(R.layout.activity_view_documents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // get fragment manager
        FragmentManager fm = getFragmentManager();
        // Make sure the current transaction finishes first
        fm.executePendingTransactions();

        // If there is no fragment yet with this tag...
        if( fm.findFragmentByTag( TagsUtil.TAG_DOCUMENT_LIST ) == null ) {
            // Add fragment
            FragmentTransaction ft = fm.beginTransaction();
            FragmentDocumentList fragmentDocumentList = new FragmentDocumentList();
            ft.replace( R.id.list_of_documents, fragmentDocumentList, TagsUtil.TAG_DOCUMENT_LIST );
            ft.commit();
        }


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

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if( id == R.id.nav_documents )      { showFragment( id, TagsUtil.FRAGMENT_DOCUMENTS); }
        else if( id == R.id.nav_archive )   { showFragment( id, TagsUtil.FRAGMENT_ARCHIVE); }
        else if( id == R.id.nav_trash )     { showFragment( id, TagsUtil.FRAGMENT_TRASH); }
        else if( id == R.id.nav_feedback )  { /* send feedback by mail */  }
        else if( id == R.id.nav_about )     { /* open activity about */ }

        /// close the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void showFragment(int navId, String fragmentTag) {

        /// save the choice
        savedInstanceState.putInt( TagsUtil.SAVED_STATE_FRAGMENT_ID, navId );

        // get fragment manager
        FragmentManager fm = getFragmentManager();
        // Make sure the current transaction finishes first
        fm.executePendingTransactions();

        // If there is a fragment with this tag...
        if( fm.findFragmentByTag( fragmentTag ) != null )
            return;

        Fragment fragment = null;
        if( navId == R.id.nav_documents)        fragment = new FragmentDocumentList();
        else if( navId == R.id.nav_archive )    fragment = new FragmentArchive();
        else if( navId == R.id.nav_trash )      fragment = new FragmentTrash();

        // Add fragment
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace( R.id.list_of_documents, fragment, fragmentTag );
        //fm.popBackStack();
        ft.commit();
    }
}
