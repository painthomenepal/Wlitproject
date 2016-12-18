package com.example.admin.googlemapsdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    LocationManager lm;

    /**
     * Apply this following logical sequence in your {@code onCreate}
     * <p>
     * 1) Instantiate the {@link SupportMapFragment} via {@link SupportMapFragment#getMapAsync(OnMapReadyCallback)}
     * <p>
     * Using the {@link SupportMapFragment#getMapAsync(OnMapReadyCallback)} assures that the {@link GoogleMap} object is not null
     * <p>
     * 2)Check if location permission is enabled via {@link ActivityCompat#checkSelfPermission(Context, String)}
     * <p>
     * In cases where permissions are not granted, request permission via {@link ActivityCompat#requestPermissions(Activity, String[], int)}
     * The result to the permission request call is captured via {@link #onRequestPermissionsResult(int, String[], int[])}
     * which is similar to how {@link Activity#startActivityForResult(Intent, int)}.
     * <p>Please refer to <a href="https://goo.gl/TrNeVC">documentation</a> for more info
     * <p>
     * While asking for permission, there is a possibility that the user can deny access. In such cases
     * Android provides a way for the app to provide justification via
     * {@link ActivityCompat#shouldShowRequestPermissionRationale(Activity, String)}
     * <p>Please refer to <a href="https://goo.gl/VdCpeO">documentation</a> for more info
     * <p>
     * 3) Check if {@link LocationManager#GPS_PROVIDER} is enabled
     * <p>
     * This step is not necessary, you can ignore this altogether because we are using {@link GoogleApiClient}
     * for location requests. But asking the user to trigger his/her GPS allows for an accurate reading
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 1) check if location permission is enabled
        // if not, ask for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        } else {

            // 2) check if GPS is enabled, not necessary but increases accuracy
            // if not, activate
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //enable loation
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Activate GPS")
                        .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        }).setMessage("Please activate GPS");
                builder.create().show();
            } else {
                // call 3) google api, get location
                client = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API).build();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int i = 0; i < grantResults.length; i++) {
                if (i == PermissionChecker.PERMISSION_DENIED) {
                    // re request for permission
                    //
                }
            }

            // check if GPS is activated

        }
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

        // add location marker here
        // or list down nearby banks here
        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
            if (location != null) {
                // Getting latitude of the current location
                double latitude = location.getLatitude();

                // Getting longitude of the current location
                double longitude = location.getLongitude();

                // Creating a LatLng object for the current location
                LatLng latLng = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
