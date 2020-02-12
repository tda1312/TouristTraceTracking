package vn.edu.usth.touristtracetracking.Register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.touristtracetracking.Login.LoginActivity;
import vn.edu.usth.touristtracetracking.R;
import vn.edu.usth.touristtracetracking.RetrofitHandler;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    ConstraintLayout constraintLayout;
    private EditText etEmail, etPassword;
    private Button btnSignUp;
    private String response_message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        constraintLayout = findViewById(R.id.Signup_constraint_layout);

        btnSignUp = findViewById(R.id.bt_Signup);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignUp.setEnabled(true);

        // set action for register button
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!isEmpty(username, password)) {
            makeRegisterRequest(username, password);
        }
    }

    public boolean isEmpty(String username, String password) {
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

    private void makeRegisterRequest(String username, String password) {
        Call<RegisterResult> call = RetrofitHandler
                .getInstance()
                .getApi()
                .createUser(new RegisterData(username, password));
        call.enqueue(new Callback<RegisterResult>() {
            @Override
            public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
                RegisterResult result = response.body();
                if (result != null && result.getSuccess()) {
                    response_message += "You successfully registered a new account!";
                    Toast.makeText(getApplicationContext(), response_message, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    String error = "Your username is already taken! Please choose other username";
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResult> call, Throwable t) {
                String failText = "You need to turn on your internet connection";
                Toast.makeText(RegisterActivity.this, "Failed to create new account", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
