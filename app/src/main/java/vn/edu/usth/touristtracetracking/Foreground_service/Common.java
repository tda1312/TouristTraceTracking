package vn.edu.usth.touristtracetracking.Foreground_service;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

public class Common {
    public static final String KEY_REQUESTTING_LOCATION_UPDATES = "LocationUpdateEnabled";

    public static String getLocationText(Location mLocation) {
        return mLocation == null ? "Unknown Location" : new StringBuilder()
                .append("(" +mLocation.getLatitude())
                .append("; ")
                .append(mLocation.getLongitude() +")")
                .toString();
    }

    public static CharSequence getLocationTitle(MyBackgroundService myBackgroundService) {
        return String.format("Location Updated: %1$s", DateFormat.getDateInstance().format(new Date()));
    }

    public static void setRequestStringLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTTING_LOCATION_UPDATES, value)
                .apply();
    }

    public static boolean requestStringLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTTING_LOCATION_UPDATES, false);
    }
}
