package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.PUT;
import retrofit.http.Path;

public class MainActivity extends Activity
        implements QuestionFragment.OnAnswerSelectedListener{

    private Class currClass;
    private ListView mDrawerList;

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

    public class Class {
        String classID;
        String studentID;
        String className;
    }

    public class ClassAdapter extends ArrayAdapter<Class> {

        ArrayList<Class> mClasses;
        Context context;

        public ClassAdapter(Context context, ArrayList<Class> classes) {
            super(context,android.R.layout.simple_list_item_1, classes);
            mClasses = classes;
            this.context = context;

        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            TextView text = (TextView)v.findViewById(android.R.id.text1);
            text.setText(mClasses.get(position).className);
            return v;
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
        setContentView(R.layout.activity_main);

        Class class1 = new Class();
        class1.className = "my Class";

        ArrayList<Class> list = new ArrayList<Class>();
        list.add(class1);


        getActionBar().setTitle("Quiz Finder");
//        //Button submit = (Button) findViewById(R.id.send_url_button);
//        //submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launchQuiz();
//            }
//        });

        Fragment qf = new SplashFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, qf)
                .commit();


        mDrawerList.setAdapter(new ClassAdapter(this, list));
        // Set the list's click listener
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    //public void launchQuiz() {
        // http://localhost:8080/quiz/1234
        //EditText urlBox = (EditText) findViewById(R.id.url);
//        urlBox.setError(null);
//
//        try {
//            URL url = new URL(urlBox.getText().toString());
//            Intent intent = new Intent(this, QuizActivity.class);
//            //TODO get rid of host and port issues
//            if (url.getPort() == -1) {
//                throw new MalformedURLException("bad");
//            }
//            intent.putExtra("host", url.getHost()+":"+url.getPort());
//            if (url.getPath().length() < 6) {
//                throw new MalformedURLException("bad");
//            }
//            intent.putExtra("qid", Integer.parseInt(url.getPath().substring(6)));
//            startActivity(intent);
//        } catch (MalformedURLException e) {
//            //TODO better error checking...
//            urlBox.setError("Bad URL, try again");
//            urlBox.requestFocus();
//        }
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getSharedPreferences("info", MODE_PRIVATE).contains("pin_field")) {
            showSettings();
        }
    }

    public void showSettings() {
        DialogFragment newFragment = new InfoEntryFragment();
        newFragment.show(getFragmentManager(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettings();
                return true;
        }
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
