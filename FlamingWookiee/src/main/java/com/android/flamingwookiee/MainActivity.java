package com.android.flamingwookiee;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.flamingwookiee.classes.Class;
import com.android.flamingwookiee.classes.ClassList;
import com.codebutler.android_websockets.WebSocketClient;
import com.google.gson.Gson;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements
        AddClassDialogFragment.AddClassDialogListener,
        QuestionFragment.OnAnswerSelectedListener, MessageFragment.OnMessageResponseListener {

    public static final String TODAYS_IP = "24.178.89.28";

    static final String TAG = "de.tavendo.autobahn.echo";
    public final static String EXTRA_CLASS_ID = "com.android.flamingwookiee.CLASS_ID";

    private ArrayList<com.android.flamingwookiee.classes.Class> mClassList;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private SharedPreferences settings;
    private WebSocketClient mClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //LAUNCHED FROM BROWSER LINK
        //gets the parameters and adds the class
        if (getIntent().getAction().equals("android.intent.action.VIEW")) {
            Uri data = getIntent().getData();
            String scheme = data.getScheme(); //http
            String host = data.getHost(); //www.wooquiz.com
            List<String> params = data.getPathSegments();
            String classId = params.get(0);
            String studentId = params.get(1);

            Class newClass = new Class("Class " + classId, classId
                    , studentId);
            ClassList.get(this).addClass(newClass);
            ClassList.get(this).saveClasses();
        }

        mClassList = ClassList.get(this).getClasses();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<Class>(this,
                R.layout.drawer_list_item, mClassList));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null) {
            showHome();
        }
    }

    private void showHome() {
        Fragment fragment = new SplashFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    //Message response listener
    public void onMessageResponse(int offset) {
        mClient.send("{\"answer\":" + offset + "}");
    }

    // Add class positive click event callback
    @Override
    public void onAddClassDialogPositiveClick(DialogFragment dialog) {
        mDrawerList.setAdapter(new ArrayAdapter<Class>(this,
                R.layout.drawer_list_item, mClassList));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        ClassList.get(this).saveClasses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public class Reply {
        String text;
        String[] answers;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Class mClass = mClassList.get(position);

            try {
                List<BasicNameValuePair> extraHeaders = Arrays.asList(
                        new BasicNameValuePair("Authorization", mClass.getStudentId())
                );


                mClient = new WebSocketClient(
                        // TODO don't hard code

                        URI.create("ws://"+TODAYS_IP+":8080/takeme/" + mClass.getClassId()),
                        new WebSocketClient.Listener() {
                            @Override
                            public void onConnect() {
                                Log.d(TAG, "Connected to ws://"+TODAYS_IP+":8080/takeme/");


                                /*
                                String[] blank = {};
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, MessageFragment.newInstance("Waiting for quiz to start", blank));
                                transaction.addToBackStack(null);
                                transaction.commit();

                                */
                                Fragment qf = new QuestionFragment();

                                Bundle args = new Bundle();
                                args.putString("question", "Waiting for quiz to start");
                                qf.setArguments(args);

                                getFragmentManager().beginTransaction()
                                        .replace(R.id.content_frame, qf)
                                        .commit();


                            }

                            @Override
                            public void onMessage(String message) {
                                //make new fragment with args, eventually will get done in a callback
                                Log.d(TAG, message);

                                Gson gson = new Gson();
                                Reply reply = gson.fromJson(message, Reply.class);

                                /*
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, MessageFragment.newInstance(reply.text, reply.answers));
                                transaction.addToBackStack(null);
                                transaction.commit();

                                */
                                Bundle args = new Bundle();

                                args.putString("question", reply.text);
                                args.putStringArray("answer_choices", reply.answers);

                                Fragment qf = new QuestionFragment();
                                qf.setArguments(args);
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.content_frame, qf)
                                        .commit();


                            }

                            @Override
                            public void onMessage(byte[] data) {
                                Log.d(TAG, String.format("Got binary message! %s", data));
                                // TODO hopefully never?
                            }

                            @Override
                            public void onDisconnect(int code, String reason) {
                                showHome();
                                Log.d(TAG, String.format("Disconnected!"));
                            }

                            @Override
                            public void onError(Exception error) {
                                Log.d(TAG, String.format("error"));
                                showHome(); // TODO probably not this here
                            }

                        }, extraHeaders
                );

                mClient.connect();

            } catch (Exception e) {
                Log.e("errors", e.toString());
            }

            //Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            //setTitle(mClassList.get(position));
            mDrawerLayout.closeDrawer(mDrawerList);


            /*
            String[] test = {"Choice 1", "Choice 2", "Choice 3"};
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, MessageFragment.newInstance("This is a Question", test));
            transaction.addToBackStack(null);
            transaction.commit();
            */


        }
    }

    public void onAnswerSelected(final int offset) {
        mClient.send("{\"answer\":" + offset + "}");
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

}
