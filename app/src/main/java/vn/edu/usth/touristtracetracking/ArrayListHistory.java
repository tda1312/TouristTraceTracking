package vn.edu.usth.touristtracetracking;

import java.util.ArrayList;

import vn.edu.usth.touristtracetracking.Foreground_service.LocationData;

public class ArrayListHistory {
    private ArrayList<LocationData> history;

    public ArrayListHistory(ArrayList<LocationData> history) {
        this.history = history;
    }

    public ArrayList<LocationData> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<LocationData> history) {
        this.history = history;
    }
}
