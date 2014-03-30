package com.android.flamingwookiee.classes;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Andrew on 3/10/14.
 */
public class ClassList {
    private static final String TAG = "ClassList";
    private static final String FILENAME = "classes.json";

    private static ClassList sClassList;
    private Context mAppContext;
    private ArrayList<com.android.flamingwookiee.classes.Class> mClasses;
    private ClassJSONSerializer mSerializer;


    //test data
    //Class class1 = new Class("Comp 1000", "1111");
    //Class class2 = new Class("Phil 1100", "5555");
    //Class class3 = new Class("Math 3240", "3333");

    private ClassList(Context appContext) {
        mAppContext = appContext;
        //mClasses = new ArrayList<Class>();
        mSerializer = new ClassJSONSerializer(mAppContext, FILENAME);

        try {
            mClasses = mSerializer.loadClasses();
        } catch (Exception e) {
            mClasses = new ArrayList<Class>();
            Log.e(TAG, "Error loading classes: ", e);
        }

        //mClasses.add(class1);
        //mClasses.add(class2);
        //mClasses.add(class3);
    }

    public static ClassList get(Context c) {
        if(sClassList == null) {
            sClassList = new ClassList(c.getApplicationContext());
        }
        return sClassList;
    }

    public ArrayList<Class> getClasses() {
        return mClasses;
    }

    public Class getClass(UUID id) {
        for (Class c : mClasses) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public void addClass(Class c) {
        mClasses.add(c);
    }

    public boolean saveClasses() {
        try {
            mSerializer.saveClasses(mClasses);
            Log.d(TAG, "classes saved to file");
            //show success toast
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving classes: ", e);
            //show failure toast
            return false;
        }
    }

}
