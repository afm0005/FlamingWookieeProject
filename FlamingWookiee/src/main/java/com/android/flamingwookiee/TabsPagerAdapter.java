package com.android.flamingwookiee;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;

/**
 * Created by Andrew on 1/21/14.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                //Info fragment
                return new InfoEntryFragment();
            case 1:
                //QuizFinder fragment
                return new QuizFinderFragment();
            case 2:
                //Quiz fragment
                return new QuizFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        //get item count(number of tabs)
        return 3;
    }
}
