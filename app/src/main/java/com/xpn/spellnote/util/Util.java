package com.xpn.spellnote.util;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

public class Util {

    public static void sendEmail( Context context, String[] receivers, String subject, String messageBody ) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra( Intent.EXTRA_SUBJECT, subject );
        i.putExtra( Intent.EXTRA_EMAIL, receivers );
        i.putExtra( Intent.EXTRA_TEXT, messageBody );
        try {
            context.startActivity(Intent.createChooser(i, "Choose an Email client:"));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( context, "There are no Email applications installed", Toast.LENGTH_SHORT ).show();
        }
    }
    public static void sendFeedback( Context context ) {
        sendEmail( context, new String[] {"XPNInc@gmail.com"}, "SpellNote Feedback", "" );
    }


    /**
     * Sends the note via messenger or e-mail
     */
    public static void sendDocument( Activity activity, String messageTitle, String messageBody ) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, messageTitle);
        i.putExtra(Intent.EXTRA_TEXT, messageBody);
        try {
            activity.startActivity(Intent.createChooser( i, "Send Message...") );
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( activity, "There are no messaging applications installed", Toast.LENGTH_SHORT ).show();
        }
    }


    public static void copyTextToClipboard( Context context, String text, boolean showToast ) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService( Context.CLIPBOARD_SERVICE );
        ClipData clip = ClipData.newPlainText("Spell Checker", text);
        clipboard.setPrimaryClip( clip );

        if( showToast )
            Toast.makeText( context, "Text copied to clipboard", Toast.LENGTH_SHORT ).show();
    }
    public static void copyTextToClipboard( Context context, String text ) {
        copyTextToClipboard( context, text, true );
    }



    public static void displaySpeechRecognizer( Activity activity, Integer SPEECH_RECOGNIZER_CODE, String languageLocale ) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageLocale);
        try {
            activity.startActivityForResult(intent, SPEECH_RECOGNIZER_CODE);
        }
        catch ( ActivityNotFoundException e ) {
            Toast.makeText(activity, "Speech recognizer not found on your device", Toast.LENGTH_SHORT).show();
        }
    }
}
