package com.xpn.spellnote.ui.document.edit;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;


public class EditCorrectText extends AppCompatEditText {

    private boolean spellCheckingEnabled = true;

    public EditCorrectText(Context context) {
        super(context);
    }

    public EditCorrectText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditCorrectText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isSpellCheckingEnabled() {
        return spellCheckingEnabled;
    }
    public void setSpellCheckingEnabled(boolean spellCheckingEnabled) {
        this.spellCheckingEnabled = spellCheckingEnabled;
    }



    /**
     * Helper function to replace interval of the text
     */
    public void replace(int left, int right, String newText) {
        setText( getText()
                .delete(left, right)        // delete the interval [left, right]
                .insert(left, newText) );   // insert new string in the deleted place

        /// set selection to the end of inserted string
        setSelection(left + newText.length());
    }

    public void replaceSelection( String newText ) {
        replace( getSelectionStart(), getSelectionEnd(), newText );
    }

    public void markText(int left, int right, int color) {
        // color = Color.parseColor("#D20000")
        getText().setSpan( new ForegroundColorSpan(color), left, right, 0 );
    }
}
