package com.xpn.spellnote.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xpn.spellnote.R;
import com.xpn.spellnote.util.CacheUtil;
import com.xpn.spellnote.util.TagsUtil;


public class FragmentEditCorrectText extends Fragment implements TextWatcher {

    protected Boolean spellCheckingEnabled;
    protected EditText text;
    protected OnTextChangedListener onTextChangedListener;

    public interface OnTextChangedListener {
        void onTextChanged( String text );
    }

    public FragmentEditCorrectText() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance( true );
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onStart() {
        super.onStart();
        try {
            onTextChangedListener = (OnTextChangedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnTextChangedListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        onTextChangedListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_correct_text, container, false);
        text = (EditText) view.findViewById( R.id.fragment_content);
        text.addTextChangedListener( this );
        spellCheckingEnabled = CacheUtil.getCache( getActivity(), TagsUtil.USER_PREFERENCE_CHECK_SPELLING, true );
        return view;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int previousLength, int changedLength) {

        Log.d( "TextChanged", "start: " + start + "\tpreviousLength: " + previousLength + "\tchangedLength: " + changedLength + "\n" );
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int previousLength, int changedLength) {

        Log.d( "TextChanged", "start: " + start + "\tpreviousLength: " + previousLength + "\tchangedLength: " + changedLength + "\n" );
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if( onTextChangedListener != null )
            onTextChangedListener.onTextChanged( editable.toString() );
    }

    public void setSpellCheckingEnabled( boolean checkSpelling ) {
        spellCheckingEnabled = checkSpelling;
    }
    public boolean isSpellCheckingEnabled() {
        return spellCheckingEnabled;
    }


    public String getText() {
        return text.getText().toString();
    }
    public void setText( String newText ) {
        text.setText( newText );
    }
    public void setText( SpannableStringBuilder newText ) {
        text.setText( newText );
    }


    public void replaceSelection( String newText ) {
        int l = text.getSelectionStart();
        int r = text.getSelectionEnd();

        SpannableStringBuilder res = new SpannableStringBuilder( text.getText() );
        res.delete(l, r);
        res.insert(l, newText);
        text.setText(res);
        text.setSelection(l + newText.length());
    }
}
