package com.sterix.sterixmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceMonitoringPhotoActivity extends AppCompatActivity {

    String service_order_location,location_area;
    public static Uri kwonURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_monitoring_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();

        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        toolbar.setSubtitle(service_order_location);

        location_area = i.getStringExtra("LOCATION_AREA");
        TextView tv_location = (TextView) findViewById(R.id.device_monitoring_photo_location);
        tv_location.setText(location_area);

        ImageView image = (ImageView) findViewById(R.id.device_monitoring_photo_row2);
        image.setVisibility(View.GONE);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                Log.d("LOGOUT","BITCH!");

                // Remove all sharedpreferences
                SharedPreferences sharedPref = getSharedPreferences("sterix_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();

                // Destroy all previous activities and go back to login screen
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                Toast t = Toast.makeText(getApplicationContext(),"Successfully logged out!",Toast.LENGTH_SHORT);
                t.show();

                // Destroy current activity
                finish();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                kwonURI = FileProvider.getUriForFile(this,
                        "com.sterix.sterixmobile.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, kwonURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            ImageView thumb = (ImageView) findViewById(R.id.device_monitoring_photo_row2);
//            extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");

//            Bitmap thumbBitmap = new Bitmap();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), kwonURI);
                //Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bitmap, 900, 500);
                thumb.setImageBitmap(bitmap);
                thumb.setVisibility(View.VISIBLE);
            }
            catch(IOException e){}

            Button b = (Button) findViewById(R.id.device_monitoring_photo_row3);
            b.setText("Change Photo");
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void saveDevicePhoto(View v){

        EditText et = (EditText) findViewById(R.id.device_notes);

        DeviceMonitoringActivity.photoPath = mCurrentPhotoPath;
        DeviceMonitoringActivity.photoNotes = et.getText().toString();
        DeviceMonitoringActivity.addPhotosNotes.setText("Change Photo/Notes");
        finish();

    }

}


