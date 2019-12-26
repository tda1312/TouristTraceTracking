package vn.edu.usth.touristtracetracking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.touristtracetracking.storage.SharePrefManager;

public class UserProfileActivity extends AppCompatActivity {
    TextView txt_change_ava;
    CircleImageView circleImageView;
    EditText etFName, etLName, etBirthday, etPhone, etNationality, etEmail, etCountry, etCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Button btsave = findViewById(R.id.bt_save);
        circleImageView = findViewById(R.id.profile_image);
        TextView txt_change_ava = findViewById(R.id.change_ava);

        // get user's personal info from SharedPref
        User user = SharePrefManager.getInstance(this).getUser();

        // display to user profile
        etFName = findViewById(R.id.fName);
        etLName = findViewById(R.id.lName);
        etBirthday = findViewById(R.id.birthday);
        etPhone = findViewById(R.id.phone);
        etNationality = findViewById(R.id.nationality);
        etEmail = findViewById(R.id.email);
        etCountry = findViewById(R.id.country);
        etCity = findViewById(R.id.city);

        etFName.setText(user.getFirstName());
        etLName.setText(user.getLastName());
        etBirthday.setText(user.getBirthday());
        etPhone.setText(user.getPhone());
        etNationality.setText(user.getNationality());
        etEmail.setText(user.getEmail());
        etCountry.setText(user.getCountry());
        etCity.setText(user.getCity());

        btsave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(UserProfileActivity.this, MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }

        });

        // Get photo from glarry
        txt_change_ava.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                circleImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}



