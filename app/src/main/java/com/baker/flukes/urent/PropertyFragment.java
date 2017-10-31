package com.baker.flukes.urent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by rflukes on 10/30/17.
 */

public class PropertyFragment extends Fragment{

    private static final String TAG = "PropertyFragment";
    private static final String ARG_PROPERTY_ID = "property_id";

    private Property mProperty;

    private FloatingActionButton mSubmitButton;
    private FloatingActionButton mDeleteButton;
    private EditText mAddress;
    private EditText mBedrooms;
    private EditText mBathrooms;
    private EditText mRent;
    private ToggleButton mUtilitiesIncluded;
    private ToggleButton mPetsAllowed;

    public static PropertyFragment newInstance(String propertyId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY_ID, propertyId);
        PropertyFragment fragment = new PropertyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_property, container, false);

        mAddress = (EditText) view.findViewById(R.id.address_input);
        mBedrooms = (EditText) view.findViewById(R.id.bedroom_input);
        mBathrooms = (EditText) view.findViewById(R.id.bathroom_input);
        mRent = (EditText) view.findViewById(R.id.rent_input);
        mUtilitiesIncluded = (ToggleButton) view.findViewById(R.id.utilities_input);
        mPetsAllowed = (ToggleButton) view.findViewById(R.id.pets_input);
        mSubmitButton = (FloatingActionButton) view.findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get info from view
                String address = mAddress.getText().toString();
                int numBedrooms = Integer.parseInt(mBedrooms.getText().toString());
                int numBathrooms = Integer.parseInt(mBathrooms.getText().toString());
                int rent = Integer.parseInt(mRent.getText().toString());
                boolean includesUtilities = mUtilitiesIncluded.isChecked();
                boolean petsAllowed = mPetsAllowed.isChecked();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Update Property object
                if(mProperty != null){
                    mProperty.setAddress(address);
                    mProperty.setBedrooms(numBedrooms);
                    mProperty.setBathrooms(numBathrooms);
                    mProperty.setRent(rent);
                    mProperty.setUtilitiesIncluded(includesUtilities);
                    mProperty.setPetsAllowed(petsAllowed);
                    mProperty.setOwnerId(user.getUid());
                }else{
                    mProperty = new Property(getContext(), user.getUid(), address, rent, numBedrooms, numBathrooms, petsAllowed, includesUtilities);
                }


                DatabaseManager.getInstance().AddProperty(mProperty);

                getActivity().onBackPressed();
            }
        });

        mDeleteButton = (FloatingActionButton) view.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update Property object
                if(mProperty != null){
                    DatabaseManager.getInstance().DeleteProperty(mProperty);
                }

                getActivity().onBackPressed();
            }
        });

        if(mProperty != null){
            updateUI();
        }

        return view;
    }

    private void updateUI(){
        Log.d(TAG, "updateUI()");
        try{
            if(mProperty != null){
                mAddress.setText(mProperty.getAddress());
                mBedrooms.setText(String.valueOf(mProperty.getBedrooms()));
                mBathrooms.setText(String.valueOf(mProperty.getBathrooms()));
                mRent.setText(String.valueOf(mProperty.getRent()));
                mUtilitiesIncluded.setChecked(mProperty.areUtilitiesIncluded());
                mPetsAllowed.setChecked(mProperty.arePetsAllowed());
            }
        }finally {
            // this means mProperty was updated before UI objects were declared
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        final String propertyId = (String) getArguments().getSerializable(ARG_PROPERTY_ID);
        Log.d(TAG, "onCreate: propertyId received: " + propertyId);
        DatabaseReference ref = DatabaseManager.getInstance().GetPropertyReference(propertyId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading property data");
                mProperty = dataSnapshot.getValue(Property.class);
                if(mProperty != null){
                    mProperty.setId(propertyId);
                    updateUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });
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