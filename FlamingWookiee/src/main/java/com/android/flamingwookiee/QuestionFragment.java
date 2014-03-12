package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionFragment extends Fragment {
    private static final String TAG = "QuestionFragment";

    private Question mQuestion;

    private TextView mQuestionArea;
    private LinearLayout mAnswerArea;

    /*
    OnAnswerSelectedListener mCallback;
    String question;
    String[] answers;
    //TODO type

    /*
    public interface OnAnswerSelectedListener {
        public void onAnswerSelected(int offset);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        // also allows the passing of messages... but that's not important, right?
        try {
            mCallback = (OnAnswerSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }
    */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuestion = new Question("Default Question: Are you alive?");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        mAnswerArea = (LinearLayout) v.findViewById(R.id.answer_area);

        Bundle args = getArguments();

        if(args.getString("type").equals("MC")) {
            //Create MC question in AnswerArea
            String[] answerChoices = args.getStringArray("answer_choices");
            for(String choice : answerChoices) {
                Button choiceButton = new Button(getActivity());
                choiceButton.setText(choice);
                mAnswerArea.addView(choiceButton);
            }
        }
        else if(args.getString("type").equals("TF")) {
            //Create T/F question in AnswerArea
            Button trueButton = new Button(getActivity());
            Button falseButton = new Button(getActivity());
            trueButton.setText("True");
            falseButton.setText("False");
            mAnswerArea.addView(trueButton);
            mAnswerArea.addView(falseButton);
        }
        else if(args.getString("type").equals("SA")) {
            //Create SA question in AnswerArea
            EditText answerField = new EditText(getActivity());
            mAnswerArea.addView(answerField);
        }
        else {
            Log.d(TAG, "Error in bundle");
        }

        mQuestionArea= (TextView) v.findViewById(R.id.question);
        mQuestionArea.setText(args.getString("question"));

        /*
        RadioGroup answerButtons = (RadioGroup) v.findViewById(R.id.answers);

        for (String a : answers) {
            RadioButton button = (RadioButton) inflater.inflate(R.layout.answer_button, null);
            button.setText(a);
            answerButtons.addView(button);
            //TODO check index...
        }

        answerButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mCallback.onAnswerSelected(
                    group.indexOfChild(
                            group.findViewById(checkedId)));
                //Tell activity which answer was clicked
            }
        });

        */

        return v;
    }

}

