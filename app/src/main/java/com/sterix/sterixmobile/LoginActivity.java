package com.sterix.sterixmobile;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText et_username;
    EditText et_password;
    HashMap<String,String> params;
    private final int MY_PERMISSIONS_CAMERA = 1;
    private final int MY_PERMISSIONS_STORAGE = 2;
    private final int MY_PERMISSIONS_INTERNET = 3;
    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ASK PERMISSIONS

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_INTERNET);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_CAMERA);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_STORAGE);
            }
        }

        initializeActivitiesAndConditions();

        // Check if user is logged in
        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        boolean loggedIn = sharedPref.getBoolean("LOGGED_IN", false);
        String username = sharedPref.getString("USERNAME", "");
        String userId = sharedPref.getString("USER_ID", "");
        ip = sharedPref.getString("IP", "");

        Log.d("LOGGED_IN",Boolean.toString(loggedIn));
//        Log.d("IP",ip);
        // if logged in
        if(loggedIn){

//            syncData(userId);
            Log.d("HERE","HERE");
            Toast t = Toast.makeText(this, "Welcome back, "+username+"!",Toast.LENGTH_SHORT);
            t.show();
            Intent serviceOrdersIntent = new Intent(getApplicationContext(), ServiceOrdersActivity.class);
            startActivity(serviceOrdersIntent);
            finish();

        }

    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    return;
                }
            }

            case MY_PERMISSIONS_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    return;
                }
            }
        }
    }

    public void initializeActivitiesAndConditions(){

        // Make this online next time, bitch!

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values;

        // Initialize Device Conditions

        String count = "SELECT count(*) FROM "+SterixContract.DeviceActivity.TABLE_NAME;
        Cursor mcursor = database.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0)
            return;

        values = new ContentValues();
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_ID,"1");
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_NAME,"Damaged");
        database.insert(SterixContract.DeviceCondition.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_ID,"2");
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_NAME,"Needs Repair");
        database.insert(SterixContract.DeviceCondition.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_ID,"3");
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_NAME,"Label Needs Replacement");
        database.insert(SterixContract.DeviceCondition.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_ID,"4");
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_NAME,"Checklist Needs Replacement");
        database.insert(SterixContract.DeviceCondition.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_ID,"5");
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_NAME,"Missing");
        database.insert(SterixContract.DeviceCondition.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_ID,"6");
        values.put(SterixContract.DeviceCondition.COLUMN_CONDITION_NAME,"Good Working Condition");
        database.insert(SterixContract.DeviceCondition.TABLE_NAME, null, values);


        // Initialize Device Activity

        values = new ContentValues();
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_ID,"1");
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_NAME,"Cleaned");
        database.insert(SterixContract.DeviceActivity.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_ID,"2");
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_NAME,"Bait /Bulb Replaced");
        database.insert(SterixContract.DeviceActivity.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_ID,"3");
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_NAME,"Glue Trap / Board Replaced");
        database.insert(SterixContract.DeviceActivity.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_ID,"4");
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_NAME,"Device Replaced");
        database.insert(SterixContract.DeviceActivity.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_ID,"5");
        values.put(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_NAME,"Lure Replaced");
        database.insert(SterixContract.DeviceActivity.TABLE_NAME, null, values);

    }



    public void login(View b){


        final String username;
        String password;


        // Fetch login credentials

        et_username = (EditText) findViewById(R.id.login_username);
        et_password = (EditText) findViewById(R.id.login_password);
//        EditText et_ip = (EditText) findViewById(R.id.login_ip);

        username = et_username.getText().toString();
        password = et_password.getText().toString();

        ip = "www.sterix.online";

        params = new HashMap<String,String>();
        params.put("username",username);
        params.put("password",password);

        // Request a string response from the provided URL.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://"+ip+"/SterixBackend/login.php";
//        String url ="https://192.168.1.17/Sterix/login.php";

//        final TextView mTextView = (TextView) findViewById(R.id.credentials);


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.get("success").toString().equals("true")) {

                                Log.d("THERE","THERE");

                                SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putBoolean("LOGGED_IN", true);
                                editor.putString("USERNAME", username);
                                editor.putString("USER_ID", response.get("user_id").toString());
                                editor.putString("IP", ip);
                                editor.putInt("DEVICE_QUEUE_NUMBER",0);
                                editor.putString("DM_IMAGES_UPLOADED","");
                                editor.putString("AM_IMAGES_UPLOADED","");
                                editor.commit();

//                                syncData(response.get("user_id").toString());

                                Toast toast = Toast.makeText(getApplicationContext(),"Welcome, "+response.get("username").toString()+"!", Toast.LENGTH_SHORT);
                                toast.show();

                                Intent serviceOrdersIntent = new Intent(getApplicationContext(), ServiceOrdersActivity.class);
                                startActivity(serviceOrdersIntent);
                                finish();
                            }
                            else{
                                Log.d("OK?","OK.");
                                Toast toast = Toast.makeText(getApplicationContext(),"Invalid login credentials.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.e("Error: ", error.getMessage());
                Toast t = Toast.makeText(getApplicationContext(),"Can't connect to server.",Toast.LENGTH_SHORT);
                t.show();

            }
        });

        queue.add(request_json);

    }


}
