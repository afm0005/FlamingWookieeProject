package com.android.flamingwookiee;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_info_entry, null);
        SharedPreferences mPref = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        mIDNumberField = (EditText)v.findViewById(R.id.id_number);
        mUsernameField = (EditText)v.findViewById(R.id.username);
        mPinNumberField = (EditText)v.findViewById(R.id.pin_number);

        if (mPref.contains("id_field")
                && mPref.contains("username_field")
                && mPref.contains("pin_field")) {
            //populate fields with data
            mIDNumberField.setText(Integer.toString(mPref.getInt("id_field", 123456789)));
            mUsernameField.setText(mPref.getString("username_field", "John Doe"));
            mPinNumberField.setText(Integer.toString(mPref.getInt("pin_field", 1234)));
        }

        builder.setTitle("Enter Info")
            .setView(v)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    attemptRegister();
                }
            });

        return builder.create();
    }


    //TODO this is awkward, because it'll leave dialog... don't let that happen
    public boolean attemptRegister() {
        mIDNumberField.setError(null);
        mPinNumberField.setError(null);
        mUsernameField.setError(null);

        String mID = mIDNumberField.getText().toString();
        String mPIN = mPinNumberField.getText().toString();
        String mName = mUsernameField.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(mID)) {
            mIDNumberField.setError("ID Number Required");
            focusView = mIDNumberField;
            cancel = true;
        } //TODO make sure int

        if (mPIN.length() != 4) {
            mPinNumberField.setError("4 Digit PIN required");
            focusView = mPinNumberField;
            cancel = true;
        } //TODO make sure int

        if (TextUtils.isEmpty(mName)) {
            mUsernameField.setError("Username Required");
            focusView = mUsernameField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            SharedPreferences mPref = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
            mPref.edit().putInt("id_field",Integer.parseInt(mIDNumberField.getText().toString())).apply();
            mPref.edit().putInt("pin_field", Integer.parseInt(mPinNumberField.getText().toString())).apply();
            mPref.edit().putString("username_field", mUsernameField.getText().toString()).apply();
        }
        return !cancel;
    }
}