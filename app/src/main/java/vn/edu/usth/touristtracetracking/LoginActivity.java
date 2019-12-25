package vn.edu.usth.touristtracetracking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import vn.edu.usth.touristtracetracking.Register.RegisterActivity;


public class LoginActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout, constraint1;
    LinearLayout linearLayout;
    TextView txbyTime;
    Handler handler= new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            constraint1.setVisibility(View.VISIBLE);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        constraint1 =(ConstraintLayout) findViewById(R.id.layout1);
        handler.postDelayed(runnable,3000);

        Button btSignup = (Button)findViewById(R.id.bt_Signup);
        btSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        final Button btSignin = (Button)findViewById(R.id.bt_Signin);
        final EditText etEmail = (EditText) findViewById(R.id.et_email);
        final EditText etPassword = (EditText) findViewById(R.id.et_password);
        btSignin.setEnabled(true);
        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);

                if (!email.isEmpty() && !password.isEmpty()) {
                    startActivity(intent);
                }

                else {
                    Toast.makeText(getApplicationContext(), "Please fill the blank for log in ", Toast.LENGTH_LONG).show();
                }

            }
        });




        // change view by time of a day
        txbyTime = findViewById(R.id.txbyTime);
        Calendar c = Calendar.getInstance();

        int timeofDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeofDay >=0 && timeofDay <12){
            //moring

            txbyTime.setText("Good Morning");
        }
        else if(timeofDay>=12 && timeofDay<16){
            //afternoon

            txbyTime.setText("Good Afternoon");
        }
        else if(timeofDay>=16 && timeofDay<21){
            //evening
            txbyTime.setText("Good Evening");
        }
        else if(timeofDay>=16 && timeofDay<24){
            //night

            txbyTime.setText("Good Night");
        }
    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }




}
