package vn.edu.usth.touristtracetracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    TextView txt_change_ava;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Button btsave = findViewById(R.id.bt_save);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        TextView txt_change_ava = findViewById(R.id.change_ava);
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
            }});
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
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



