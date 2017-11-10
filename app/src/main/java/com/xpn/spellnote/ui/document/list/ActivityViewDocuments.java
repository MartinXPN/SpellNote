package com.xpn.spellnote.ui.document.list;

import android.app.FragmentManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.ActivityViewDocumentsBinding;
import com.xpn.spellnote.ui.about.ActivityAbout;
import com.xpn.spellnote.ui.dictionary.ActivitySelectLanguages;
import com.xpn.spellnote.ui.document.list.archive.FragmentViewArchive;
import com.xpn.spellnote.ui.document.list.documents.FragmentViewDocumentList;
import com.xpn.spellnote.ui.document.list.trash.FragmentViewTrash;
import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.Util;


public class ActivityViewDocuments extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String NAVIGATION_DRAWER_FIRST_LAUNCH_TAG = "nav_first";
    private static final String SAVED_STATE_FRAGMENT_TAG = "curr_f";
    private ActivityViewDocumentsBinding binding;
    BaseFragmentDocumentList documentFragment = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_documents);

        /// set-up analytics
        FirebaseAnalytics.getInstance(this);

        /// set up toolbar and navigation-toggle
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

        };
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();

        /// set up navigation drawer
        binding.navigation.setNavigationItemSelectedListener(this);

        /// show the latest chosen fragment
        Integer navigationId = savedInstanceState == null ? R.id.nav_documents : savedInstanceState.getInt(SAVED_STATE_FRAGMENT_TAG);
        onNavigationItemSelected( binding.navigation.getMenu().findItem(navigationId));

        /// open drawer on first launch
        if(CacheUtil.getCache(this, NAVIGATION_DRAWER_FIRST_LAUNCH_TAG, true)) {
            binding.drawer.openDrawer(Gravity.START, true);
//            MenuItem dictionariesItem = binding.navigation.getMenu().findItem(R.id.nav_trash);
//            new SelectDictionariesTutorial(this, dictionariesItem.getActionView()).showTutorial();
            CacheUtil.setCache(this, NAVIGATION_DRAWER_FIRST_LAUNCH_TAG, false );
        }
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
        binding.drawer.closeDrawer(GravityCompat.START, true);
        return true;
    }


    public void showFragment( Integer navigationId, BaseFragmentDocumentList documentFragment ) {

        /// use navigation id as fragment tag, show category in toolbar
        String fragmentTag = navigationId.toString();
        binding.toolbar.setTitle(documentFragment.getCategory());
        binding.navigation.setCheckedItem(navigationId);

        // get fragment manager, Make sure the current transaction finishes first
        FragmentManager fm = getFragmentManager();
        fm.executePendingTransactions();

        // Don't make new transaction if it's already present
        if( fm.findFragmentByTag( fragmentTag ) != null ) {
            this.documentFragment = (BaseFragmentDocumentList) fm.findFragmentById( R.id.list_of_documents );
        }
        else {
            this.documentFragment = documentFragment;
            fm.beginTransaction().replace(R.id.list_of_documents, documentFragment, fragmentTag).commit();
        }
    }
}
