package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
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

    public class Answer {
        String Id;
        int Answer;

        public Answer(int a, String id) {
            Answer = a;
            Id = id;
        }
    }


    public interface WookieService {
        @PUT("/quiz/{id}/answer")
        void answer(@Path("id") int id, @Body Answer answer, Callback<Result> cb);
    }

    int qid;
    //TODO go buy a server already
    String host;
    WookieService wookie;
    String mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //TODO remember how savedInstanceState works?
        if (savedInstanceState == null) {
            getActionBar().setTitle("Quiz");
            Bundle b = getIntent().getExtras();
            host = b.getString("host");
            qid = b.getInt("qid");

            SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
            String mId = sp.getString("id_field", "");
            //TODO security opportunities here
            this.mId = mId;

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
        Answer ans = new Answer(offset, mId);
        wookie.answer(qid, ans, new Callback<Result>() {
            @Override
            public void failure(final RetrofitError error) {
                //TODO show error?
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.failure_toast,
                        (ViewGroup)findViewById(R.id.failure_toast_layout));
                TextView text = (TextView)layout.findViewById(R.id.failure_text);
                text.setText("Error: Answer Not Received");

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
                Log.e("retrofit error", error.toString());
            }
            @Override
            public void success(Result r, Response resp) {
                //TODO show success?
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.success_toast,
                        (ViewGroup)findViewById(R.id.succes_toast_layout));
                TextView text = (TextView)layout.findViewById(R.id.success_text);
                text.setText("Success: Answer Received");

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                Log.e("yay", "great success");
            }
        });
    }

}
