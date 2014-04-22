package com.android.flamingwookiee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        QuestionFragment.OnAnswerSelectedListener,
        ClassInfoDialogFragment.ClassInfoDialogListener{

    public static final String BASE_URL = "ws://24.178.89.28:8080/takeme/";
    static final String TAG = "de.tavendo.autobahn.echo";
    private ArrayList<Class> mClassList;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private WebSocketClient mClient;
    private Class mCurrentClass;
    private ArrayAdapter<String> mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //LAUNCHED FROM BROWSER LINK
        //gets the parameters and adds the class
        if (getIntent().getAction().equals("android.intent.action.VIEW")) {
            Uri data = getIntent().getData();
            List<String> params = data.getPathSegments();
            if (!params.get(0).isEmpty() && !params.get(1).isEmpty()) {
                String classId = params.get(0);
                String studentId = params.get(1);

                Class newClass = new Class("Class " + classId, classId
                        , studentId);
                ClassList.get(this).addClass(newClass);
                ClassList.get(this).saveClasses();
            }
        }


        mClassList = ClassList.get(this).getClasses();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerAdapter = new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, ClassList.get(this).getClassTitles());

        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        registerForContextMenu(mDrawerList);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, R.drawable.ic_navigation_drawer, R.string.open, R.string.close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            showHome();
        }
    }

    private void showHome() {
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SplashFragment())
                .commit();


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle("Clicker");
                mDrawerLayout.openDrawer(mDrawerList);
                mDrawerList.clearChoices();
            }
        });
    }

    private void showClass(final int pos) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Highlight the selected item, update the title, and close the drawer
                setTitle(mCurrentClass.getTitle());
                mDrawerLayout.closeDrawer(mDrawerList);
                mDrawerList.setItemChecked(pos, true);
            }
        });
    }

    // Add class positive click event callback
    @Override
    public void onAddClassDialogPositiveClick(DialogFragment dialog, String title,
                                              String cId, String sId) {
        if (title.isEmpty() || cId.isEmpty() || sId.isEmpty()) {
            Toast.makeText(MainActivity.this, "Error, could not add class", Toast.LENGTH_SHORT).show();
        } else {
            Class newClass = new Class(title, cId, sId);
            ClassList.get(this).addClass(newClass);
            ClassList.get(this).saveClasses();
            mDrawerAdapter.add(title);
            mDrawerAdapter.notifyDataSetChanged();
            mDrawerList.invalidate();
            showHome();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle presses on the action bar items

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_add_class:
                DialogFragment dialogFragment = new AddClassDialogFragment();
                dialogFragment.show(getFragmentManager(), "add_class");
                return true;
            case android.R.id.home:
                showHome();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAnswerSelected(final int offset) {
        mClient.send("{\"answer\":" + offset + "}");
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public class Reply {
        String text;
        String[] answers;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            mCurrentClass = mClassList.get(position);

            try {
                List<BasicNameValuePair> extraHeaders = Arrays.asList(
                        new BasicNameValuePair("Authorization", mCurrentClass.getStudentId())
                );


                mClient = new WebSocketClient(
                    // TODO don't hard code
                    URI.create(BASE_URL + mCurrentClass.getClassId()),

                    new WebSocketClient.Listener() {
                        @Override
                        public void onConnect() {
                            Log.d(TAG, "Connected!");

                            Fragment qf = new QuestionFragment();

                            Bundle args = new Bundle();
                            args.putString("question", "Waiting for quiz to start");
                            qf.setArguments(args);

                            getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, qf)
                                    .commit();


                            showClass(position);
                        }

                        @Override
                        public void onMessage(String message) {
                            //make new fragment with args, eventually will get done in a callback
                            Log.e("FROM SERVER", message);

                            Gson gson = new Gson();
                            Reply reply = gson.fromJson(message, Reply.class);

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
                        }

                        @Override
                        public void onError(Exception error) {
                            showHome(); // TODO probably not this here
                        }

                    }, extraHeaders
                );

                mClient.connect();

            } catch (Exception e) {
                Log.e("errors", e.toString());
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId() == R.id.left_drawer) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            mCurrentClass = mClassList.get(info.position);
            menu.setHeaderTitle(mClassList.get(info.position).getTitle());
            String[] menuItems = {"Info", "Delete"};
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex  = item.getItemId();
        final int classPos = info.position;

        switch (menuItemIndex) {
            case 0: DialogFragment infoFragment = ClassInfoDialogFragment.newInstance(
                    mClassList.get(info.position).getTitle(),
                    mClassList.get(info.position).getClassId(),
                    mClassList.get(info.position).getStudentId());
                    infoFragment.show(getFragmentManager(), "info_fragment");
                    break;

            case 1: AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Delete class?");
// Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDrawerAdapter.remove(mClassList.get(classPos).getTitle());
                        mDrawerAdapter.notifyDataSetChanged();
                        mDrawerList.invalidate();
                        ClassList.get(getApplication()).removeClass(mClassList.get(classPos));
                        ClassList.get(getApplication()).saveClasses();

                        showHome();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showHome();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();
        }
        return true;
    }

    //Class info dialog listener
    public void onClassInfoDialogPositiveClick(DialogFragment dialog, String newTitle) {
        mCurrentClass.setTitle(newTitle);
        ClassList.get(this).saveClasses();
        //mDrawerAdapter.remove(mCurrentClass.getTitle());
        mDrawerAdapter.notifyDataSetChanged();
        mDrawerList.invalidate();
        showHome();
    }

}


