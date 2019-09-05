package com.xpn.spellnote.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

import com.xpn.spellnote.R;


public class Util {

    public static void chooseImageFromGallery(Fragment fragment, int requestCode) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, fragment.getString(R.string.hint_choose_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        fragment.startActivityForResult(chooserIntent, requestCode);
    }

    public static void openWebPage(Context context, Uri uri) {
        Intent web = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(web);
    }

    public static void openAppInPlayStore(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            openWebPage(context, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()));
        }
    }


    public static void sendEmail( Context context, String[] receivers, String subject, String messageBody ) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra( Intent.EXTRA_SUBJECT, subject );
        i.putExtra( Intent.EXTRA_EMAIL, receivers );
        i.putExtra( Intent.EXTRA_TEXT, messageBody );
        try {
            context.startActivity(Intent.createChooser(i, context.getString(R.string.hint_choose_email_client)));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( context, context.getString(R.string.error_no_email_clients), Toast.LENGTH_SHORT ).show();
        }
    }
    public static void sendFeedback( Context context ) {
        sendEmail( context, new String[] {"support@xpnsolutions.com"}, "SpellNote Feedback", "" );
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
            activity.startActivity(Intent.createChooser( i, activity.getString(R.string.hint_sent_file)) );
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( activity, activity.getString(R.string.error_no_messaging_apps), Toast.LENGTH_SHORT ).show();
        }
    }


    public static void copyTextToClipboard( Context context, String text, boolean showToast ) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService( Context.CLIPBOARD_SERVICE );
        if( clipboard == null ) {
            Toast.makeText( context, context.getString(R.string.error_no_clipboard), Toast.LENGTH_SHORT ).show();
            return;
        }

        ClipData clip = ClipData.newPlainText("SpellNote", text);
        clipboard.setPrimaryClip( clip );

        if( showToast )
            Toast.makeText( context, context.getString(R.string.message_copied_to_clipboard), Toast.LENGTH_SHORT ).show();
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
            Toast.makeText(activity, activity.getString(R.string.error_no_speech_recognizer), Toast.LENGTH_SHORT).show();
        }
    }
}
