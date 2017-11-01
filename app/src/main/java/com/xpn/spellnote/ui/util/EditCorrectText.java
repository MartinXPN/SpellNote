package com.xpn.spellnote.ui.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.xpn.spellnote.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class EditCorrectText extends AppCompatEditText implements SpellCheckingListener {

    public final Integer INCORRECT_COLOR = ContextCompat.getColor(this.getContext(), R.color.text_wrong);
    public final Integer CORRECT_COLOR = ContextCompat.getColor(this.getContext(), R.color.text_correct);
    private boolean spellCheckingEnabled = true;
    private SpellChecker spellChecker;
    private CurrentWordCorrectnessListener currentWordCorrectnessListener;


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
        if( !spellCheckingEnabled ) {
            /// mark the whole text as a correct one
            markText(0, getText().length(), CORRECT_COLOR);
        }
    }

    public void setSpellChecker(SpellChecker spellChecker) {
        this.spellChecker = spellChecker;
    }
    public void setCurrentWordCorrectnessListener(CurrentWordCorrectnessListener listener) {
        this.currentWordCorrectnessListener = listener;
    }


    public void checkCurrentWordCorrectness() {

        if( currentWordCorrectnessListener == null )
            return;
        int left = getWordStart(getSelectionStart());
        int right = getWordEnd(getSelectionEnd());


        List <String> words = getWords(left, right);
        if( words.size() != 1 ) {
            currentWordCorrectnessListener.onMultipleWordsSelected();
            return;
        }

        String currentWord = words.get(0);
        ForegroundColorSpan[] currentSpans = getText().getSpans(left, right, ForegroundColorSpan.class);

        if( currentSpans.length == 0 || currentSpans[0].getForegroundColor() == CORRECT_COLOR )
            currentWordCorrectnessListener.onCurrentWordIsCorrect(currentWord);
        else
            currentWordCorrectnessListener.onCurrentWordIsWrong(currentWord);
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        checkCurrentWordCorrectness();
        super.onSelectionChanged(selStart, selEnd);
    }

    public void removeSpans(int left, int right) {
        left = Math.max( 0, left );
        right = Math.min( getText().length(), right );
        ForegroundColorSpan[] toRemoveSpans = getText().getSpans(left, right, ForegroundColorSpan.class);
        for (ForegroundColorSpan toRemoveSpan : toRemoveSpans)
            getText().removeSpan(toRemoveSpan);
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
        left = Math.max( left, 0 );
        right = Math.min( right, getText().length() );
        removeSpans( left, right );
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
        left = Math.max( 0, left );
        right = Math.min( getText().length(), right );

        String[] res = getText()
                .subSequence(left, right)           // take only the range [left, right)
                .toString()                         // convert to immuatable string
                .replaceAll("[.:_,\n\t]", " ")      // remove all punctuation marks with a regexp
                .split(" ");                        // split the resulting string into words by ' '

        Set<String> words = new HashSet<>(Arrays.asList(res));
        words.remove("");
        return new ArrayList<>(words);
    }

    public List <String> getAllWords() {
        return getWords(0, getText().length() );
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

        if( text.length() == 0 )
            return index;

        if( isWordCharacter( text.charAt(index) ) ) {
            while (index > 0 && isWordCharacter(text.charAt(index - 1)))
                --index;
        }
        else {
            return index;
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


    public float getCursorPositionX() {
        int pos = getSelectionStart();
        return getLayout().getPrimaryHorizontal(pos) + getPaddingLeft();
    }

    public float getCursorPositionY() {
        int pos = getSelectionStart();
        int line = getLayout().getLineForOffset(pos);

        return (float) (getLayout().getLineBottom(line) + getPaddingTop());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int count) {
        super.onTextChanged(text, start, before, count);

        int left = start - 10;
        int right = start + count + 10;
        List<String> words = getWords(getWordStart(left), getWordEnd(right));

        if( isSpellCheckingEnabled() && spellChecker != null )
            spellChecker.checkSpelling(left, right, words, this);
    }


    @Override
    public void markIncorrect(int left, int right, List<String> incorrectWords) {
        if( !isSpellCheckingEnabled() )
            return;

        for( String word : incorrectWords ) {
            markWord( word, left, right, INCORRECT_COLOR);
        }
        checkCurrentWordCorrectness();
    }

    @Override
    public void markCorrect(int left, int right, List<String> correctWords) {
        if( !isSpellCheckingEnabled() )
            return;

        for( String word : correctWords ) {
            markWord( word, left, right, CORRECT_COLOR);
        }
        checkCurrentWordCorrectness();
    }


    public interface SpellChecker {
        void checkSpelling(int left, int right, List <String> words, SpellCheckingListener listener);
    }
}