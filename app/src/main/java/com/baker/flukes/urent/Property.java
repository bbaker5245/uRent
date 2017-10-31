package com.baker.flukes.urent;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.database.Exclude;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Brian on 10/4/2017.
 */

public class Property {

    private String mId;
    private String mOwnerId;
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private int mRent;
    private int mBedrooms;
    private int mBathrooms;
    private boolean mPetsAllowed;
    private boolean mUtilitiesIncluded;
    private Context mContext;

    public Property() {
    }

    public Property( Context context, String ownerId, String address, int rent, int bedrooms, int bathrooms, boolean petsAllowed, boolean utilitiesIncluded ){
        mContext = context;
        mOwnerId = ownerId;
        mAddress = address;
        mRent = rent;
        mBedrooms = bedrooms;
        mBathrooms = bathrooms;
        mPetsAllowed = petsAllowed;
        mUtilitiesIncluded = utilitiesIncluded;

        setCoordinates(address);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ownerId", mOwnerId);
        result.put("address", mAddress);
        result.put("latitude", mLatitude);
        result.put("longitude", mLongitude);
        result.put("rent", mRent);
        result.put("bedrooms", mBedrooms);
        result.put("bathrooms", mBathrooms);
        result.put("petsAllowed", mPetsAllowed);
        result.put("utilitiesIncluded", mUtilitiesIncluded);

        return result;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(String ownerId) {
        mOwnerId = ownerId;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
        setCoordinates(address);
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }


    private void setCoordinates(String address) {

    }

    public int getRent() {
        return mRent;
    }

    public void setRent(int rent) {
        mRent = rent;
    }

    public int getBedrooms() {
        return mBedrooms;
    }

    public void setBedrooms(int bedrooms) {
        mBedrooms = bedrooms;
    }

    public int getBathrooms() {
        return mBathrooms;
    }

    public void setBathrooms(int bathrooms) {
        mBathrooms = bathrooms;
    }

    public boolean arePetsAllowed() {
        return mPetsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        mPetsAllowed = petsAllowed;
    }

    public boolean areUtilitiesIncluded() {
        return mUtilitiesIncluded;
    }

    public void setUtilitiesIncluded(boolean utilities) {
        mUtilitiesIncluded = utilities;
    }
}
