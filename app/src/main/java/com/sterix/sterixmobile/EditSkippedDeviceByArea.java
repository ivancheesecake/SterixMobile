package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class EditSkippedDeviceByArea extends AppCompatActivity {

    TextView barcodeInfo;
    String service_order_location,service_order_id,location_area_id,position,device_id,device_code;
    HashMap<String,String> activities;
    HashMap<String,String> conditions;
    HashMap<String,String> currentDevice;
    ArrayList<String> conditionsList;
    ArrayList<String> activitiesList;
    Spinner conditionsSpinner,activitySpinner;
    Monitoring m;
    public String ip;
    int device_queue_number;
    String uploaded;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_inaccessible_device);

        Intent i = getIntent();
        m =  i.getParcelableExtra("DEVICE_MONITORING_PARCEL");
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        service_order_id = i.getStringExtra("SERVICE_ORDER_ID");
        location_area_id = i.getStringExtra("CLIENT_LOCATION_AREA_ID");
        position = i.getStringExtra("POSITION");
        device_id = i.getStringExtra("DEVICE_ID");
        device_code = i.getStringExtra("DEVICE_CODE");

        Log.d("LOCATION_AREA_ID",location_area_id);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(service_order_location);
        setSupportActionBar(toolbar);

        // Fetch IP Address

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs", Context.MODE_PRIVATE);
        ip = sharedPref.getString("IP", "");
        device_queue_number = sharedPref.getInt("DEVICE_QUEUE_NUMBER", 0);
        uploaded = sharedPref.getString("DM_IMAGES_UPLOADED","");

        // Fetch data from previous activity

//        TextView tv_location = (TextView) findViewById(R.id.edit_inaccessible_location);
//        tv_location.setText(m.getLocation());

        // Fetch conditions (na dapat manggagaling sa previous activity)

        TextView inaccessibleDeviceCode = (TextView) findViewById(R.id.inaccessible_device_code);
        inaccessibleDeviceCode.setText(device_code);

        conditions = new HashMap<String,String>();
        conditions.put("Missing","5");
        conditions.put("Inaccessible","7");
        conditions.put("Blocked","8");
        conditions.put("Not Inspected","9");


        conditionsList = new ArrayList<>();
        conditionsList.add("Condition");

        for (String key:conditions.keySet()){
            conditionsList.add(key);
        }

        // Fetch activities (na dapat manggaling din sa previous activity

        activities = new HashMap<String,String>();
        activities.put("Installed","7");
        activities.put("Removed","8");
        activities.put("Reschedule","9");


        activitiesList = new ArrayList<>();
        activitiesList.add("Activity");

        for (String key:activities.keySet()){
            activitiesList.add(key);
        }

        // Setup spinners

        conditionsSpinner = (Spinner) findViewById(R.id.device_condition_inaccessible);
        activitySpinner = (Spinner) findViewById(R.id.device_activity_inaccessible);

        ArrayAdapter<String> conditionsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,conditionsList);
        conditionsSpinner.setAdapter(conditionsAdapter);

        ArrayAdapter<String> activityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,activitiesList);
        activitySpinner.setAdapter(activityAdapter);

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
                SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
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

            case R.id.menu_sync:

                Intent syncIntent = new Intent(this.getApplicationContext(), SyncActivity.class);
                startActivity(syncIntent);
                return true;

            case R.id.menu_open_concerns:

                Intent openConcernsIntent = new Intent(this.getApplicationContext(), OpenConcernsActivity.class);
                startActivity(openConcernsIntent);
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    public void cancelledt(View v){
        finish();
    }

    public void save(View v){

//        Log.d("NUMBA",v.getTag(R.id.device_position).toString());
//        HashMap editedDevice = DeviceMonitoringActivity.devices.get(Integer.parseInt(position));
//        Log.d("EDITED",editedDevice.toString());

        String condition = conditionsSpinner.getSelectedItem().toString();
        String activity = activitySpinner.getSelectedItem().toString();

        SQLiteDatabase db = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        // Sorry ang bobo ng pagka-code nito.
        // Inaantok lang talaga yung developer.
        // Pls understand. :(

        String device_condition_val,device_condition_id_val, activity_val,activity_id_val;

        if(condition.equals("Condition")) {

//            editedDevice.put("device_condition", "");
//            editedDevice.put("device_condition_id", "0");
            values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION,"");
            values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION_ID,"0");
            device_condition_val = "";
            device_condition_id_val = "0";


        }

        else{
//            editedDevice.put("device_condition", condition);
//            editedDevice.put("device_condition_id", conditions.get(condition));
            values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION,condition);
            values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION_ID,conditions.get(condition));

            device_condition_val = condition;
            device_condition_id_val = conditions.get(condition);
        }

        if(activity.equals("Activity")) {

//            editedDevice.put("activity", "");
//            editedDevice.put("activity_id", "0");
            values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY,"");
            values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY_ID,"0");

            activity_val = "";
            activity_id_val = "0";
        }
        else {
//            editedDevice.put("activity", activity);
//            editedDevice.put("activity_id", activities.get(activity));
            values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY,activity);
            values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY_ID,activities.get(activity));
            activity_val = activity;
            activity_id_val = activities.get(activity);
        }

