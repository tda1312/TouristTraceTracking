package vn.edu.usth.touristtracetracking.ForegroundService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationData {
    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("arrival_at")
    @Expose
    private String arrival_at;

    public LocationData(String latitude, String longitude, String arrival_at) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrival_at = arrival_at;
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

    public String getArrival_at() {
        return arrival_at;
    }

    public void setArrival_at(String arrival_time) {
        this.arrival_at = arrival_time;
    }
}
