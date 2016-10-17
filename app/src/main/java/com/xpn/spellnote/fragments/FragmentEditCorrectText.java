package com.xpn.spellnote.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xpn.spellnote.R;


public class FragmentEditCorrectText extends Fragment {

    protected TextView text;

    public FragmentEditCorrectText() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_correct_text, container, false);
        text = (TextView) view.findViewById( R.id.text );
        return view;
    }

    public String getText() {
        return text.getText().toString();
    }
}
