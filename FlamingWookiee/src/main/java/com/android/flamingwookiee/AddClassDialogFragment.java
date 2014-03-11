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
public class AddClassDialogFragment extends DialogFragment {

    /* The activity/fragment that creates an instance of this dialog fragment
     * must implement this interface in order to receive the event callbacks.
     * In this case the homepage will need the update the class list.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface AddClassDialogListener {
        public void onAddClassDialogPositiveClick(DialogFragment fragment);
    }

    private AddClassDialogListener mListener;

    private EditText mClassHash;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface.
        try {
            // Instantiate the AddClassDialogListener so we can send events to the host.
            mListener = (AddClassDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception.
            throw new ClassCastException(activity.toString()
                    + " must implement AddClassDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_class, null);

        mClassHash = (EditText) v.findViewById(R.id.class_hash);

        builder.setTitle("Add Class");
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //search for class... can be done via http/retrofit
                /*Sends the hash.. gets back a class name to display
                and a class id to connect to a quiz.
                Also maybe send this event back to main activity.
                 */

                Class newClass = new Class(mClassHash.getText().toString(), "1234");
                ClassList.get(getActivity()).addClass(newClass);

                mListener.onAddClassDialogPositiveClick(AddClassDialogFragment.this);

            }
        });

        return builder.create();
    }
}
