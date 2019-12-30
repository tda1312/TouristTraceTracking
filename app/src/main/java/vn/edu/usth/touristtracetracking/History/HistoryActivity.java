package vn.edu.usth.touristtracetracking.History;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.edu.usth.touristtracetracking.R;

public class HistoryActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLngBounds bounds;
    private LatLngBounds.Builder builder;
    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    LocationManager locationManager;
    Marker marker;

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



        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("lat", "21.02852");
            jsonObject1.put("long", "105.80483");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject2 = new JSONObject();
        try{
            jsonObject2.put("lat", "21.028611");
            jsonObject2.put("long", "105.804917");
        } catch (JSONException e){
            e.printStackTrace();
        }

        JSONObject jsonObject3 = new JSONObject();
        try{
            jsonObject3.put("lat", "21.02873");
            jsonObject3.put("long", "105.80504");
        } catch (JSONException e){
            e.printStackTrace();
        }

        JSONObject jsonObject4 = new JSONObject();
        try{
            jsonObject4.put("lat", "21.02884");
            jsonObject4.put("long", "105.80515");
        } catch (JSONException e){
            e.printStackTrace();
        }

        JSONObject jsonObject5 = new JSONObject();
        try{
            jsonObject5.put("lat", "21.02895");
            jsonObject5.put("long", "105.80526");
        } catch (JSONException e){
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject1);
        jsonArray.put(jsonObject2);
        jsonArray.put(jsonObject3);
        jsonArray.put(jsonObject4);
        jsonArray.put(jsonObject5);

        for (int i = 0; i < jsonArray.length(); i ++){
            try {
                JSONObject coord = jsonArray.getJSONObject(i);
                double latitude = coord.getDouble("lat");
                double longitude = coord.getDouble("long");

                LatLng latLng = new LatLng(latitude, longitude);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                marker = mMap.addMarker(markerOptions);
                builder.include(markerOptions.getPosition());
                // zoom when each marker is created
                // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                line.add(latLng);

            } catch (JSONException e){
                e.printStackTrace();
            }

            // put delay to display marker
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException ie){
                ie.printStackTrace();
            }*/
        }

        bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 500);
        mMap.moveCamera(cameraUpdate);
        mMap.addPolyline(line);

    }


}