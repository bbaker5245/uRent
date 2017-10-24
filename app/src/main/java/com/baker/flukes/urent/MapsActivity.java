package com.baker.flukes.urent;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng osu = new LatLng(39.9976772, -83.0085753);
        mMap.setMinZoomPreference(15.0f);
        mMap.addMarker(new MarkerOptions().position(osu).title("OSU"));
        String address = "132 East 12th Ave, Columbus, OH";
        LatLng property = new LatLng(39.9976762, -83.0085753);
        try {
            property = getLocationFromAddress(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Log.d(TAG,getLocationFromAddress(address).toString());
        }catch (IOException e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().position(property).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(property));
    }

    public  LatLng getLocationFromAddress(String strAddress) throws IOException {

        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            return p1;
        }catch (Exception e){
            System.out.print(e);
        }
        return p1;
    }
}
