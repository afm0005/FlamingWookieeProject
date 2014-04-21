package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.android.flamingwookiee.MessageFragment.OnMessageResponseListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MessageFragment extends Fragment {
    // the fragment initialization parameters
    private static final String ARG_TEXT = "text";
    private static final String ARG_CHOICES = "choices";

    private TextView mTextArea;
    private RadioGroup mChoiceGroup;

    private String mMessageText;
    private String[] mMessageChoices;

    private OnMessageResponseListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Parameter 1.
     * @param choices Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    public static MessageFragment newInstance(String text, String[] choices) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putStringArray(ARG_CHOICES, choices);
        fragment.setArguments(args);
        return fragment;
    }
    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessageText = getArguments().getString(ARG_TEXT);
            mMessageChoices = getArguments().getStringArray(ARG_CHOICES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        mTextArea  =(TextView) v.findViewById(R.id.message_text);
        mTextArea.setText(mMessageText);

        mChoiceGroup = (RadioGroup) v.findViewById(R.id.choice_group);

        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);


        char c = 'A';
        for (int i = 0; i < mMessageChoices.length; i++) {
            final RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.radio_button, null);
            mChoiceGroup.addView(radioButton, layoutParams);
            radioButton.setText(c++ + " - " + mMessageChoices[i]);
        }


        mChoiceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onButtonPressed(group.indexOfChild(group.findViewById(checkedId)));

            }

        });

        if(mMessageChoices.length == 0) {
            ProgressBar progress = (ProgressBar) v.findViewById(R.id.wait_spinner);
            progress.setVisibility(View.VISIBLE);
        }

        getActivity().setTitle("Quiz");

        return v;
    }

    //radio button pressed
    public void onButtonPressed(int offset) {
        if (mListener != null) {
            mListener.onMessageResponse(offset);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMessageResponseListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMessageResponseListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMessageResponseListener {
        public void onMessageResponse(int offset);
    }


}
