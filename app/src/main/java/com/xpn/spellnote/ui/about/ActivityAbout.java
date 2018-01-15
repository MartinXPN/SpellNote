package com.xpn.spellnote.ui.about;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.xpn.spellnote.R;
import com.xpn.spellnote.databinding.ActivityAboutBinding;


public class ActivityAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_about);

        /// set-up analytics
        FirebaseAnalytics.getInstance(this);

        /// set-up toolbar
        setSupportActionBar(binding.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.aboutAuthor.setMovementMethod( LinkMovementMethod.getInstance() );
        binding.aboutApp.setMovementMethod( LinkMovementMethod.getInstance() );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if( item.getItemId() == android.R.id.home )
            finish();
        return super.onOptionsItemSelected(item);
    }
}