//        Log.d("EDITED",editedDevice.toString());

//      Modify the local array
//        DeviceMonitoringActivity.devices.set(Integer.parseInt(position),editedDevice);
//        DeviceMonitoringSummaryActivity.mAdapter.notifyDataSetChanged();

//      Modify the database

        if(device_condition_val.equals("") || activity_val.equals("") ){
            Toast t = Toast.makeText(getApplicationContext(), "Condition and Activity are required.", Toast.LENGTH_SHORT);
            t.show();

        }

        else {

            db.update(SterixContract.DeviceMonitoring.TABLE_NAME,values,SterixContract.DeviceMonitoring._ID + " = ?",new String[]{device_id});

            if(isOnline()){

                //            int pos = Integer.parseInt(position);

                //            Log.d("TEST",m.getLocation_area_id());
                insertDeviceMonitoringToServer(service_order_id,device_code, location_area_id, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),device_condition_id_val,activity_id_val);
            }
            else{


                insertDeviceMonitoringToQueue(service_order_id,device_code, location_area_id, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),device_condition_id_val,activity_id_val);

            }


            ArrayList<HashMap<String,String>> devices = fetchSkippedDevices(service_order_id);
            SkippedDevicesByAreaExpandActivity.devices.clear();
            SkippedDevicesByAreaExpandActivity.devices.addAll(devices);
            SkippedDevicesByAreaExpandActivity.mAdapter.notifyDataSetChanged();

            finish();
        }

    }


    public ArrayList<HashMap<String,String>> fetchSkippedDevices(String service_order_id){

        ArrayList out = new ArrayList<HashMap<String,String>>();

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.DeviceMonitoring._ID,
                SterixContract.DeviceMonitoring.COLUMN_SERVICE_ORDER_ID,
                SterixContract.DeviceMonitoring.COLUMN_CLIENT_LOCATION_AREA_ID,
                SterixContract.DeviceMonitoring.COLUMN_DEVICE_CODE,
                SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION_ID,
                SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION,
                SterixContract.DeviceMonitoring.COLUMN_ACTIVITY_ID,
                SterixContract.DeviceMonitoring.COLUMN_ACTIVITY,
                SterixContract.DeviceMonitoring.COLUMN_IMAGE,
                SterixContract.DeviceMonitoring.COLUMN_NOTES,
                SterixContract.DeviceMonitoring.COLUMN_SCHEDULE,

        };

