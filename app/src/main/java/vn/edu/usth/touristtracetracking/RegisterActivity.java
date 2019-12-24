package vn.edu.usth.touristtracetracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout;
    TextView txbyTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        constraintLayout = findViewById(R.id.Signup_constraint_layout);
        final Button btSignup = (Button)findViewById(R.id.bt_Signup);
        final EditText etEmail = (EditText) findViewById(R.id.et_email);
        final EditText etPassword = (EditText) findViewById(R.id.et_password);
        btSignup.setEnabled(true);
        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);

                if (!email.isEmpty() && !password.isEmpty()) {
                    startActivity(intent);
                }

                else {
                    Toast.makeText(getApplicationContext(), "Please fill the blank for sign up ", Toast.LENGTH_LONG).show();
                }

            }
        });




    }

}
