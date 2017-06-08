package com.xpn.spellnote.ui.document.edit;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Pair;

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

    public void replaceCurrentWord( String newWord ) {
        replace(
                getWordStart( getSelectionStart() ),
                getWordEnd( getSelectionEnd() ),
                newWord
        );
    }

    public void markText(int left, int right, int color) {
        // color = Color.parseColor("#D20000")
        left = Math.max( left, 0 );
        right = Math.min( right, getText().length() );
        getText().setSpan( new ForegroundColorSpan(color), left, right, 0 );
    }


    /**
     * Mark all occurrences of a word with a specified color
     * in the range [left, right)
     */
    public void markWord(String word, int left, int right, int color) {
        String text = getText().toString();
        left = Math.max( left, 0 );
        right = Math.min( right, text.length() );
        int index = text.indexOf(word, left);

        while(index >= 0 && index < right) {
            /// define boundaries of the current word that needs to be marked
            int leftIndex = index - 1;
            int rightIndex = index + word.length();

            /// check if it's a word not a substring
            if( ( ( leftIndex >= 0              && !isWordCharacter(text.charAt(leftIndex)) ) || leftIndex == -1 ) &&
                ( ( rightIndex < text.length()  && !isWordCharacter(text.charAt(rightIndex))) || rightIndex == text.length() ) ) {
                markText( leftIndex + 1, rightIndex, color );
            }
            index = text.indexOf(word, index + 1);
        }
    }

    /**
     * Removes all punctuation marks and splits by ' '
     * @return all nonempty strings found after that operation
     */
    public List <String> getWords(int left, int right) {
        String[] res = getText()
                .subSequence(left, right)           // take only the range [left, right)
                .toString()                         // convert to immuatable string
                .replaceAll("[.:_,\n\t]", " ")      // remove all punctuation marks with a regexp
                .split(" ");                        // split the resulting string into words by ' '

        List<String> words = new ArrayList<>(Arrays.asList(res));
        words.removeAll(Collections.singleton(""));
        return words;
    }

    public CharSequence getCurrentWord() {
        return getText().subSequence(
                getWordStart(getSelectionStart()),
                getWordEnd(getSelectionEnd()));
    }

    private boolean isWordCharacter(char c) {
        return Character.isLetter(c) || c == '-' || c == '\'';
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


    public Pair<Float, Float> getCursorPosition() {
        int pos = getSelectionStart();
        int line = getLayout().getLineForOffset(pos);
        float x = getLayout().getPrimaryHorizontal(pos) + getPaddingLeft();
        float y = getLayout().getLineBottom(line) + getPaddingTop();

        return new Pair<>(x, y);
    }
}
