package com.baker.flukes.urent;

import android.content.Context;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brian on 10/31/2017.
 */

public class University {

    private String mId;
    private String mName;
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private Context mContext;

    public University() {
    }

    public University(Context context, String name, String address){
        mContext = context;
        mAddress = address;
        setCoordinates(address);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", mName);
        result.put("address", mAddress);
        result.put("latitude", mLatitude);
        result.put("longitude", mLongitude);

        return result;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
        setCoordinates(address);
    }

    private void setCoordinates(String address) {

    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }
}
