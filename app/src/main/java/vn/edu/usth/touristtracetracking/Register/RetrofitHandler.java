package vn.edu.usth.touristtracetracking.Register;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import vn.edu.usth.touristtracetracking.Api;

public class RetrofitHandler {
    private static final String BASE_URL = "http://ec2-52-221-183-90.ap-southeast-1.compute.amazonaws.com:443/api/users";
    private static RetrofitHandler mInstance;
    private Retrofit retrofit;

    private RetrofitHandler() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitHandler getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitHandler();
        }
        return mInstance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
}
