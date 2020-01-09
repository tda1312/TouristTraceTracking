package vn.edu.usth.touristtracetracking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import vn.edu.usth.touristtracetracking.Foreground_service.LocationData;
import vn.edu.usth.touristtracetracking.History.GetHistoryResponse;
import vn.edu.usth.touristtracetracking.Login.LoginData;
import vn.edu.usth.touristtracetracking.Login.LoginResponse;
import vn.edu.usth.touristtracetracking.Register.RegisterData;
import vn.edu.usth.touristtracetracking.Register.RegisterResult;
import vn.edu.usth.touristtracetracking.Update.UpdateResponse;

public interface Api {

    @Headers("Content-Type: application/json")
    @POST("api/users")
    Call<RegisterResult> createUser(
            @Body RegisterData body
    );

    @Headers("Content-Type: application/json")
    @POST("api/users/login")
    Call<LoginResponse> userLogin(
            @Body LoginData body
    );

    @Headers("Content-Type: application/json")
    @PUT("api/users/{id}")
    Call<UpdateResponse> updateUser(
            @Path("id") int id,
            @Header("Authorization") String auth,
            @Body User body
    );

    // WIP
    @Headers("Content-Type: application/json")
    @POST("api/users/{id}/history")
    Call<SendHistoryResponse> addHistory(
            @Path("id") int id,
            @Header("Authorization") String auth,
            @Body List<LocationData> body
    );

    @GET("api/users/{id}/history")
    Call<GetHistoryResponse> getHistory(
            @Path("id") int id,
            @Header("Authorization") String auth
    );
}
