package vn.edu.usth.touristtracetracking;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import com.michaldrabik.tapbarmenulib.TapBarMenu;

// 10 DECEMBER 2019
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public boolean firstTime = true;

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    LocationManager locationManager;
    Marker marker;

    //Tap Bar Menu
    // TapBarMenu tapBarMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Tab Bar Menu created
        /* tapBarMenu = findViewById(R.id.tapBarMenu);
        tapBarMenu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                tapBarMenu.toggle();
            }
        });

        ImageView item1 = findViewById(R.id.item1);
        ImageView item2 = findViewById(R.id.item2);
        ImageView item3 = findViewById(R.id.item3);
        ImageView item4 = findViewById(R.id.item4);

        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuItemClick(v);
            }
        });

        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuItemClick(v);
            }
        });

        item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuItemClick(v);
            }
        });

        item4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuItemClick(v);
            }
        }); */
        // Set onClickListeners

        // Checks for permissions, request if not granted
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);

            return;
        }

        // If there is a network provider (viettel, mobiphone,...) then use that for location
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Get coordinates
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);

                    Geocoder geocoder = new Geocoder(getApplicationContext());


                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String result = "";

                        String cityName = addressList.get(0).getAddressLine(0);
                        String stateName = addressList.get(0).getAddressLine(1);
                        String countryName = addressList.get(0).getAddressLine(2);

                        result = result + " " + cityName + stateName + countryName;

                        // If there is already a marker, replace that
                        if (marker != null){
                            marker.remove();
                            marker = mMap.addMarker(new MarkerOptions().
                                    position(latLng).
                                    title(result)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            mMap.setMaxZoomPreference(20);
                        }

                        // Else add new one
                        else {
                            marker = mMap.addMarker(new MarkerOptions().
                                    position(latLng).
                                    title(result)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            mMap.setMaxZoomPreference(20);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

            // Else we can request from GPS provider, the code is exactly the same
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude,longitude);

                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

                        String result = "";
                        String cityName = addressList.get(0).getAddressLine(0);
                        String stateName = addressList.get(0).getAddressLine(1);
                        String countryName = addressList.get(0).getAddressLine(2);

                        result = result + " " + cityName + stateName + countryName;
                        if (marker != null){
                            marker.remove();
                            marker = mMap.addMarker(new MarkerOptions().
                                            position(latLng).
                                            title(result)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            mMap.setMaxZoomPreference(20);
                        }
                        else {
                            marker = mMap.addMarker(new MarkerOptions().
                                    position(latLng).
                                    title(result)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            mMap.setMaxZoomPreference(20);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }


    }

    /*public void onMenuItemClick(View view) {
        tapBarMenu.close();
        switch (view.getId()) {
            case R.id.item1:
                Log.i("TAG", "Item 1 selected");
                Intent UserProfileIntent = new Intent(MapsActivity.this, UserProfileActivitiy.class);
                startActivity(UserProfileIntent);
                break;
            case R.id.item2:
                Log.i("TAG", "Item 2 selected");
                Intent MapIntent = new Intent(MapsActivity.this, MapsActivity.class);
                startActivity(MapIntent);
                break;
            case R.id.item3:
                Log.i("TAG", "Item 3 selected");
                Intent FavoriteIntent = new Intent(MapsActivity.this, FavoriteActivity.class);
                startActivity(FavoriteIntent);
                break;
            case R.id.item4:
                Log.i("TAG", "Item 4 selected");
                Intent SettingsIntent = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(SettingsIntent);
                break;
        }
    } */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);

            return;
        }

        // If first time open, zoom in then allow user to move around while updating the markers
        if (firstTime) {
            firstTime = false;
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }

    }
}
