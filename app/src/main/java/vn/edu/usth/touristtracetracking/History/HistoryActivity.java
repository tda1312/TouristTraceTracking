package vn.edu.usth.touristtracetracking.History;

import androidx.fragment.app.FragmentActivity;

import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.touristtracetracking.ForegroundService.LocationData;
import vn.edu.usth.touristtracetracking.R;
import vn.edu.usth.touristtracetracking.RetrofitHandler;
import vn.edu.usth.touristtracetracking.User;
import vn.edu.usth.touristtracetracking.storage.SharePrefManager;

public class HistoryActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLngBounds bounds;
    private LatLngBounds.Builder builder;
    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    LocationManager locationManager;
    Marker marker;

    // request variables
    private List<LocationData> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_history);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        PolylineOptions line = new PolylineOptions();
        mMap = googleMap;
        builder = new LatLngBounds.Builder();

        SharePrefManager sharePrefManager = SharePrefManager.getInstance(HistoryActivity.this);
        User user = sharePrefManager.getUser();

        Call<GetHistoryResponse> call = RetrofitHandler.getInstance()
                .getApi().getHistory(user.getId(), "Bearer " + sharePrefManager.getToken());
        call.enqueue(new Callback<GetHistoryResponse>() {
            @Override
            public void onResponse(Call<GetHistoryResponse> call, Response<GetHistoryResponse> response) {
                GetHistoryResponse response_body = response.body();

                if (response_body != null && response_body.isSuccess()) {
                    locationList = response_body.getData();
                    for (int i = 0; i < locationList.size(); i++) {

                        LocationData location = locationList.get(i);
                        double latitude = Double.parseDouble(location.getLatitude());
                        double longitude = Double.parseDouble(location.getLongitude());
                        LatLng latLng = new LatLng(latitude, longitude);

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title("")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        marker = mMap.addMarker(markerOptions);
                        builder.include(markerOptions.getPosition());
                        // zoom when each marker is created
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                        line.add(latLng);
                    }

                    bounds = builder.build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 500);
                    mMap.moveCamera(cameraUpdate);
                    mMap.addPolyline(line);

                } else {
                    Log.i("ABCDE", response_body.isSuccess() + " oh no :((((");
                }
            }

            @Override
            public void onFailure(Call<GetHistoryResponse> call, Throwable t) {
                Log.i("ABCDE", t.getMessage());
            }
        });
    }
}