package com.android.flamingwookiee;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Andrew on 1/18/14.
 */
public class InfoEntryFragment extends Fragment {
    private EditText mIDNumberField;
    private EditText mUsernameField;
    private EditText mPinNumberField;
    private Button mSaveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_entry, container, false);
        SharedPreferences mPref = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        mIDNumberField = (EditText)v.findViewById(R.id.id_number);
        mIDNumberField.setText(Integer.toString(mPref.getInt("id_field", 123456789)));
        mUsernameField = (EditText)v.findViewById(R.id.username);
        mUsernameField.setText(mPref.getString("username_field", "e.g. afm0005"));
        mPinNumberField = (EditText)v.findViewById(R.id.pin_number);
        mPinNumberField.setText(Integer.toString(mPref.getInt("pin_field", 1234)));
        mSaveButton = (Button)v.findViewById(R.id.save_info_button);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attemptRegister()) {
                    SharedPreferences mPref = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
                    mPref.edit().putInt("id_field",Integer.parseInt(mIDNumberField.getText().toString())).apply();
                    mPref.edit().putInt("pin_field", Integer.parseInt(mPinNumberField.getText().toString())).apply();
                    mPref.edit().putString("username_field", mUsernameField.getText().toString()).apply();
                    Toast.makeText(getActivity(), R.string.info_saved_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    public boolean attemptRegister() {
        boolean allFieldsEntered = true;
        if(TextUtils.isEmpty(mIDNumberField.getText())) {
            mIDNumberField.setError("ID Number Required");
            allFieldsEntered = false;
        }

        if(TextUtils.isEmpty(mPinNumberField.getText())) {
            mPinNumberField.setError("Pin Number Required");
            allFieldsEntered = false;
        }
        else if (mPinNumberField.length() != 4) {
            mPinNumberField.setError("Pin Number must be four digits");
            allFieldsEntered = false;
        }

        if(TextUtils.isEmpty(mUsernameField.getText())) {
            mUsernameField.setError("Username Required");
            allFieldsEntered = false;
        }
        return allFieldsEntered;
    }
}