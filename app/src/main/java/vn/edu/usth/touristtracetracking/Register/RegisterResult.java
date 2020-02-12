package vn.edu.usth.touristtracetracking.Register;

import android.util.Log;

public class RegisterResult {
    private Boolean success;
    private String client_id;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
