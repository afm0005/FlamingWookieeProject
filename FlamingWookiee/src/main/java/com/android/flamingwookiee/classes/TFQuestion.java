package com.android.flamingwookiee.classes;

/**
 * Created by Andrew on 3/11/14.
 */
public class TFQuestion extends Question {

    private String mTrueChoice;
    private String mFalseChoice;

    public TFQuestion(String question) {
        super(question);
        mTrueChoice = "True";
        mFalseChoice = "False";
    }

    public String getTrueChoice() {
        return mTrueChoice;
    }

    public String getFalseChoice() {
        return mFalseChoice;
    }

}
