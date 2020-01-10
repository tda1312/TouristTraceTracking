package vn.edu.usth.touristtracetracking.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.touristtracetracking.ForegroundService.MapsActivity;
import vn.edu.usth.touristtracetracking.R;
import vn.edu.usth.touristtracetracking.Register.RegisterActivity;
import vn.edu.usth.touristtracetracking.RetrofitHandler;
import vn.edu.usth.touristtracetracking.User;
import vn.edu.usth.touristtracetracking.storage.SharePrefManager;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int TIME_INTERVAL = 2000;
    ConstraintLayout constraintLayout, constraint1;
    TextView welcomeText;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            constraint1.setVisibility(View.VISIBLE);
        }
    };
    private Button btSignIn, btSignUp;
    private EditText etEmail, etPassword;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        constraint1 = findViewById(R.id.layout1);
        btSignIn = findViewById(R.id.bt_Signin);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btSignUp = findViewById(R.id.bt_Signup);
        welcomeText = findViewById(R.id.txbyTime);

        handler.postDelayed(runnable, 3000);

        // Sign up button handler
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Sign in button handler
        btSignIn.setEnabled(true);
        btSignIn.setOnClickListener(this);

        // Welcome text handler
        changeWelcomeText();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void changeWelcomeText() {
        // change view by time of a day
        Calendar c = Calendar.getInstance();

        int timeofDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeofDay >= 0 && timeofDay < 12) {
            //moring
            welcomeText.setText("Good Morning");
        } else if (timeofDay >= 12 && timeofDay < 16) {
            //afternoon
            welcomeText.setText("Good Afternoon");
        } else if (timeofDay >= 16 && timeofDay < 21) {
            //evening
            welcomeText.setText("Good Evening");
        } else if (timeofDay >= 16 && timeofDay < 24) {
            //night
            welcomeText.setText("Good Night");
        }
    }

    @Override
    public void onClick(View v) {
        String username = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (!isEmpty(username, password)) {
            makeLoginRequest(username, password);
        }
    }

    private void makeLoginRequest(String username, String password) {
        SharePrefManager sharePrefInstance = SharePrefManager.getInstance(LoginActivity.this);

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

                    // save user's info and token to ShareRef
                    User current_user = loginResponse.getUser();
                    String token = loginResponse.getToken();
                    sharePrefInstance.saveUser(current_user);
                    sharePrefInstance.saveToken(token);

                    // take user into the app
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
                Toast.makeText(LoginActivity.this, "Please turn on your connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isEmpty(String username, String password) {
        if (username.isEmpty()) {
            etEmail.setError("Username is required!");
            etEmail.requestFocus();
            return true;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
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
