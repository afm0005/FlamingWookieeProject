package com.android.flamingwookiee;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
        QuestionFragment.OnAnswerSelectedListener, MessageFragment.OnMessageResponseListener,
        HomeFragment.OnGridInteractionListener, ClassInfoDialogFragment.ClassInfoDialogListener {

    public static final String TODAYS_IP = "24.178.89.28";

    static final String TAG = "de.tavendo.autobahn.echo";
    public final static String EXTRA_CLASS_ID = "com.android.flamingwookiee.CLASS_ID";

    private ArrayList<com.android.flamingwookiee.classes.Class> mClassList;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private GridView mGridView;
    private SharedPreferences settings;
    private WebSocketClient mClient;
    private Class mCurrentClass;



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
            if(!params.get(0).isEmpty() && !params.get(1).isEmpty()) {
                String classId = params.get(0);
                String studentId = params.get(1);

                Class newClass = new Class("Class " + classId, classId
                        , studentId);
                ClassList.get(this).addClass(newClass);
                ClassList.get(this).saveClasses();
            }
        }

        mClassList = ClassList.get(this).getClasses();

        if (savedInstanceState == null) {
            showHome();
        }

        /*
        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        showHome();
                    }
                }
        );
        */


        /*
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<Class>(this,
                R.layout.drawer_list_item, mClassList));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        registerForContextMenu(mDrawerList);

        if (savedInstanceState == null) {
            showHome();
        }
        */


    }

    private void showHome() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, HomeFragment.newInstance(ClassList.get(this).getClassTitles(),"test"));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //Class info dialog listener
    public void onClassInfoDialogPositiveClick(DialogFragment dialog, String newTitle) {
        mCurrentClass.setTitle(newTitle);
        ClassList.get(this).saveClasses();
        showHome();
    }

    //Message response listener
    public void onMessageResponse(int offset) {
        mClient.send("{\"answer\":" + offset + "}");
    }

    //Grid context menu listener
    public void onGridContextMenuInteraction(int classPos, int menuPos) {
        mCurrentClass = mClassList.get(classPos);
        switch (menuPos) {
            case 0: DialogFragment infoFragment = ClassInfoDialogFragment.newInstance(
                    mClassList.get(classPos).getTitle(),
                    mClassList.get(classPos).getClassId(),
                    mClassList.get(classPos).getStudentId());
                    infoFragment.show(getFragmentManager(), "info_fragment");
                    break;
            case 1: ClassList.get(this).removeClass(mClassList.get(classPos));
                    ClassList.get(this).saveClasses();
                    showHome();
                    break;
        }

    }

    //Grid button listener
    public void onGridInteraction(int position) {
        mCurrentClass = mClassList.get(position);

        Toast.makeText(MainActivity.this, "Grid button pressed", Toast.LENGTH_SHORT).show();

        try {
            List<BasicNameValuePair> extraHeaders = Arrays.asList(
                    new BasicNameValuePair("Authorization", mCurrentClass.getStudentId())
            );


            mClient = new WebSocketClient(
                    // TODO don't hard code

                    URI.create("ws://" + TODAYS_IP + ":8080/takeme/" + mCurrentClass.getClassId()),
                    new WebSocketClient.Listener() {
                        @Override
                        public void onConnect() {
                            Log.d(TAG, "Connected to ws://" + TODAYS_IP + ":8080/takeme/");


                            String[] blank = {};
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.content_frame, MessageFragment.newInstance("Waiting for quiz to start", blank));
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }

                        @Override
                        public void onMessage(String message) {
                            //make new fragment with args, eventually will get done in a callback
                            Log.d(TAG, message);

                            Gson gson = new Gson();
                            Reply reply = gson.fromJson(message, Reply.class);


                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.content_frame, MessageFragment.newInstance(reply.text, reply.answers));
                            transaction.addToBackStack(null);
                            transaction.commit();
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
                            showHome(); // TODO probably not this her
                        }

                    }, extraHeaders
            );

            mClient.connect();

        } catch (Exception e) {
            Log.e("errors", e.toString());
        }
    }

    // Add class positive click event callback
    @Override
    public void onAddClassDialogPositiveClick(DialogFragment dialog, String title,
                                              String cId, String sId) {
        if(title.isEmpty() || cId.isEmpty() || sId.isEmpty()) {
            Toast.makeText(MainActivity.this, "Error, could not add class", Toast.LENGTH_SHORT).show();
        }
        else {
            Class newClass = new Class(title, cId, sId);
            ClassList.get(this).addClass(newClass);
            ClassList.get(this).saveClasses();
            showHome();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle presses on the action bar items
        switch(item.getItemId()) {
            case R.id.action_add_class:
                DialogFragment dialogFragment = new AddClassDialogFragment();
                dialogFragment.show(getFragmentManager(), "add_class");
                return true;
            case R.id.action_settings:
                //TODO add some settings
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class Reply {
        String text;
        String[] answers;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            //Class mClass = mClassList.get(position);

            try {
                List<BasicNameValuePair> extraHeaders = Arrays.asList(
                        new BasicNameValuePair("Authorization", mCurrentClass.getStudentId())
                );


                mClient = new WebSocketClient(
                        // TODO don't hard code

                        URI.create("ws://"+TODAYS_IP+":8080/takeme/" + mCurrentClass.getClassId()),
                        new WebSocketClient.Listener() {
                            @Override
                            public void onConnect() {
                                Log.d(TAG, "Connected to ws://"+TODAYS_IP+":8080/takeme/");



                                String[] blank = {};
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, MessageFragment.newInstance("Waiting for quiz to start", blank));
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }

                            @Override
                            public void onMessage(String message) {
                                //make new fragment with args, eventually will get done in a callback
                                Log.d(TAG, message);

                                Gson gson = new Gson();
                                Reply reply = gson.fromJson(message, Reply.class);


                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, MessageFragment.newInstance(reply.text, reply.answers));
                                transaction.addToBackStack(null);
                                transaction.commit();
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


