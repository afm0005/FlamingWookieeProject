package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionFragment extends Fragment {
    private static final String TAG = "QuestionFragment";

    private TextView mQuestionArea;
    private LinearLayout mAnswerArea;

    OnAnswerSelectedListener mCallback;
    String question;
    String[] answers;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        mAnswerArea = (LinearLayout) v.findViewById(R.id.answer_area);

        Bundle args = getArguments();

        // TODO(reed): sorry dude just optimizing for MC to get this shit working
//        if(args.getString("type").equals("MC")) {
            //Create MC question in AnswerArea
            /*
            String[] answerChoices = args.getStringArray("answer_choices");
            Button[] choiceButtons = new Button[answerChoices.length];
            for(int i = 0; i < answerChoices.length; i++) {
                Button choiceButton = new Button(getActivity());
                choiceButton.setText(answerChoices[i]);
                mAnswerArea.addView(choiceButton);
                choiceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onAnswerSelected(1);

                    }
                });
                choiceButtons[i] = choiceButton;
            }
            */
            mQuestionArea= (TextView) v.findViewById(R.id.question);
            mQuestionArea.setText(args.getString("question"));

            String[] answerChoices = args.getStringArray("answer_choices");
            if (answerChoices != null) {
                final RadioButton[] rb = new RadioButton[answerChoices.length];
                RadioGroup rg = new RadioGroup(getActivity());
                rg.setOrientation(RadioGroup.VERTICAL); // TODO yikes... just set in manifest instead
                for(int i = 0; i < rb.length; i++) {
                    rb[i] = new RadioButton(getActivity());
                    rg.addView(rb[i]);
                    rb[i].setText(answerChoices[i]);
                }
                mAnswerArea.addView(rg);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                  @Override
                                                  public void onCheckedChanged(RadioGroup group, int checkedId) {
                        mCallback.onAnswerSelected(group.indexOfChild(group.findViewById(checkedId)));

                }

                });
            }
//        else if(args.getString("type").equals("TF")) {
//            //Create T/F question in AnswerArea
//            Button trueButton = new Button(getActivity());
//            Button falseButton = new Button(getActivity());
//            trueButton.setText("True");
//            falseButton.setText("False");
//            mAnswerArea.addView(trueButton);
//            mAnswerArea.addView(falseButton);
//        }
//        else if(args.getString("type").equals("SA")) {
//            //Create SA question in AnswerArea
//            EditText answerField = new EditText(getActivity());
//            mAnswerArea.addView(answerField);
//        }
//        else {
//            Log.d(TAG, "Error in bundle");
//        }

        /*

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

