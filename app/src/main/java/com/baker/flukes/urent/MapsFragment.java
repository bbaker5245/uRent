package com.baker.flukes.urent;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rflukes on 10/30/17.
 */

public class MapsFragment extends SupportMapFragment {
    private GoogleMap mMap;
    private static final String TAG = "MapsFragment";
    private static final String ARG_UNIVERSITY_ID = "university_id";
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private static final int REQUEST_ERROR = 0;
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private GoogleApiClient mClient;
    private Location mLocation;
    private DatabaseReference mPropertiesDatabase;
    private List<Property> mProperties;
    private Map<String,String> universities = new HashMap<>();


    public static MapsFragment newInstance(String universityId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_UNIVERSITY_ID, universityId);
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        request.setNumUpdates(100);
        request.setInterval(0);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG, "Got a fix: " + location);
                        mMap.setMyLocationEnabled(true);
                    }
                });
    }

    private boolean hasLocationPermission() {
        int result = ContextCompat
                .checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        universities.put("-Kxl7JXZpVyIMBP_GQ-8","The Ohio Union - Ohio State University");
        universities.put("-KxlBUACq7Vh6VTgprR3","Michigan Union");
        universities.put("-KxlBUA_tM2BceD1VrcL","Penn State University");
        universities.put("-KxlBUAbxBwrZNOqf7qn","49 Abbot Rd, East Lansing, MI");

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Bundle args = getArguments();
                String university_id = args.getSerializable(ARG_UNIVERSITY_ID).toString();
                String university = universities.get(university_id);
                mMap = googleMap;
                LatLng osu = new LatLng(39.9976772, -83.0085753);
                mMap.setMinZoomPreference(12.0f);
                try {
                    LatLng property  = getLocationFromAddress(getContext(), university);
                    if(property != null){
                        mMap.addMarker(new MarkerOptions().position(property).title(university));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(property));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

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
                        addPropertiesToMap();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "error loading initial data");
                    }
                });
            }
        });
    }
    private void addPropertiesToMap(){
        Log.d(TAG, "adding Properties to map");
        for(Property property : mProperties){
            try{
                LatLng latLng = getLocationFromAddress(getContext(), property.getAddress());
                if(latLng != null){
                    mMap.addMarker(new MarkerOptions().position(latLng).title(property.getAddress()));
                }
            }catch (IOException e){
                Log.d(TAG,e.getMessage());
            }


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
        getActivity().invalidateOptionsMenu();

    }
    @Override
    public void onResume() {
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(getContext());

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog(getActivity(), errorCode, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {

                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    // Leave if services are unavailable.
                                    getActivity().finish();
                                }
                            });

            errorDialog.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_maps, menu);
        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                if (hasLocationPermission()) {
                    Log.d(TAG, "hasLocationPermission");
                    getLocation();
                }else {
                    requestPermissions(LOCATION_PERMISSIONS,
                            REQUEST_LOCATION_PERMISSIONS);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static LatLng getLocationFromAddress(Context context, String strAddress) throws IOException {

        Geocoder coder = new Geocoder(context);
        List<Address> addresses;
        LatLng p1 = null;

        try {
            addresses = coder.getFromLocationName(strAddress,5);
            if (addresses!=null && addresses.size()>0) {
                Address location=addresses.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
                return p1;
            }

        }catch (Exception e){
            System.out.print(e);
        }
        return p1;
    }
}
