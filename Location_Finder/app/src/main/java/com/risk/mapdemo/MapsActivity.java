package com.risk.mapdemo;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS =10000;
    double latitude,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient(){
        client = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this) //Registers a listener to receive connection events from this GoogleApiClient.
                            .addOnConnectionFailedListener(this) //Adds a listener to register to receive connection failed events from this GoogleApiClient.
                            .addApi(LocationServices.API).build();
        client.connect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000); //https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest,this); //If your app can continuously track location, it can deliver more relevant information to the user.-FusedLocationAPI
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){ //Gets whether you should show UI with rationale for requesting a permission.
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);

            }
            return false;
        }
        else return  true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission is granted
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(client ==null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{ //permission is denied

                    Toast.makeText(this,"Permission_Denied!", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }
        Log.d("lat =", ""+ latitude);
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    public void onClick(View v){
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        String url;
        switch(v.getId())
        {
            case R.id.imgbSearch:
                EditText etSearch = (EditText) findViewById(R.id.etSearch);
                String location = etSearch.getText().toString();
                List<android.location.Address> addressList = null;
                MarkerOptions mo = new MarkerOptions();

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                            addressList = geocoder.getFromLocationName(location, 5);

                        if(addressList!=null) {
                            for (int i = 0; i < addressList.size(); i++) {
                                android.location.Address myAddress = addressList.get(i);
                                LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                                latitude = myAddress.getLatitude();
                                longitude= myAddress.getLongitude();
                                mo.position(latLng);
                                mo.title(location);
                                mMap.addMarker(mo);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.btn_Restaurants:
                mMap.clear();
                String restaurant = "restaurant";
                url = getUrl(latitude,longitude,restaurant);
                dataTransfer[0] =mMap;
                dataTransfer[1]= url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this,"showing nearby restaurants",Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_Schools:
                mMap.clear();
                String school = "school";
                url = getUrl(latitude,longitude,school);

                dataTransfer[0] =mMap;
                dataTransfer[1]= url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this,"showing nearby schools",Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_Hospitals:
                mMap.clear();
                String hospital = "hospital";
                url = getUrl(latitude,longitude,hospital);

                dataTransfer[0] =mMap;
                dataTransfer[1]= url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this,"showing nearby hospitals",Toast.LENGTH_LONG).show();
                break;
        }
    }
    private String getUrl(double latitude, double longitude, String nearByPlace){

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location"+ "=" + latitude + ","+ longitude);
        googlePlacesUrl.append("&radius="+ PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type="+ nearByPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyBDrQjHTPLhAavHpCYstlm0x1IQK5KhovA");
        Log.d("MapsActivity","url = " + googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }


}
