package com.android.flamingwookiee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.flamingwookiee.classes.*;

/**
 * Created by Andrew on 4/20/2014.
 */
public class ClassInfoDialogFragment extends DialogFragment {

    /* The activity/fragment that creates an instance of this dialog fragment
     * must implement this interface in order to receive the event callbacks.
     * In this case the homepage will need the update the class list.
     * Each method passes the DialogFragment in case the host needs to query it.
     */

    private EditText mClassTitle;
    private TextView mClassTitleLabel;
    private TextView mClassId;
    private TextView mStudentId;
    private ImageButton mEditTitle;

    public interface ClassInfoDialogListener {
        public void onClassInfoDialogPositiveClick(DialogFragment fragment, String newTitle);
    }

    private ClassInfoDialogListener mListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface.
        try {
            // Instantiate the ClassInfoDialogListener so we can send events to the host.
            mListener = (ClassInfoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception.
            throw new ClassCastException(activity.toString()
                    + " must implement ClassInfoDialogListener");
        }
    }

    static ClassInfoDialogFragment newInstance(String classTitle, String cId, String sId) {
        ClassInfoDialogFragment fragment = new ClassInfoDialogFragment();

        Bundle args = new Bundle();
        args.putString("class_title", classTitle);
        args.putString("class_id", cId);
        args.putString("student_id", sId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_class_info, null);



        mClassTitle = (EditText) v.findViewById(R.id.class_name_field);
        mClassTitleLabel = (TextView) v.findViewById(R.id.class_name_text);
        mClassId = (TextView) v.findViewById(R.id.class_id_text);
        mStudentId = (TextView) v.findViewById(R.id.student_id_text);
        mEditTitle = (ImageButton) v.findViewById(R.id.edit_name_button);
        mClassTitle.setTag(mClassTitle.getKeyListener());
        mClassTitle.setKeyListener(null);




        mClassTitleLabel.setText("Class Title: ");
        mClassTitle.setText(getArguments().getString("class_title"));
        mClassId.setText("Class ID: " + getArguments().getString("class_id"));
        mStudentId.setText("Student ID: " + getArguments().getString("student_id"));

        mEditTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClassTitle.setKeyListener((KeyListener) mClassTitle.getTag());
            }
        });

        builder.setTitle("Class Information");
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mListener.onClassInfoDialogPositiveClick(ClassInfoDialogFragment.this,
                        mClassTitle.getText().toString());

            }
        });

        final AlertDialog infoAlert = builder.create();


        mClassTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (TextUtils.isEmpty(mClassTitle.getText().toString())) {
                    infoAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String editTitle = charSequence.toString();
                if (TextUtils.isEmpty(editTitle)) {
                    mClassTitle.setError("Class must have a title!");
                    infoAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
                else {
                    mClassTitle.setError(null);
                    infoAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Not using
            }
        });

        return infoAlert;
    }
}
