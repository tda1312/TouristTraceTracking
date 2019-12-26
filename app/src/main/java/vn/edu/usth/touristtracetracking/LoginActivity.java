package vn.edu.usth.touristtracetracking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.touristtracetracking.Register.RegisterActivity;
import vn.edu.usth.touristtracetracking.storage.SharePrefManager;


public class LoginActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout, constraint1;
    LinearLayout linearLayout;
    TextView txbyTime;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            constraint1.setVisibility(View.VISIBLE);

        }
    };
    private Button btSignin;
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        constraint1 = findViewById(R.id.layout1);
        handler.postDelayed(runnable, 3000);

        Button btSignup = findViewById(R.id.bt_Signup);
        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        Button btSignin = findViewById(R.id.bt_Signin);
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        btSignin.setEnabled(true);
        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // check username and password is empty or not
                if (username.isEmpty()) {
                    etEmail.setError("Username is required!");
                    etEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }

                // call request to the server
                Call<LoginResponse> call = RetrofitHandler
                        .getInstance()
                        .getApi()
                        .userLogin(new LoginData(username, password));

                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        LoginResponse loginResponse = response.body();

                        if (loginResponse != null && loginResponse.isSuccess()) {
                            Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                            User current_user = loginResponse.getUser();
                            Log.i("ABCDE", current_user.getBirthday() + "hehe");


//                            User newUser = new User(12, "phuongminh here", "dinh", "23/03/99", "hehe","hehe","hehe","hehe","hehe");
                            // save user
                            SharePrefManager.getInstance(LoginActivity.this)
                                    .saveUser(current_user);




                            // open map
                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            String error = "Your username or password is not correct";
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {

                    }
                });
            }
        });

        // change view by time of a day
        txbyTime = findViewById(R.id.txbyTime);
        Calendar c = Calendar.getInstance();

        int timeofDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeofDay >= 0 && timeofDay < 12) {
            //moring

            txbyTime.setText("Good Morning");
        } else if (timeofDay >= 12 && timeofDay < 16) {
            //afternoon

            txbyTime.setText("Good Afternoon");
        } else if (timeofDay >= 16 && timeofDay < 21) {
            //evening
            txbyTime.setText("Good Evening");
        } else if (timeofDay >= 16 && timeofDay < 24) {
            //night

            txbyTime.setText("Good Night");
        }
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(SharePrefManager.getInstance(this).isLoggedIn()) {
//            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        }
//    }
}
