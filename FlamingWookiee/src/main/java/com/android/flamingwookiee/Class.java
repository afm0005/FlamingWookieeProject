package com.android.flamingwookiee;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by Andrew on 2/23/14.
 */
public class Class {
    private static final String JSON_TITLE = "title";
    private static final String JSON_CLASS_ID = "class_id";
    private static final String JSON_ID = "id";

    private String mTitle;
    private String mClassId;
    private UUID mId;

    public Class() {
        mTitle = "Default_Class";
        mClassId = "Default_ID";
    }

    public Class(String title, String ID) {
        mTitle = title;
        mClassId = ID;
        mId = UUID.randomUUID();
    }

    public Class(JSONObject json) throws JSONException {
        mTitle = json.getString(JSON_TITLE);
        mClassId = json.getString(JSON_CLASS_ID);
        mId = UUID.fromString(json.getString(JSON_ID));
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getClassId() {
        return mClassId;
    }

    public void setClassId(String ID) {
        mClassId = ID;
    }

    public UUID getId() {
        return mId;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_CLASS_ID, mClassId);
        json.put(JSON_ID, mId.toString());
        return json;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
