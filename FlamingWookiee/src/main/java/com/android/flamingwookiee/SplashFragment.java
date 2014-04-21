package com.android.flamingwookiee;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Andrew on 2/19/14.
 */
public class SplashFragment extends Fragment {
    private TextView mHomeBanner;

    public static SplashFragment newInstance() {
        SplashFragment fragment = new SplashFragment();
        return fragment;
    }

    public SplashFragment() {
        //required empty constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_splash, container, false);

        mHomeBanner = (TextView) v.findViewById(R.id.home_banner);
        mHomeBanner.setText("Looks like you haven't added any classes yet, use the add class button above" +
                " or add a class from your email link to get started!");


        return v;
    }

}
