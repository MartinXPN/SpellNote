package com.xpn.spellnote.ui.document.edit;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


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


    /**
     * Mark all occurrences of a word with a specified color
     * in the range [left, right]
     */
    public void markWord(String word, int left, int right, int color) {
        String text = getText().toString();
        int index = text.indexOf(word, left);

        while(index >= 0 && index < right) {
            markText(index, index + text.length(), color);
            index = text.indexOf(word, index + 1);
        }
    }

    /**
     * Removes all punctuation marks and splits by ' '
     * @return all nonempty strings found after that operation
     */
    public List <String> getWords(int left, int right) {
        String[] res = getText()
                .subSequence(left, right)   // take only the range [left, right)
                .toString()                 // convert to immuatable string
                .replaceAll("[.:_,]", " ")  // remove all punctuation marks with a regexp
                .split(" ");                // split the resulting string into words by ' '

        List<String> words = new ArrayList<>(Arrays.asList(res));
        words.removeAll(Collections.singleton(""));
        return words;
    }

    public List <String> getAllWords() {
        return getWords( 0, getText().length() );
    }


    private boolean isWordCharacter(char c) {
        return Character.isLetter(c) || c == '-';
    }

    /**
     * @param index any index inside a word
     * Returns first index of the word
     */
    public int getWordStart(int index) {
        Editable text = getText();
        index = Math.min( index, text.length() - 1 );
        index = Math.max( index, 0 );

        while( index > 0 && isWordCharacter(text.charAt(index-1))) {
            --index;
        }

        /// words contain '-' but they don't start with it
        while( index < text.length() && text.charAt(index) == '-')
            ++index;
        return index;
    }


    /**
     * @param index any index inside a word
     * Returns the first index at which text[index] is not alphabetic of a connector (i.e. '-')
     */
    public int getWordEnd(int index) {
        Editable text = getText();
        index = Math.min( index, text.length() );
        index = Math.max( index, 0 );

        while( index < text.length() && isWordCharacter(text.charAt(index))) {
            ++index;
        }
        return index;
    }
}
