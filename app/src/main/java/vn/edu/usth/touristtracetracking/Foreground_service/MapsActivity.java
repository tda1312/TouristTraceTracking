package vn.edu.usth.touristtracetracking.Foreground_service;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import vn.edu.usth.touristtracetracking.R;

// 10 DECEMBER 2019
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public boolean firstTime = true;
    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    LocationManager locationManager;
    Marker marker;
    boolean cameraSet = false;

    // Background service variables
    MyBackgroundService mService = null;
    boolean mBound = false;
    Double latitude, longitude;

    // Create instance of service connection
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MyBackgroundService.LocalBinder binder = (MyBackgroundService.LocalBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    //Tap Bar Menu
    // TapBarMenu tapBarMenu;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                    userLocationFAB(location);

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
                        if (marker != null) {
                            marker.remove();
                            marker = mMap.addMarker(new MarkerOptions().
                                    position(latLng).
                                    title(result)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//                            mMap.setMaxZoomPreference(20);
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                        }

                        // Else add new one
                        else {
                            marker = mMap.addMarker(new MarkerOptions().
                                    position(latLng).
                                    title(result)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            if(!cameraSet) {
                                mMap.setMaxZoomPreference(20);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                                cameraSet = true;
                            }

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

                    userLocationFAB(location);

                    LatLng latLng = new LatLng(latitude, longitude);

                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String result = "";
                        String cityName = addressList.get(0).getAddressLine(0);
                        String stateName = addressList.get(0).getAddressLine(1);
                        String countryName = addressList.get(0).getAddressLine(2);

                        result = result + " " + cityName + stateName + countryName;
                        if (marker != null) {
                            marker.remove();
                            marker = mMap.addMarker(new MarkerOptions().
                                    position(latLng).
                                    title(result)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//                            mMap.setMaxZoomPreference(20);
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                        } else {
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            if(!cameraSet) {
                                mMap.setMaxZoomPreference(20);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                                cameraSet = true;
                            }
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

        // Use dexter to manage permissions easier
        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ))
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        bindService(new Intent(MapsActivity.this, MyBackgroundService.class),
                                mServiceConnection,
                                Context.BIND_AUTO_CREATE);
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
        // Automatically run service after starting the activity 2s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mService.requestLocationUpdates();
            }
        }, 5000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event) {
        if (event != null){
            String data = event.getLocation().getLatitude() +
                    "; " +
                    event.getLocation().getLongitude();
            latitude = event.getLocation().getLatitude();
            longitude = event.getLocation().getLongitude();
            Toast.makeText(mService, "Your current location:\n" + data, Toast.LENGTH_SHORT).show();
        }
    }

    private void userLocationFAB(Location location) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        //Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        final LatLng latLng = new LatLng(latitude, longitude);

        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.button_location);

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
            }
        });
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
                Intent FavoriteIntent = new Intent(MapsActivity.this, HistoryActivity.class);
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
        //userLocationFAB();
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }



        // If first time open, zoom in then allow user to move around while updating the markers
        /*if (firstTime) {
            firstTime = false;
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        }*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "YAYYYYY", Toast.LENGTH_SHORT).show();
            recreate();
        } else {
            Toast.makeText(getApplicationContext(),"NAYYYY", Toast.LENGTH_SHORT).show();
        }
    }
}
