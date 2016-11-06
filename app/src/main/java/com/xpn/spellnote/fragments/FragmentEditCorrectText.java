package com.xpn.spellnote.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xpn.spellnote.R;


public class FragmentEditCorrectText extends Fragment {

    protected TextView text;
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


    /**
     * remove reference onDetach for not leaking memory
     */
    @Override
    public void onDetach() {
        super.onDetach();
        onTextChangedListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_correct_text, container, false);
        text = (TextView) view.findViewById( R.id.edit_correct_text_fragment);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                FragmentEditCorrectText.this.onTextChangedListener.onTextChanged( editable.toString() );
            }
        });
        return view;
    }

    public String getText() {
        return text.getText().toString();
    }
}
