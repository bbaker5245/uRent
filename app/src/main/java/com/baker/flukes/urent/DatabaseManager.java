package com.baker.flukes.urent;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brian on 10/30/2017.
 */

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    private static DatabaseManager instance = null;
    private DatabaseReference mDatabase = null;

    private DatabaseManager(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseManager getInstance(){
        if(instance == null){
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void AddProperty(Property property){
        Log.d(TAG, "AddProperty");

        DatabaseReference databaseProperties = mDatabase.child("/properties");
        DatabaseReference databaseUserProperties = mDatabase.child("/users").child("/" + property.getOwnerId()).child("/properties");

        // Get id and serialize property
        String key;
        if(property.getId() != null){
            key = property.getId();
            Log.d(TAG, "existing id: " + key);
        }else{
            key = databaseProperties.push().getKey();
            property.setId(key);
            Log.d(TAG, "new id: " + key);
        }


        // Create update for /properties/$id child
        Map<String, Object> propertyValues = property.toMap();
        Map<String, Object> propertiesUpdates = new HashMap<>();
        propertiesUpdates.put("/" + key, propertyValues);
        databaseProperties.updateChildren(propertiesUpdates);

        // Create update for /users/$userId/properties child
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + key, true);
        databaseUserProperties.updateChildren(userUpdates);
    }

    public DatabaseReference GetPropertyReference(String propertyId){
        Log.d(TAG, "GetPropertyReference");
        return FirebaseDatabase.getInstance().getReference().child("/properties").child("/" + propertyId);
    }

    public void DeleteProperty(Property property){
        DatabaseReference databaseProperties = mDatabase.child("/properties");
        DatabaseReference databaseUserProperties = mDatabase.child("/users").child("/" + property.getOwnerId()).child("/properties");

        databaseProperties.child("/" + property.getId()).removeValue();
        databaseUserProperties.child("/" + property.getId()).removeValue();
    }

    public DatabaseReference GetPropertyListReference(){
        Log.d(TAG, "GetPropertyListReference");
        return FirebaseDatabase.getInstance().getReference().child("/properties");
    }
}
