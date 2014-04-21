package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionFragment extends Fragment {
    OnAnswerSelectedListener mCallback;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        Bundle args = getArguments();

        TextView mQuestionArea = (TextView) v.findViewById(R.id.question);
        mQuestionArea.setText(args.getString("question"));

        RadioGroup mChoiceGroup = (RadioGroup) v.findViewById(R.id.choice_group);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        String[] answerChoices = args.getStringArray("answer_choices");
        if (answerChoices != null) {
            final RadioButton[] rbgroup = new RadioButton[answerChoices.length];
            int i = 0;
            for (RadioButton rb : rbgroup) {
                rb = (RadioButton) inflater.inflate(R.layout.radio_button, null);
                rb.setText(answerChoices[i++]);
                mChoiceGroup.addView(rb, layoutParams);
            }
            mChoiceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    mCallback.onAnswerSelected(group.indexOfChild(group.findViewById(checkedId)));

                }

            });
        }

        if (answerChoices == null || answerChoices.length == 0) {
            ProgressBar progress = (ProgressBar) v.findViewById(R.id.wait_spinner);
            progress.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.quiz, menu);
    }

    public interface OnAnswerSelectedListener {
        public void onAnswerSelected(int offset);
    }

}

