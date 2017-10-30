package com.baker.flukes.urent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rflukes on 10/30/17.
 */

public class AddPropertyFragment extends Fragment{
    private static final String TAG = "AddPropertyFragment";
    private DatabaseReference mDatabase;
    private Button mSubmitButton;
    private EditText mAddress;
    private EditText mBedrooms;
    private EditText mBathrooms;
    private EditText mRent;
    private ToggleButton mUtilitiesIncluded;
    private ToggleButton mPetsAllowed;
    private List<Property> mProperties;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_add_property, container, false);

        mAddress = (EditText) view.findViewById(R.id.address_input);
        mBedrooms = (EditText) view.findViewById(R.id.bedroom_input);
        mBathrooms = (EditText) view.findViewById(R.id.bathroom_input);
        mRent = (EditText) view.findViewById(R.id.rent_input);
        mUtilitiesIncluded = (ToggleButton) view.findViewById(R.id.utilities_input);
        mPetsAllowed = (ToggleButton) view.findViewById(R.id.pets_input);
        mSubmitButton = (Button) view.findViewById(R.id.submit_button);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = mAddress.getText().toString();
                int numBedrooms = Integer.parseInt(mBedrooms.getText().toString());
                int numBathrooms = Integer.parseInt(mBathrooms.getText().toString());
                int rent = Integer.parseInt(mRent.getText().toString());
                boolean includesUtilities = mUtilitiesIncluded.isChecked();
                boolean petsAllowed = mPetsAllowed.isChecked();
                Property newProperty = new Property(getContext(), address, rent, numBedrooms, numBathrooms, petsAllowed, includesUtilities);

                // Get key (id) to add new property to database
                String key = mDatabase.push().getKey();

                // Update property with this id
                newProperty.setId(key);

                // Create update data
                Map<String, Object> propertyValues = newProperty.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/" + key, propertyValues);

                // Create a new property at: /properties/$key
                mDatabase.updateChildren(childUpdates);
                // Listen to update UI after addition is made
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "done loading initial data");
                        Toast.makeText(getContext(), "Property Added! Press Back button to return to Property Listings", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "error loading initial data");
                    }
                });
            }
        });
        mProperties = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("/properties");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new property has been added, add it to the displayed list
                Property property = dataSnapshot.getValue(Property.class);
                property.setId(dataSnapshot.getKey());
                mProperties.add(property);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A property has changed, use the key to determine if we are displaying this
                // property and if so displayed the changed property.
                Property newProperty = dataSnapshot.getValue(Property.class);
                String propertyKey = dataSnapshot.getKey();

                for(Property property : mProperties){
                    if(property.getId().equals(propertyKey)){
                        property = newProperty;
                        property.setId(propertyKey);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A property has changed, use the key to determine if we are displaying this
                // property and if so remove it.
                String propertyKey = dataSnapshot.getKey();
                int toRemove = -1;
                for(int i = 0; i < mProperties.size(); i++){
                    if(mProperties.get(i).getId().equals(propertyKey)){
                        toRemove = i;
                        break;
                    }
                }
                if(toRemove >= 0){
                    mProperties.remove(toRemove);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A property has changed position, use the key to determine if we are
                // displaying this property and if so move it.

                // don't care about this now
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postProperties:onCancelled", databaseError.toException());
                //Toast.makeText(context, "Failed to load properties.", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addChildEventListener(childEventListener);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading initial data");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });
            return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        Log.d(TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}