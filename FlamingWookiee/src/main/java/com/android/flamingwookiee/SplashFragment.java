package com.android.flamingwookiee;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Andrew on 2/19/14.
 */
public class SplashFragment extends Fragment {
    private Button mAddClassButton;
    private TextView mCurrentUser;
    private Button mStartWS;

    public interface OnStartSelectedListener {
        public void onStartSelected();
    }

    OnStartSelectedListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_splash, container, false);

        //Add a class button, shows add a class dialog
        mAddClassButton = (Button) v.findViewById(R.id.add_class);
        mAddClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new AddClassDialogFragment();
                dialogFragment.show(getFragmentManager(), "add_class");

            }
        });

        mStartWS = (Button) v.findViewById(R.id.start_ws);
        mStartWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onStartSelected();
            }
        });

        //Displays the current username in the top-right corner of home page
        mCurrentUser = (TextView) v.findViewById(R.id.current_user);
        SharedPreferences mPref = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mCurrentUser.setText(mPref.getString("username", "No username found"));

        return v;
    }

}
