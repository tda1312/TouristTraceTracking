package vn.edu.usth.touristtracetracking.Register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.touristtracetracking.LoginActivity;
import vn.edu.usth.touristtracetracking.R;
import vn.edu.usth.touristtracetracking.RetrofitHandler;

public class RegisterActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout;
    TextView txbyTime;
    private EditText etEmail, etPassword;
    private String response_message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        constraintLayout = findViewById(R.id.Signup_constraint_layout);

        final Button btSignup = (Button)findViewById(R.id.bt_Signup);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);

        btSignup.setEnabled(true);
        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

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

                Call<RegisterResult> call = RetrofitHandler
                        .getInstance()
                        .getApi()
                        .createUser(new RegisterData(username, password));
                call.enqueue(new Callback<RegisterResult>() {
                    @Override
                    public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
                        RegisterResult result = response.body();
                        if (result != null && result.getSuccess()) {
                            String s = response.body().getClient_id();
                            response_message += "You successfully registered with id " + s + " " + response.body().getSuccess();
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
                        Log.i("Fail", "fail");
                    }
                });


            }
        });




    }

}
