package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionFragment extends Fragment {

    OnAnswerSelectedListener mCallback;
    String question;
    String[] answers;
    //TODO type

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        Bundle b = getArguments();
        question = b.getString("question");
        answers = b.getStringArray("answers");

        TextView tv = (TextView) v.findViewById(R.id.question);
        tv.setText(question);

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

        return v;
    }

}

