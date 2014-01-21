package com.android.flamingwookiee;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Andrew on 1/18/14.
 */
public class InfoDialogFragment extends DialogFragment {
    private EditText mIDNumberField;
    private EditText mUsernameField;
    private EditText mPinNumberField;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_info_entry, null);

        mIDNumberField = (EditText)v.findViewById(R.id.id_number);
        mUsernameField = (EditText)v.findViewById(R.id.username);
        mPinNumberField = (EditText)v.findViewById(R.id.pin_number);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.info_entry_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        if (attemptRegister()) {
                            SharedPreferences mPref = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
                            mPref.edit().putInt("id_field",Integer.parseInt(mIDNumberField.getText().toString())).apply();
                            mPref.edit().putInt("pin_field", Integer.parseInt(mPinNumberField.getText().toString())).apply();
                            mPref.edit().putString("username_field", mUsernameField.getText().toString()).apply();
                        }



                    }
                })
                .create();
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