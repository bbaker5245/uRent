package com.baker.flukes.urent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

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
 * Created by Brian on 10/4/2017.
 */

public class PropertyListFragment extends Fragment {

    private DatabaseReference mDatabase;
    private List<Property> mProperties;

    private RecyclerView mPropertyRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    private PropertyAdapter mAdapter;
    private Button mMapButton;
    private FloatingActionButton mAddPropertyButton;
    private static final String TAG = "PropertyListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_property_list, container, false);

        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh called");
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                updateUI();
            }
        });
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mAddPropertyButton = (FloatingActionButton) view.findViewById(R.id.add_property_button);
        mAddPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "addProperty called");

                Property newProperty = new Property("1234 added", 200, 3, 4, true, false);

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
                        updateUI();
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
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });

        mPropertyRecyclerView = (RecyclerView) view.findViewById(R.id.property_recycler_view);
        mPropertyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //mMapButton = (Button) container.findViewById(R.id.map_button);
        //mMapButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
            //    Intent intent = new Intent(getActivity(), MapsActivity.class);
            //    startActivity(intent);
            //}
        //});
        return view;
    }

    private void updateUI() {
        Log.d(TAG, "updateUI");
        mAdapter = new PropertyAdapter(mProperties);
        mPropertyRecyclerView.setAdapter(mAdapter);
        mSwipeContainer.setRefreshing(false);
    }

    private class PropertyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Property mProperty;
        private TextView mAddressTextView;
        private TextView mRentTextView;

        public PropertyHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_property, parent, false));
            itemView.setOnClickListener(this);

            mAddressTextView = (TextView) itemView.findViewById(R.id.property_address);
            mRentTextView = (TextView) itemView.findViewById(R.id.property_rent);
        }

        public void bind(Property property){
            mProperty = property;
            mAddressTextView.setText(mProperty.getAddress());
            mRentTextView.setText("$" + mProperty.getRent());
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "removing property from database");
            Toast.makeText(getActivity(), mProperty.getAddress() + " deleted from firebase", Toast.LENGTH_SHORT).show();

            // Get the id of this property
            String key = mProperty.getId();

            // Delete it from the database
            mDatabase.child("/" + key).removeValue();

            // Listen to update UI after deletion is made
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "done loading initial data");
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "error loading initial data");
                }
            });

            Log.d(TAG, mProperty.getAddress() + " clicked!");
        }
    }

    private class PropertyAdapter extends RecyclerView.Adapter<PropertyHolder>{
        private List<Property> mProperties;

        public PropertyAdapter(List<Property> properties){
            mProperties = properties;
        }

        @Override
        public PropertyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PropertyHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PropertyHolder holder, int position) {
            Property property = mProperties.get(position);
            holder.bind(property);
        }

        @Override
        public int getItemCount() {
            return mProperties.size();
        }
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
