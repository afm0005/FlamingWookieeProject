package com.android.flamingwookiee.classes;

/**
 * Created by Andrew on 3/11/14.
 */
public class MCQuestion extends Question{

    private String[] mAnswerChoices;

    public MCQuestion(String question) {
        super(question);
    }

    public void setAnswerChoices(String[] answers) {
        mAnswerChoices = answers;
    }

    public String[] getAnswerChoices() {
        return mAnswerChoices;
    }

    @Override
    public String toString() {
        return "{question: " + super.getQuestion() +
                ", answerChoices: " + mAnswerChoices + "}";
    }
}
