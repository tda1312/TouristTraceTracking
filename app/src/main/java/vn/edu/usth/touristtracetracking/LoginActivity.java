package vn.edu.usth.touristtracetracking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout, constraint1;
    LinearLayout linearLayout;
    TextInputEditText edEmail, edPassword;
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
        setContentView(R.layout.activity_login);
        edEmail = (TextInputEditText)findViewById(R.id.ed_email);
        edPassword=(TextInputEditText)findViewById(R.id.ed_password);
        constraint1 =(ConstraintLayout) findViewById(R.id.layout1);
        handler.postDelayed(runnable,3000);

        Button btSignup = (Button)findViewById(R.id.bt_Signup);
        btSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        Button btSignin = (Button)findViewById(R.id.bt_Signin);
        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(LoginActivity.this,MapsActivity.class));
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
