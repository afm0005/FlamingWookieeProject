package com.android.flamingwookiee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Andrew on 3/10/14.
 */
public class AddClassDialogFragment extends DialogFragment {

    private AddClassDialogListener mListener;

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

        final EditText mClassTitle = (EditText) v.findViewById(R.id.class_name);
        final EditText mClassId = (EditText) v.findViewById(R.id.class_hash);
        final EditText mStudentId = (EditText) v.findViewById(R.id.student_hash);

        builder.setTitle("Add Class");
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mListener.onAddClassDialogPositiveClick(AddClassDialogFragment.this,
                        mClassTitle.getText().toString(), mClassId.getText().toString(),
                        mStudentId.getText().toString());

            }
        });

        return builder.create();
    }

    /* The activity/fragment that creates an instance of this dialog fragment
     * must implement this interface in order to receive the event callbacks.
     * In this case the homepage will need the update the class list.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface AddClassDialogListener {
        public void onAddClassDialogPositiveClick(DialogFragment fragment,
                                                  String title, String cId,
                                                  String sId);
    }
}
