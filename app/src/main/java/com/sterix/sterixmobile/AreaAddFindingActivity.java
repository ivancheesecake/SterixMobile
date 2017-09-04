package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public String ip;

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

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        ip = sharedPref.getString("IP", "");

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

        String service_order_id,client_location_area_id,findings,recommendations,timestamp;

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        // Initialize Service Orders
        EditText findingsET = (EditText) findViewById(R.id.area_findings_notes);
        EditText recommendationsET = (EditText) findViewById(R.id.area_findings_recommendations);

        service_order_id = m.getService_order_id();
        client_location_area_id = m.getLocation_area_id();
        findings = findingsET.getText().toString();
        recommendations = recommendationsET.getText().toString();
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        values.put(SterixContract.AreaFinding.COLUMN_SERVICE_ORDER_ID,service_order_id);
        values.put(SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_AREA_ID,client_location_area_id);
        values.put(SterixContract.AreaFinding.COLUMN_IMAGE,mCurrentPhotoPath);
        values.put(SterixContract.AreaFinding.COLUMN_FINDINGS,findings);
        values.put(SterixContract.AreaFinding.COLUMN_RECOMMENDATIONS,recommendations);
        values.put(SterixContract.AreaFinding.COLUMN_TIMESTAMP,timestamp);

        database.insert(SterixContract.AreaFinding.TABLE_NAME, null, values);


        // Insert rows to database
        // Save photo to disk
        // Destroy activity

        //if Online, insert to local database and server

        if (isOnline()) {
            insertAreaMonitoringToServer(service_order_id, client_location_area_id,findings,recommendations,timestamp);
//            db.execSQL("delete from " + SterixContract.DeviceMonitoringPestQueue.TABLE_NAME + " where device_monitoring_id = "+currentDevice.get("id"));

        }
        else{

            database.insert(SterixContract.AreaFindingQueue.TABLE_NAME, null, values);
            // Store updates locally
//            insertDeviceMonitoringToQueue(service_order_id,currentDevice.get("device_code"),location_area_id,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),conditionId,activityId);
//            Log.d("INTERNETS?","WALANG INTERNET, PARANG PAG-ASA SA KANYA");

        }

        database.close();

        Toast t = Toast.makeText(getApplicationContext(),"Finding saved.",Toast.LENGTH_SHORT);
        t.show();


        // Add toast here
        finish();


    }

    public void insertAreaMonitoringToServer(String service_order_id, String client_location_area_ID,String findings,String proposed_action,String timestamp){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://"+ip+"/SterixBackend/insertAreaMonitoring.php";

//        Log.d("PEST_INFO",pestInfo.toString());

        HashMap params = new HashMap<String,String>();
        params.put("service_order_id",service_order_id);
        params.put("client_location_area_ID",client_location_area_ID);
        params.put("findings",findings);
        params.put("proposed_action",proposed_action);
        params.put("timestamp",timestamp);

//        params.put("pest_info",pestInfo.toString());


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.get("success").toString().equals("true")) {
                                Toast toast = Toast.makeText(getApplicationContext(),"Area monitoring data for area "+service_order_location+" was successfully updated in the server!", Toast.LENGTH_SHORT);
                                toast.show();

                            }
                            else{

                                Toast toast = Toast.makeText(getApplicationContext(),"Cannot connect to server.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        queue.add(request_json);

    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected void onSaveInstanceState(Bundle outState) {


        outState.putParcelable("image", imageBitmap);
        super.onSaveInstanceState(outState);
    }


}