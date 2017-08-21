package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AreaAddFindingActivity extends AppCompatActivity {

    Bitmap imageBitmap;
    Bundle extras;
    String service_order_location;
    Monitoring m;
    Uri kwonURI;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_area_add_finding);

        Intent i = getIntent();
        m = i.getParcelableExtra("AREA_MONITORING_PARCEL");
        Log.d("HEYYYY",m.getId());
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TextView area = (TextView) findViewById(R.id.area_add_finding_location);
        area.setText(m.getLocation());


        ImageView thumb = (ImageView) findViewById(R.id.area_add_finding_row2);

        if(savedInstanceState != null) {


            Bitmap bitmap = savedInstanceState.getParcelable("image");
            if(bitmap!=null)
                thumb.setImageBitmap(bitmap);

        }

        thumb.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(service_order_location);


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

            ImageView thumb = (ImageView) findViewById(R.id.area_add_finding_row2);
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




            Button b = (Button) findViewById(R.id.area_add_finding_row3);
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


    public void saveFinding(View v){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        // Initialize Service Orders

        EditText findingsET = (EditText) findViewById(R.id.area_findings_notes);
        EditText recommendationsET = (EditText) findViewById(R.id.area_findings_recommendations);

        values.put(SterixContract.AreaFinding.COLUMN_SERVICE_ORDER_ID,m.getService_order_id());
        values.put(SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_AREA_ID,m.getLocation_area_id());
        values.put(SterixContract.AreaFinding.COLUMN_IMAGE,mCurrentPhotoPath);
        values.put(SterixContract.AreaFinding.COLUMN_FINDINGS,findingsET.getText().toString());
        values.put(SterixContract.AreaFinding.COLUMN_RECOMMENDATIONS,recommendationsET.getText().toString());
        values.put(SterixContract.AreaFinding.COLUMN_TIMESTAMP,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        database.insert(SterixContract.AreaFinding.TABLE_NAME, null, values);

        // Insert rows to database
        // Save photo to disk
        // Destroy activity

        Toast t = Toast.makeText(getApplicationContext(),"Finding saved.",Toast.LENGTH_SHORT);
        t.show();


        // Add toast here
        finish();


    }

    protected void onSaveInstanceState(Bundle outState) {


        outState.putParcelable("image", imageBitmap);
        super.onSaveInstanceState(outState);
    }


}