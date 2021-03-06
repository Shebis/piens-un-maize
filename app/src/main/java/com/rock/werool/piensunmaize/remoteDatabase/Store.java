package com.rock.werool.piensunmaize.remoteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by guntt on 17.08.2017.
 */

public class Store implements Serializable {

    public static final String TAG_ID = "s_id";
    public static final String TAG_NAME = "s_name";
    public static final String TAG_LOCATION = "s_location";

    private int id;
    private String name;
    private String location;

    public Store(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Store(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Store(JSONObject jobj) throws JSONException {
        this(jobj.getInt(TAG_ID), jobj.getString(TAG_NAME), jobj.getString(TAG_LOCATION));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.getId() + " | " + this.getName() + " | " + this.getLocation();
    }
}
