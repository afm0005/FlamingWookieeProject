package com.android.flamingwookiee;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.PUT;
import retrofit.http.Path;

public class MainActivity extends Activity implements
        EnterUserInfoDialogFragment.EnterUserInfoDialogListener,
        AddClassDialogFragment.AddClassDialogListener {

    public final static String EXTRA_CLASS_ID = "com.android.flamingwookiee.CLASS_ID";

    private Class mCurrClass;
    private ArrayList<Class> mClassList;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private SharedPreferences settings;

    //Test classes
    //Class class1 = new Class("Comp 1000", "1111");
    //Class class2 = new Class("Phil 1100", "5555");
    //Class class3 = new Class("Math 3240", "3333");


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

//    public class ClassAdapter extends ArrayAdapter<Class> {
//
//        ArrayList<Class> mClasses;
//        Context context;
//
//        public ClassAdapter(ArrayList<Class> classes) {
//            super(, 0, classes);
//            mClasses = classes;
//            this.context = context;
//
//        }

//        @Override
//        public View getView(final int position, View view, ViewGroup parent) {
//
//            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
//
//            TextView text = (TextView)v.findViewById(android.R.id.text1);
//            text.setText(mClasses.get(position).className);
//            return v;
//        }
//    }


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
