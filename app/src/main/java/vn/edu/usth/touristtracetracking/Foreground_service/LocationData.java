package vn.edu.usth.touristtracetracking.Foreground_service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationData {
    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("arrival_time")
    @Expose
    private String arrival_time;

    public LocationData(String latitude, String longitude, String arrival_time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrival_time = arrival_time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }
}