//        Log.d("LOCATION_AREA_ID",location_area_id);

        String selection = SterixContract.DeviceMonitoring.COLUMN_SERVICE_ORDER_ID +" = ? and "+ SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION +" = '' and "+ SterixContract.DeviceMonitoring.COLUMN_CLIENT_LOCATION_AREA_ID+" = ?";
        String selectionArgs[] = {service_order_id,location_area_id};
        String sortOrder = SterixContract.DeviceMonitoring._ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.DeviceMonitoring.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring._ID));
            String so_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_SERVICE_ORDER_ID));
            String loc_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_CLIENT_LOCATION_AREA_ID));
            String dcode = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CODE));
            String dc_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION_ID));
            String dc = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION));
            String a_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY_ID));
            String a = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_IMAGE));
            String notes = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_NOTES));
            String schedule = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_SCHEDULE));

            HashMap<String,String> temp = new HashMap<>();

            temp.put("id",id);
            temp.put("service_order_id",so_id);
            temp.put("client_location_area_id",loc_id);
            temp.put("device_code",dcode);
            temp.put("device_condition_id",dc_id);
            temp.put("device_condition",dc);
            temp.put("activity_id",a_id);
            temp.put("activity",a);
            temp.put("image",image);
            temp.put("notes",notes);

            if(schedule.contains(ServiceOrdersActivity.dayOfService)){
                temp.put("is_scheduled","true");
                out.add(temp);
            }

        }

        database.close();

        return out;
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void insertDeviceMonitoringToServer(String service_order_id, final String device_code, String client_location_area_ID, String timestamp, String device_condition_ID, String activity_ID){
//        JSONArray pestInfo = new JSONArray();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+ip+"/SterixBackend/insertDeviceMonitoring2.php";

        HashMap params = new HashMap<String,String>();
        params.put("service_order_id",service_order_id);
        params.put("device_code",device_code);
        params.put("client_location_area_ID",client_location_area_ID);
        params.put("timestamp",timestamp);
        params.put("device_condition_ID",device_condition_ID);
        params.put("activity_ID",activity_ID);
        params.put("pest_info", new JSONArray());
        params.put("photo_path", "");
        params.put("photo_notes","");

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("JWT", "");
        params.put("jwt",jwt);


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            if(response.get("success").toString().equals("true")) {
                                Toast toast = Toast.makeText(getApplicationContext(),"Device monitoring data for device "+device_code+" was successfully updated in the server!", Toast.LENGTH_SHORT);
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


    public void insertDeviceMonitoringToQueue(String service_order_id, final String device_code, String client_location_area_ID, String timestamp, String device_condition_ID, String activity_ID){

        Log.d("MAHIRAP","insertDeviceMonitoringToQueue");

        Log.d("MAHIRAP","device code "+device_code+" device condition id"+device_condition_ID);
        SQLiteDatabase db = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SterixContract.DeviceMonitoringQueue.COLUMN_SERVICE_ORDER_ID,service_order_id);
        values.put(SterixContract.DeviceMonitoringQueue.COLUMN_DEVICE_CODE,device_code);
        values.put(SterixContract.DeviceMonitoringQueue.COLUMN_CLIENT_LOCATION_AREA_ID,client_location_area_ID);
        values.put(SterixContract.DeviceMonitoringQueue.COLUMN_TIMESTAMP,timestamp);
        values.put(SterixContract.DeviceMonitoringQueue.COLUMN_DEVICE_CONDITION_ID,device_condition_ID);
        values.put(SterixContract.DeviceMonitoringQueue.COLUMN_ACTIVITY_ID,activity_ID);
        values.put(SterixContract.DeviceMonitoringQueue.COLUMN_IMAGE,"");
        values.put(SterixContract.DeviceMonitoringQueue.COLUMN_NOTES,"");
        values.put(SterixContract.DeviceMonitoringQueue.COLUMN_QUEUE_NUMBER,Integer.toString(device_queue_number));
        db.insert(SterixContract.DeviceMonitoringQueue.TABLE_NAME, null, values);

        device_queue_number++;

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("DEVICE_QUEUE_NUMBER",device_queue_number);
        editor.commit();

        Toast toast = Toast.makeText(getApplicationContext(),"Device monitoring data for device "+device_code+" was successfully updated!", Toast.LENGTH_SHORT);
        toast.show();

    }


}
