package com.android.flamingwookiee;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
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

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;
import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.PUT;
import retrofit.http.Path;

public class QuizActivity extends Activity implements QuestionFragment.OnAnswerSelectedListener {


    //right now mostly irrelevant and I believe throwing an error
    //retrofit just needs something...
    //eventually, we should let the user know whether request was successful or not, though



    private Question mQuestion;
    private final Wamp mConnection = new WampConnection();
    static final String TAG = "de.tavendo.autobahn.echo";



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

    private static class MyEvent1 {
        public String question;
        public String answer1;
        public String answer2;
        public String[] answers;

        @Override
        public String toString() {
            return question + answer1 + answer2 + answers[0] + answers[1] + answers[2];
        }
    }

    private void start() {
        final String wsuri = "ws://192.168.2.3:9000";

        mConnection.connect(wsuri, new Wamp.ConnectionHandler() {

            @Override
            public void onOpen() {
                Log.d(TAG, "Connected to server" + wsuri);
                mConnection.subscribe("http://example.com/myEvent1", MyEvent1.class, new Wamp.EventHandler() {

                    @Override
                public void onEvent(String topicUri, Object event) {
                        MyEvent1 q = (MyEvent1) event;
                        Log.d(TAG, "Received: " + q.toString());

                        Bundle args = new Bundle();
                        args.putString("question", q.question);
                        args.putString("type", "MC");
                        args.putStringArray("answer_choices", q.answers);
                        Fragment qf = new QuestionFragment();
                        qf.setArguments(args);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, qf)
                                .commit();
                    }
                });
            }

            @Override
            public void onClose(int code, String reason) {
                Log.d(TAG, "WAMP closed");
            }
        });

    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        Intent mIntent = getIntent();
        String classId = mIntent.getStringExtra(MainActivity.EXTRA_CLASS_ID);

        start();

        //TODO remember how savedInstanceState works?
        if (savedInstanceState == null) {
            getActionBar().setTitle("Quiz");
            Bundle b = getIntent().getExtras();
            host = b.getString("host");
            qid = b.getInt("qid");

            SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
            String mId = sp.getString("id_field", "");
            String mPin = sp.getString("pin_field", "");
            //TODO security opportunities here
            this.mId = mId + mPin;

            RestAdapter ra = new RestAdapter.Builder()
                    .setServer("http://" + host) //dicey
                    .build();

            wookie = ra.create(WookieService.class);
        }





            //Sample/Test Questions
            //sample multiple choice question
            MCQuestion mcQ = new MCQuestion("What is a Wookiee?");
            String[] sampleChoices = {"A UK garage musician", "A fictional species from Star Wars",
                    "I don't know", "I don't care"};
            mcQ.setAnswerChoices(sampleChoices);
            //sample true/false question
            TFQuestion tfQ = new TFQuestion("Wookiees are awesome!");
            //sample short answer question
            SAQuestion saQ = new SAQuestion("How do you feel about Wookiees?");

            //choosing a type to test...
            mQuestion = mcQ;

            Bundle args = new Bundle();
            //TODO dummy data currently...mostly waiting on those pesky server people

            String question = mQuestion.getQuestion();
            args.putString("question", question);

            if(mQuestion instanceof MCQuestion) {
                String[] answerChoices = ((MCQuestion) mQuestion).getAnswerChoices();
                args.putString("type", "MC");
                args.putStringArray("answer_choices", answerChoices);
                Log.d("QUESTION", "mQuestion is a MCQuestion");
            }
            else if(mQuestion instanceof TFQuestion) {
                args.putString("type", "TF");
                Log.d("QUESTION", "mQuestion is a TFQuestion");
            }
            else if(mQuestion instanceof SAQuestion) {
                //no answer args to pass for short answer questions
                args.putString("type", "SA");
                Log.d("QUESTION", "mQuestion is a SAQuestion");
            }



            //make new fragment with args, eventually will get done in a callback
            Fragment qf = new QuestionFragment();
            qf.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, qf)
                    .commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO maybe back button?
        return super.onOptionsItemSelected(item);
    }


    public void onAnswerSelected(final int offset) {

        Answer ans = new Answer(offset, mId);
        JSONObject json = new JSONObject();
        //json.put("json_payload", "default");
        try {
            json.put("answer", Integer.toString(offset));
        } catch (JSONException e) {
            Log.d("quiz activity", "error making json");
        }

        mConnection.publish("http://example.com/myEvent1", json.toString());
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
