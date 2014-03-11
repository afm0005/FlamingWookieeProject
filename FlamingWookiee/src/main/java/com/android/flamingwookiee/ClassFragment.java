package com.android.flamingwookiee;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.UUID;

/**
 * Created by Andrew on 2/23/14.
 */
public class ClassFragment extends Fragment{
    public static final String ARG_ID = "class_id";

    private Class mClass;

    private TextView mCurrentUser;
    private TextView mClassTitle;
    private Button mHomeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClass = new Class();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_class, parent, false);
        UUID id = (UUID) getArguments().getSerializable(ARG_ID);

        TextView mClassTitle = (TextView)v.findViewById(R.id.class_title_text);
        mClassTitle.setText(ClassList.get(getActivity()).getClass(id).toString());

        TextView mCurrentUser = (TextView) v.findViewById((R.id.current_user));
        SharedPreferences mPref = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mCurrentUser.setText(mPref.getString("username", "No username found"));

        mHomeButton = (Button) v.findViewById(R.id.go_home);
        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SplashFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        ClassList.get(getActivity()).saveClasses();
    }

}
