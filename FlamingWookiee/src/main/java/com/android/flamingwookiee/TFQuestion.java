package com.android.flamingwookiee;

/**
 * Created by Andrew on 3/11/14.
 */
public class TFQuestion extends Question {

    private String mTrueChoice;
    private String mFalseChoice;

    public TFQuestion(String question) {
        super(question);
    }

    public String getTrueChoice() {
        return mTrueChoice;
    }

    public void setTrueChoice(String trueChoice) {
        mTrueChoice = trueChoice;
    }

    public String getFalseChoice() {
        return mFalseChoice;
    }

    public void setFalseChoice(String falseChoice) {
        mFalseChoice = falseChoice;
    }
}
