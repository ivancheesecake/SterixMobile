package com.sterix.sterixmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
//
//            case R.id.action_favorite:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
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


