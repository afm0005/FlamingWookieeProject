package com.android.flamingwookiee;

import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Andrew on 1/18/14.
 */
public class QuizFinderFragment extends Fragment {
    private EditText mURLField;
    private Button mSendButton;
    private Button mChangeInfoButton;
    private static final String DIALOG_USERNAME = "username";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quiz_finder, container, false);

        mURLField = (EditText)v.findViewById(R.id.url);

        mSendButton = (Button)v.findViewById(R.id.send_url_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mURLField.getText();
                //SharedPreferences mPref = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
                //mPref.edit().putString("url", mURLField.getText().toString());
            }
        });
        return v;
    }
}