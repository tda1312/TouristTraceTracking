package vn.edu.usth.touristtracetracking.History;

public class SendHistoryResponse {
    private boolean success;

    public SendHistoryResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
