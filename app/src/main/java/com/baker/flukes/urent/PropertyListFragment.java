package com.baker.flukes.urent;

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
import android.widget.TextView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 10/4/2017.
 */

public class PropertyListFragment extends Fragment {

    private static final String TAG = "PropertyListFragment";
    private static final String ARG_UNIVERSITY_ID = "university_id";
    public static final String EXTRA_UNIVERSITY_ID = "com.baker.flukes.urent.university_id";
    private static final String ARG_USER_ID = "user_id";

    private DatabaseReference mPropertiesDatabase;
    private DatabaseReference mUniversityDatabase;
    private DatabaseReference mUserDatabase;
    private List<String> propertyIds;
    private List<Property> mProperties;

    private RecyclerView mPropertyRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    private PropertyAdapter mAdapter;
    private FloatingActionButton mMapButton;
    private FloatingActionButton mAddPropertyButton;
    private static boolean showMapButton;

    public static PropertyListFragment newInstanceForUniversity(String universityId){
        showMapButton = true;
        Bundle args = new Bundle();
        args.putSerializable(ARG_UNIVERSITY_ID, universityId);
        PropertyListFragment fragment = new PropertyListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static PropertyListFragment newInstanceForUser(String userId){
        showMapButton = false;
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_ID, userId);
        PropertyListFragment fragment = new PropertyListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_property_list, container, false);

        mProperties = new ArrayList<>();
        mPropertiesDatabase = DatabaseManager.getInstance(getContext()).GetPropertyListReference();
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
                newProperty.setId(propertyKey);

                for(int i = 0; i < mProperties.size(); i++){
                    if(mProperties.get(i).getId().equals(propertyKey)){
                        mProperties.set(i, newProperty);
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
        mPropertiesDatabase.addChildEventListener(childEventListener);
        mPropertiesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading initial data for all properties");
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });

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
                Intent intent = PropertyActivity.newIntent(getActivity(), null);
                startActivity(intent);
            }
        });

        mPropertyRecyclerView = (RecyclerView) view.findViewById(R.id.property_recycler_view);
        mPropertyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mMapButton = (FloatingActionButton) view.findViewById(R.id.map_button);
        if(!showMapButton){
            mMapButton.setVisibility(View.INVISIBLE);
        }
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String university_id = (String) getArguments().getSerializable(ARG_UNIVERSITY_ID);
                Intent intent = MapsActivity.newIntentForMaps(getActivity(),university_id);
                startActivity(intent);
            }
        });
        return view;
    }

    private void updateUI() {
        Log.d(TAG, "updateUI");
        if(propertyIds == null){
            Log.d(TAG, "full property list being displayed");
            mAdapter = new PropertyAdapter(mProperties);
        }else{
            Log.d(TAG, "filtered property list being displayed");
            List<Property> filteredProperties = new ArrayList<>();
            for(Property property : mProperties){
                if(propertyIds.contains(property.getId())){
                    filteredProperties.add(property);
                }
            }
            mAdapter = new PropertyAdapter(filteredProperties);
        }
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
            if(mProperty.getOwnerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                Intent intent = PropertyActivity.newIntent(getActivity(), mProperty.getId());
                startActivity(intent);
            }else{
                Intent intent = ViewPropertyActivity.newIntent(getActivity(), mProperty.getId());
                startActivity(intent);
            }
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
        if(getArguments().containsKey(ARG_UNIVERSITY_ID)){
            propertyIds = new ArrayList<>();
            final String universityId = (String) getArguments().getSerializable(ARG_UNIVERSITY_ID);
            Log.d(TAG, "onCreate: universityId received: " + universityId);
            mUniversityDatabase = DatabaseManager.getInstance(getContext()).GetUniversityPropertyListReference(universityId);
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                    propertyIds.add(dataSnapshot.getKey());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A property has changed, use the key to determine if we are displaying this
                    // property and if so remove it.
                    String propertyKey = dataSnapshot.getKey();
                    int toRemove = -1;
                    for(int i = 0; i < propertyIds.size(); i++){
                        if(propertyIds.get(i).equals(propertyKey)){
                            toRemove = i;
                            break;
                        }
                    }
                    if(toRemove >= 0){
                        propertyIds.remove(toRemove);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                    // don't care about this
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postProperties:onCancelled", databaseError.toException());
                    //Toast.makeText(context, "Failed to load properties.", Toast.LENGTH_SHORT).show();
                }
            };
            mUniversityDatabase.addChildEventListener(childEventListener);
            mUniversityDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "done loading initial data for university properties");
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "error loading initial data");
                }
            });
        }else if(getArguments().containsKey(ARG_USER_ID)){
            propertyIds = new ArrayList<>();
            final String userId = (String) getArguments().getSerializable(ARG_USER_ID);
            Log.d(TAG, "onCreate: userId received: " + userId);
            mUserDatabase = DatabaseManager.getInstance(getContext()).GetUserPropertyListReference(userId);
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                    propertyIds.add(dataSnapshot.getKey());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A property has changed, use the key to determine if we are displaying this
                    // property and if so remove it.
                    String propertyKey = dataSnapshot.getKey();
                    int toRemove = -1;
                    for(int i = 0; i < propertyIds.size(); i++){
                        if(propertyIds.get(i).equals(propertyKey)){
                            toRemove = i;
                            break;
                        }
                    }
                    if(toRemove >= 0){
                        propertyIds.remove(toRemove);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                    // don't care about this
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postProperties:onCancelled", databaseError.toException());
                    //Toast.makeText(context, "Failed to load properties.", Toast.LENGTH_SHORT).show();
                }
            };
            mUserDatabase.addChildEventListener(childEventListener);
            mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "done loading initial data for user properties");
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "error loading initial data");
                }
            });
        }else{
            propertyIds = null;
            Log.d(TAG, "onCreate: no userId or universityId received ");
        }
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
        updateUI();
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
