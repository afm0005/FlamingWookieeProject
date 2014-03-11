package com.android.flamingwookiee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Andrew on 3/10/14.
 */
public class EnterUserInfoDialogFragment extends DialogFragment {

    /* The activity/fragment that creates an instance of this dialog fragment
     * must implement this interface in order to receive the event callbacks.
     * In this case the homepage will need the updated username to display.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface EnterUserInfoDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events.
    private EnterUserInfoDialogListener mListener;
    // Text field to enter a username.
    private EditText mUsername;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface.
        try {
            // Instantiate the EnterUserInfoDialogListener so we can send events to the host.
            mListener = (EnterUserInfoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception.
            throw new ClassCastException(activity.toString()
                    + " must implement EnterUserInfoDialogListener");
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final SharedPreferences settings = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_enter_info, null);

        mUsername = (EditText) v.findViewById(R.id.username);

        builder.setTitle("Enter username");
        builder.setView(v);

        if (settings.contains("username")) {
            mUsername.setText(settings.getString("username", "No username found"));
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String username = mUsername.getText().toString();
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", username);
                editor.apply();

                mListener.onDialogPositiveClick(EnterUserInfoDialogFragment.this);
            }
        });

        return builder.create();
    }
}
