package com.baker.flukes.urent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.util.List;

/**
 * Created by Brian on 10/4/2017.
 */

public class PropertyListFragment extends Fragment {

    private RecyclerView mPropertyRecyclerView;
    private PropertyAdapter mAdapter;
    private Button mMapButton;
    private static final String TAG = "PropertyListFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_property_list, container, false);

        mPropertyRecyclerView = (RecyclerView) view.findViewById(R.id.property_recycler_view);
        mPropertyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mMapButton = (Button) container.findViewById(R.id.map_button);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });

        updateUI();

        return view;
    }

    private void updateUI() {
        PropertyManager propertyManager = PropertyManager.get(getActivity());
        List<Property> properties = propertyManager.getProperties();

        mAdapter = new PropertyAdapter(properties);
        mPropertyRecyclerView.setAdapter(mAdapter);
    }

    private class PropertyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Property mProperty;
        private TextView mAddressTextView;

        public PropertyHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_property, parent, false));
            itemView.setOnClickListener(this);

            mAddressTextView = (TextView) itemView.findViewById(R.id.property_address);
        }

        public void bind(Property property){
            mProperty = property;
            mAddressTextView.setText(mProperty.getAddress());
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), mProperty.getAddress() + " clicked!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, mProperty.getAddress() + " clicked!");
            Log.e(TAG, mProperty.getAddress() + " clicked!");
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
