package vn.edu.usth.touristtracetracking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import vn.edu.usth.touristtracetracking.Register.RegisterData;
import vn.edu.usth.touristtracetracking.Register.RegisterResult;

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
}
