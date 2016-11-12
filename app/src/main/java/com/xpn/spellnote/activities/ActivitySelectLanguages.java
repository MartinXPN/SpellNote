package com.xpn.spellnote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xpn.spellnote.R;
import com.xpn.spellnote.databasehelpers.DownloaderService;

public class ActivitySelectLanguages extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_languages);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /// start downloading dictionaries
                Intent intent = new Intent(ActivitySelectLanguages.this, DownloaderService.class);
                intent.putExtra(DownloaderService.FILENAME, "image.jpg");
                intent.putExtra(DownloaderService.URL, "http://static.asiawebdirect.com/m/bangkok/portals/laos/shared/teasersL/luang-prabang/luang-prabang-attractions/kuang-si-waterfall/teaserMultiLarge/image/Kuang-Si-Waterfall-near-Luang-Prabang.jpg");
                startService(intent);

                Toast.makeText( ActivitySelectLanguages.this, "Started downloading dictionaries", Toast.LENGTH_LONG ).show();

                /// TODO Remove unneeded ones
                /// TODO Update obsolete ones
                ActivitySelectLanguages.this.finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == android.R.id.home )   { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
