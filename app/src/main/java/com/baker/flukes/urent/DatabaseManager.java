package com.baker.flukes.urent;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Brian on 10/30/2017.
 */

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    private static DatabaseManager instance = null;
    private DatabaseReference mDatabase = null;
    private static Context mContext;

    private DatabaseManager(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseManager getInstance(Context context){
        if(instance == null){
            instance = new DatabaseManager();
        }
        mContext = context;
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

        addPropertyToNearbyUniversities(property);
    }

    public DatabaseReference GetUserEmailReference(String userId){
        Log.d(TAG, "GetUserReference");
        return FirebaseDatabase.getInstance().getReference().child("/users").child("/" + userId).child("/email");
    }

    public DatabaseReference GetPropertyReference(String propertyId){
        Log.d(TAG, "GetPropertyReference");
        return FirebaseDatabase.getInstance().getReference().child("/properties").child("/" + propertyId);
    }

    public DatabaseReference GetMessageReference(String messageId){
        Log.d(TAG, "GetMessageReference");
        return FirebaseDatabase.getInstance().getReference().child("/messages").child("/" + messageId);
    }

    public void DeleteProperty(Property property){
        DatabaseReference databaseProperties = mDatabase.child("/properties");
        DatabaseReference databaseUserProperties = mDatabase.child("/users").child("/" + property.getOwnerId()).child("/properties");

        databaseProperties.child("/" + property.getId()).removeValue();
        databaseUserProperties.child("/" + property.getId()).removeValue();

        deletePropertyFromNearbyUniversities(property);
    }

    public DatabaseReference GetPropertyListReference(){
        Log.d(TAG, "GetPropertyListReference");
        return FirebaseDatabase.getInstance().getReference().child("/properties");
    }

    public DatabaseReference GetMessageListReference(){
        Log.d(TAG, "GetMessageListReference");
        return FirebaseDatabase.getInstance().getReference().child("/messages");
    }

    public DatabaseReference GetUniversityPropertyListReference(String universityId){
        Log.d(TAG, "GetUniversityPropertyListReference");
        return FirebaseDatabase.getInstance().getReference().child("/universities").child("/" + universityId).child("/properties");
    }

    public DatabaseReference GetUserPropertyListReference(String userId){
        Log.d(TAG, "GetUserPropertyListReference");
        return FirebaseDatabase.getInstance().getReference().child("/users").child("/" + userId).child("/properties");
    }

    public DatabaseReference GetUserMessageListReference(String userId){
        Log.d(TAG, "GetUserMessageListReference");
        return FirebaseDatabase.getInstance().getReference().child("/users").child("/" + userId).child("/messages");
    }

    public void AddUniversity(University university){
        Log.d(TAG, "AddUniversity");

        DatabaseReference databaseProperties = mDatabase.child("/universities");

        // Get id and serialize property
        String key;
        if(university.getId() != null){
            key = university.getId();
            Log.d(TAG, "existing id: " + key);
        }else{
            key = databaseProperties.push().getKey();
            university.setId(key);
            Log.d(TAG, "new id: " + key);
        }
        // Create update for /universities/$id child
        Map<String, Object> propertyValues = university.toMap();
        Map<String, Object> propertiesUpdates = new HashMap<>();
        propertiesUpdates.put("/" + key, propertyValues);
        databaseProperties.updateChildren(propertiesUpdates);
    }
    public void AddMessage(Message message){
        Log.d(TAG, "AddMessage");

        DatabaseReference databaseProperties = mDatabase.child("/messages");
        DatabaseReference databaseSenderProperties = mDatabase.child("/users").child("/" + message.getSender()).child("/messages");
        DatabaseReference databaseRecipientProperties = mDatabase.child("/users").child("/" + message.getRecipient()).child("/messages");
        String key;
        if(message.getId() != null){
            key = message.getId();
            Log.d(TAG, "existing id: " + key);
        }else{
            key = databaseProperties.push().getKey();
            message.setId(key);
            Log.d(TAG, "new id: " + key);
        }
        // Create update for /messages/$id child
        Map<String, Object> messageValues = message.toMap();
        Map<String, Object> messageUpdates = new HashMap<>();
        messageUpdates.put("/" + key, messageValues);
        databaseProperties.updateChildren(messageUpdates);


        // Create update for /users/$userId/messages child for sender
        Map<String, Object> senderUpdates = new HashMap<>();
        senderUpdates.put("/" + key, true);
        databaseSenderProperties.updateChildren(senderUpdates);

        // Create update for /users/$userId/messages child for recipient
        Map<String, Object> recipientUpdates = new HashMap<>();
        recipientUpdates.put("/" + key, true);
        databaseRecipientProperties.updateChildren(recipientUpdates);

    }

    public DatabaseReference GetUniversityListReference(){
        Log.d(TAG, "GetUniversityListReference");
        return FirebaseDatabase.getInstance().getReference().child("/universities");
    }

    private DatabaseReference mDatabaseUniversities;
    private List<String> mUniversityIds;

    private void addPropertyToNearbyUniversities(Property property) {
        //create database reference to get each university
        final Property fProperty = property;
        mUniversityIds = new ArrayList<>();
        mDatabaseUniversities = mDatabase.child("/universities");
        mDatabaseUniversities.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new university has been added, add it to the list
                University university = dataSnapshot.getValue(University.class);
                if(closeTogether(fProperty, university)){
                    mUniversityIds.add(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                //don't care about this
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                //don't care about this
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                //don't care about this
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postProperties:onCancelled", databaseError.toException());
                //Toast.makeText(context, "Failed to load properties.", Toast.LENGTH_SHORT).show();
            }
        });
        mDatabaseUniversities.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading initial data for all properties");
                addProperty(mUniversityIds, fProperty.getId());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });
    }

    private void addProperty(List<String> universityIds, String propertyId){
        Log.d(TAG, "adding " + propertyId + " to " + universityIds.size() + " universities");
        for(String universityId : universityIds){
            DatabaseReference db = mDatabaseUniversities.child("/" + universityId).child("/properties");
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("/" + propertyId, true);
            db.updateChildren(userUpdates);
        }
    }

    private void deletePropertyFromNearbyUniversities(Property property) {
        //create database reference to get each university
        final Property fProperty = property;
        mUniversityIds = new ArrayList<>();
        mDatabaseUniversities = mDatabase.child("/universities");
        mDatabaseUniversities.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new university has been added, add it to the displayed list
                University university = dataSnapshot.getValue(University.class);
                mUniversityIds.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                //don't care about this
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                //don't care about this
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                //don't care about this
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postProperties:onCancelled", databaseError.toException());
                //Toast.makeText(context, "Failed to load properties.", Toast.LENGTH_SHORT).show();
            }
        });
        mDatabaseUniversities.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading initial data for all properties");
                removeProperty(mUniversityIds, fProperty.getId());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });
    }

    private void removeProperty(List<String> universityIds, String propertyId){
        Log.d(TAG, "removing " + propertyId + " from universities");
        for(String universityId : universityIds){
            DatabaseReference db = mDatabaseUniversities.child("/" + universityId).child("/properties");
            db.child("/" + propertyId).removeValue();
        }
    }

    private boolean closeTogether(Property p, University u) {
        LatLng location = null;
        try {
            location = MapsFragment.getLocationFromAddress(mContext, p.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }

        LatLng university = null;
        try {
            university = MapsFragment.getLocationFromAddress(mContext, u.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        LatLng sw = new LatLng(university.latitude-.2, university.longitude-.2);
        LatLng ne = new LatLng(university.latitude+.2, university.longitude+.2);
        LatLngBounds boundingBox = new LatLngBounds(sw,ne);
        if(location != null && boundingBox.contains(location)){
            return true;
        }else{
            return false;
        }
    }
}
