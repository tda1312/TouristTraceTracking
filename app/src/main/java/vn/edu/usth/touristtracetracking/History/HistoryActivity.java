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
    final static String ISO8601DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSZ";

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

    private void getHistoryFromServer() {

        SharePrefManager sharePrefManager = SharePrefManager.getInstance(HistoryActivity.this);
        User user = sharePrefManager.getUser();

        Call<GetHistoryResponse> call = RetrofitHandler.getInstance()
                .getApi().getHistory(user.getId(), "Bearer " + sharePrefManager.getToken());
        call.enqueue(new Callback<GetHistoryResponse>() {
            @Override
            public void onResponse(Call<GetHistoryResponse> call, Response<GetHistoryResponse> response) {
                GetHistoryResponse responseBody = response.body();

                if (responseBody != null && responseBody.isSuccess()) {
                    locationList = responseBody.getData();
                    displayList(locationList);
                } else {
                    Toast.makeText(HistoryActivity.this, "You don't have any history", Toast.LENGTH_SHORT).show();
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
        for (int i = 0; i < locationList.size(); i++) {
            LocationData location = locationList.get(i);
            double latitude = Double.parseDouble(location.getLatitude());
            double longitude = Double.parseDouble(location.getLongitude());

            String dtStart = location.getArrival_at();
            boolean test = isToday(dtStart);
            if (test) {
                Log.i("ABCDE", location.getArrival_at() + " is " + test);
                LatLng latLng = new LatLng(latitude, longitude);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                marker = mMap.addMarker(markerOptions);
                builder.include(markerOptions.getPosition());
                // zoom when each marker is created
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));

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
        int historyDate, historyMonth;
        try {
            Date date = format.parse(datestring);
            Date today = Calendar.getInstance().getTime();

            historyDate = date.getDate();
            historyMonth = date.getMonth();
            Log.i("ABCDE", date + " has " + historyDate + " " + historyMonth);
            Log.i("ABCDE", "Today has " + today.getDate() + " " + today.getMonth());

            if (historyDate == today.getDate() && historyMonth == today.getMonth()) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("ABCDE", "Error");
        }
        Log.i("ABCDE", "faileheheheh");
        return false;
    }
}