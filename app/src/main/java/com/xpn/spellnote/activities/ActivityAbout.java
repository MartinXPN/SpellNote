package com.xpn.spellnote.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.xpn.spellnote.R;

public class ActivityAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView about_author = (TextView) findViewById( R.id.about_author );
        about_author.setMovementMethod(LinkMovementMethod.getInstance());

        TextView about_app = (TextView) findViewById( R.id.about_app );
        about_app.setMovementMethod( LinkMovementMethod.getInstance() );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if( id == android.R.id.home )   { finish();     return true; }

        return super.onOptionsItemSelected(item);
    }
}
