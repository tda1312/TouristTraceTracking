package vn.edu.usth.touristtracetracking.History;

import androidx.fragment.app.FragmentActivity;

import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
    PolylineOptions line;

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
        mMap = googleMap;
        builder = new LatLngBounds.Builder();

        getHistoryFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ABCDE", "On resume here!");
        getHistoryFromServer();
    }

    private void getHistoryFromServer() {

        SharePrefManager sharePrefManager = SharePrefManager.getInstance(HistoryActivity.this);
        User user = sharePrefManager.getUser();

        Call<GetHistoryResponse> call = RetrofitHandler.getInstance()
                .getApi().getHistory(user.getId(), "Bearer " + sharePrefManager.getToken());
        call.enqueue(new Callback<GetHistoryResponse>() {
            @Override
            public void onResponse(Call<GetHistoryResponse> call, Response<GetHistoryResponse> response) {
                GetHistoryResponse responseBody = response.body();

                if (responseBody != null && responseBody.isSuccess() && responseBody.getData().size() != 0) {
                    locationList = responseBody.getData();
                    displayList(locationList);
                } else if (responseBody.getData().size() == 0){
                    Toast.makeText(HistoryActivity.this, "You don't have any history! Please come back later!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HistoryActivity.this, "Failed to get history!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetHistoryResponse> call, Throwable t) {
                Log.i("ABCDE", t.getMessage());
            }
        });
    }

    private void displayList(List<LocationData> locationList) {
        line = new PolylineOptions();
        for(LocationData location : locationList){
            double latitude = Double.parseDouble(location.getLatitude());
            double longitude = Double.parseDouble(location.getLongitude());

            // display the history of today only
            if (isToday(location.getArrival_at())) {
                LatLng latLng = new LatLng(latitude, longitude);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("(" + latitude + ";" + longitude + ")")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                marker = mMap.addMarker(markerOptions);
                builder.include(markerOptions.getPosition());
                // zoom when each marker is created
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                line.add(latLng);
                bounds = builder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 500);
                mMap.moveCamera(cameraUpdate);
                mMap.addPolyline(line);
            }
        }
    }

    public static boolean isToday(String datestring) {
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = format.parse(datestring);
            Date today = Calendar.getInstance().getTime();
            if (date.getDate() == today.getDate() && date.getMonth() == today.getMonth()) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("ABCDE", "ParseException error!");
        }
        return false;
    }
}