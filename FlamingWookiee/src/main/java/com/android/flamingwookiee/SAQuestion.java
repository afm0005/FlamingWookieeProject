package com.android.flamingwookiee;

/**
 * Created by Andrew on 3/11/14.
 */
public class SAQuestion extends Question {

    private String mAnswer;

    public SAQuestion(String question) {
        super(question);
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        mAnswer = answer;
    }
}
