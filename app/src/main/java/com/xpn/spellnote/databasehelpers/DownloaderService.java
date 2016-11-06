package com.xpn.spellnote.databasehelpers;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class DownloaderService extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "service receiver";

    public DownloaderService() {
        super( "DownloaderService" );
    }

    // Will be called asynchronously by OS.
    @Override
    protected void onHandleIntent(Intent intent) {
        String urlPath = intent.getStringExtra(URL);
        String fileName = intent.getStringExtra(FILENAME);
        File output = new File( getFilesDir(), fileName );
        if (output.exists()) {
            boolean success = output.delete();
            if( !success ) {
                Log.d( "Downloader", "Wasn't able to delete existing file with the same name" );
            }
        }

        InputStream stream = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(urlPath);
            stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            fos = new FileOutputStream(output.getPath());
            int next;
            while ((next = reader.read()) != -1) {
                fos.write(next);
            }
            result = Activity.RESULT_OK;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {   try { stream.close(); } catch (IOException e) { e.printStackTrace(); } }
            if (fos != null)    {   try { fos.close(); }    catch (IOException e) { e.printStackTrace(); } }
        }
        publishResults(output.getAbsolutePath(), result);
    }

    private void publishResults(String outputPath, int result) {
        Log.d( "DownloaderService", "outputPath -> " + outputPath + "\tresult -> " + ( result == Activity.RESULT_OK ? "success" : "fail" ) );
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}