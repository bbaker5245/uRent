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
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by rflukes on 11/3/17.
 */

public class ViewPropertyFragment extends Fragment{
    private static final String TAG = "PropertyFragment";
    private static final String ARG_PROPERTY_ID = "property_id";

    private Property mProperty;
    private TextView mAddress;
    private TextView mBedrooms;
    private TextView mBathrooms;
    private TextView mRent;
    private TextView mUtilitiesIncluded;
    private TextView mPetsAllowed;
    private FloatingActionButton mBackButton;
    private FloatingActionButton mMessageButton;

    public static ViewPropertyFragment newInstance(String propertyId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY_ID, propertyId);
        ViewPropertyFragment fragment = new ViewPropertyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_view_property, container, false);

        mAddress = (TextView) view.findViewById(R.id.address_input);
        mBedrooms = (TextView) view.findViewById(R.id.bedroom_input);
        mBathrooms = (TextView) view.findViewById(R.id.bathroom_input);
        mRent = (TextView) view.findViewById(R.id.rent_input);
        mUtilitiesIncluded = (TextView) view.findViewById(R.id.utilities_input);
        mPetsAllowed = (TextView) view.findViewById(R.id.pets_input);

        mBackButton = (FloatingActionButton) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mMessageButton = (FloatingActionButton) view.findViewById(R.id.message_button);

        return view;
    }

    private void updateUI(){
        Log.d(TAG, "updateUI()");
        try{
            if(mProperty != null){
                mAddress.setText(mProperty.getAddress());
                mBedrooms.setText("Bedrooms:" + String.valueOf(mProperty.getBedrooms()));
                mBathrooms.setText("Bathrooms:" + String.valueOf(mProperty.getBathrooms()));
                mRent.setText("Rent:" + String.valueOf(mProperty.getRent()));
                if(mProperty.arePetsAllowed()){
                    mPetsAllowed.setText("Pets are allowed");
                }else{
                    mPetsAllowed.setText("Pets are not allowed");
                }
                if(mProperty.areUtilitiesIncluded()){
                    mUtilitiesIncluded.setText("Utilities are included");
                }else{
                    mUtilitiesIncluded.setText("Utilities are not included");
                }

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
        DatabaseReference ref = DatabaseManager.getInstance(getContext()).GetPropertyReference(propertyId);
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
