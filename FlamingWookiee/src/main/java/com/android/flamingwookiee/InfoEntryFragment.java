package com.android.flamingwookiee;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Andrew on 1/18/14.
 */
public class InfoEntryFragment extends DialogFragment {
    private EditText mIDNumberField;
    private EditText mUsernameField;
    private EditText mPinNumberField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_info_entry, null);
        SharedPreferences mPref = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        mIDNumberField = (EditText)v.findViewById(R.id.id_number);
        mUsernameField = (EditText)v.findViewById(R.id.username);
        mPinNumberField = (EditText)v.findViewById(R.id.pin_number);


        builder.setTitle("Enter Info");
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences mPref = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
                mPref.edit().putString("id_field", mIDNumberField.getText().toString()).apply();
                mPref.edit().putString("pin_field", mPinNumberField.getText().toString()).apply();
                mPref.edit().putString("username_field", mUsernameField.getText().toString()).apply();

            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
        if (mPref.contains("id_field")
                && mPref.contains("username_field")
                && mPref.contains("pin_field")) {
            //populate fields with data
            mIDNumberField.setText(mPref.getString("id_field", ""));
            mUsernameField.setText(mPref.getString("username_field", "John Doe"));
            mPinNumberField.setText(mPref.getString("pin_field", ""));
        }

        mUsernameField.setError(null);
        mPinNumberField.setError(null);
        mIDNumberField.setError(null);



        String mID = mIDNumberField.getText().toString();
        String mName = mUsernameField.getText().toString();
        String mPIN = mPinNumberField.getText().toString();

        if (TextUtils.isEmpty(mID)) {
            mIDNumberField.setError("ID Number Required");
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }
        if (TextUtils.isEmpty(mName)) {
            mUsernameField.setError("Username Required");
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }
        if(TextUtils.isEmpty(mPIN)) {
            mPinNumberField.setError("4 digit PIN Required");
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }




        mIDNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String mID = charSequence.toString();
                if (TextUtils.isEmpty(mID)) { //set or reset error
                    mIDNumberField.setError("ID Number Required");
                    alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                } else { //clear error and check if other fields have errors.
                    mIDNumberField.setError(null);
                    if(mUsernameField.getError() == null && mPinNumberField.getError() == null)
                    alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mUsernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String mName = mUsernameField.getText().toString();
                if (TextUtils.isEmpty(mName)) {
                    mUsernameField.setError("Username Required");
                    alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
                else {
                    mUsernameField.setError(null);
                    if(mPinNumberField.getError() == null && mIDNumberField.getError() == null)
                        alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mPinNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String mPIN = mPinNumberField.getText().toString();
                    if (mPIN.length() != 4) {
                        mPinNumberField.setError("4 Digit PIN required");
                        alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }
                    else {
                        mPinNumberField.setError(null);
                        if(mUsernameField.getError() == null && mIDNumberField.getError() == null)
                            alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return alert;

    }
}