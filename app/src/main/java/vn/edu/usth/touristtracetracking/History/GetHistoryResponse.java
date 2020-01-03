package vn.edu.usth.touristtracetracking.History;

import java.util.List;

import vn.edu.usth.touristtracetracking.Foreground_service.LocationData;

public class GetHistoryResponse {
    private boolean success;
    private List<LocationData> data;

    public GetHistoryResponse(boolean success, List<LocationData> data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<LocationData> getData() {
        return data;
    }

    public void setData(List<LocationData> data) {
        this.data = data;
    }
}
