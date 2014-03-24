package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;

public class MainActivity extends Activity implements
        EnterUserInfoDialogFragment.EnterUserInfoDialogListener,
        AddClassDialogFragment.AddClassDialogListener {


    static final String TAG = "de.tavendo.autobahn.echo";
    public final static String EXTRA_CLASS_ID = "com.android.flamingwookiee.CLASS_ID";
    private static final String WOOKIEE_URL = "absker.com:5432";

    private Class mCurrClass;
    private ArrayList<Class> mClassList;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private SharedPreferences settings;
    static Button mStart;


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


    static class User {
        String name;
    }

    public interface WookieService {
        @PUT("/quiz/{id}/answer")
        void answer(@Path("id") int id, @Body Answer answer, Callback<Result> cb);
        @GET("/users")
        List<User> users();

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


        //LAUNCHED FROM BROWSER LINK
        //gets the parameters and adds the class
        if (getIntent().getAction().equals("android.intent.action.VIEW")) {
            Uri data = getIntent().getData();
            String scheme = data.getScheme(); //http
            String host = data.getHost(); //twitter.com
            List<String> params = data.getPathSegments();
            String classId = params.get(0);
            String studentId = params.get(1);

            Class newClass = new Class(classId, "1234"
                    , studentId);
            ClassList.get(this).addClass(newClass);
        }

        mClassList = ClassList.get(this).getClasses();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<Class>(this,
                R.layout.drawer_list_item, mClassList));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null) {
            Fragment fragment = new SplashFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!getSharedPreferences("userInfo", MODE_PRIVATE).contains("username")) {
            showSettings();
        }
    }

    public void showSettings() {
        DialogFragment dialog = new EnterUserInfoDialogFragment();
        dialog.show(getFragmentManager(), "EnterUserInfoDialogFragment");
    }

    // Enter username positive click event callback
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        /* I want the splashFragment to update its view here to show
         * the new username.
         * This works but isn't ideal, if I change username from a
         * class page it comes back to home page.
         * ***POSSIBLY IRRELEVANT NOW****
         */
        Fragment fragment = new SplashFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

    }

    // Add class positive click event callback
    @Override
    public void onAddClassDialogPositiveClick(DialogFragment dialog) {
        mDrawerList.setAdapter(new ArrayAdapter<Class>(this,
                R.layout.drawer_list_item, mClassList));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        //Create new fragment and specify the class to show based on position
        Fragment fragment = new ClassFragment();
        Bundle args = new Bundle();
        args.putSerializable(ClassFragment.ARG_ID, mClassList.get(position).getId());
        fragment.setArguments(args);

        //Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        //Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(mClassList.get(position));
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

}
