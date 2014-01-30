package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.PUT;
import retrofit.http.Path;

public class QuizActivity extends Activity
        implements QuestionFragment.OnAnswerSelectedListener {


    //right now mostly irrelevant and I believe throwing an error
    //retrofit just needs something...
    //eventually, we should let the user know whether request was successful or not, though
    public class Result {
        boolean Success;
    }

    public interface WookieService {
        @FormUrlEncoded
        @PUT("/quiz/{id}/answer")
        void answer(@Path("id") int id, @Field("answer") int answer, Callback<Result> cb);
    }

    int qid;
    //TODO go buy a server already
    String host;
    WookieService wookie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //TODO remember how savedInstanceState works?
        if (savedInstanceState == null) {
            Bundle b = getIntent().getExtras();
            host = b.getString("host");
            qid = b.getInt("qid");

            RestAdapter ra = new RestAdapter.Builder()
                    .setServer("http://"+host) //dicey
                    .build();

            wookie = ra.create(WookieService.class);

            Bundle args = new Bundle();
            //TODO dummy data currently...mostly waiting on those pesky server people
            String question = "Just Answer the Question";
            String[] answers = new String[]{"a", "b", "c", "d"};
            args.putString("question", question);
            args.putStringArray("answers", answers);
            //TODO type, e.g. multiple choice, t/f, open ended

            //make new fragment with args, eventually will get done in a callback
            Fragment qf = new QuestionFragment();
            qf.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, qf)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO maybe back button?
        return super.onOptionsItemSelected(item);
    }

    public void onAnswerSelected(final int offset) {
        wookie.answer(qid, offset, new Callback<Result>() {
            @Override
            public void failure(final RetrofitError error) {
                //TODO show error?
                Log.e("retrofit error", error.toString());
            }
            @Override
            public void success(Result r, Response resp) {
                //TODO show success?
                Log.e("yay", "great success");
            }
        });
    }

}