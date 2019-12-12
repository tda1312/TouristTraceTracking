package vn.edu.usth.touristtracetracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout;
    TextView txbyTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        constraintLayout = findViewById(R.id.Signup_constraint_layout);
        Button btSignup = (Button)findViewById(R.id.bt_Signup);
        btSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });




    }

}
