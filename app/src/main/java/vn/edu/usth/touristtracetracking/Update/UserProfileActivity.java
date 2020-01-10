package vn.edu.usth.touristtracetracking.Update;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.touristtracetracking.R;
import vn.edu.usth.touristtracetracking.RetrofitHandler;
import vn.edu.usth.touristtracetracking.User;
import vn.edu.usth.touristtracetracking.storage.SharePrefManager;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener{
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
        etFName = (findViewById(R.id.fName));
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

        // handle update button
        btsave.setOnClickListener(this);

        // Get photo from gallery
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
    public void onClick(View v) {
        // Get text from the form
        String fisrtname = etFName.getText().toString().trim();
        String lastName = etLName.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String nationality = etNationality.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String city = etCity.getText().toString().trim();

        // Get SharePref
        SharePrefManager sharePrefManager = SharePrefManager.getInstance(UserProfileActivity.this);
        User user = sharePrefManager.getUser();

        // Make and handle req + res
        Call<UpdateResponse> call = RetrofitHandler.getInstance()
                .getApi().updateUser(user.getId(), "Bearer " + sharePrefManager.getToken(), new User(user.getId(), fisrtname, lastName, birthday, phone, nationality, email, country, city));
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                UpdateResponse updateResponse = response.body();
                if (updateResponse != null && updateResponse.isSuccess()) {
                    // display result to user
                    Toast.makeText(UserProfileActivity.this, updateResponse.getMessage(), Toast.LENGTH_LONG).show();
                    // save new info to shareRef
                    sharePrefManager.saveUser(response.body().getUser());
                } else {
                    Toast.makeText(UserProfileActivity.this, "No, you failed!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                String error = "Failed to update!";
                Toast.makeText(UserProfileActivity.this, error, Toast.LENGTH_LONG).show();
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



