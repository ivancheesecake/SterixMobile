package com.sterix.sterixmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SkippedDevicesByAreaActivity extends AppCompatActivity {

    String service_order_location,service_order_id,location_area_id,position,device_id,device_code;
    RecyclerView recyclerView;
    public static ArrayList<HashMap<String,String>> devices;
    public static HashSet<String> areas;
    public static ArrayList<String> areas_list;
    public static RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    Monitoring m;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_monitoring_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent i = getIntent();
        service_order_id = i.getStringExtra("SERVICE_ORDER_ID");


        m =  i.getParcelableExtra("DEVICE_MONITORING_PARCEL");
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        service_order_id = i.getStringExtra("SERVICE_ORDER_ID");

        recyclerView = (RecyclerView) findViewById(R.id.summary_recyclerview);

        devices = fetchDevices(service_order_id);

        areas = new HashSet<String>();

        for(int j=0; j<devices.size(); j++){

            areas.add(devices.get(j).get("client_location_area_id"));

        }

        Log.d("AREASKIP",areas.toString());

        areas_list = new ArrayList<String>(areas);

        mAdapter = new SkippedDeviceAreaAdapter(areas_list);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(mAdapter);

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

            case R.id.menu_sync:

                Intent syncIntent = new Intent(this.getApplicationContext(), SyncActivity.class);
                startActivity(syncIntent);
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }


    public ArrayList<HashMap<String,String>> fetchDevices(String service_order_id){

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

        };

//        Log.d("LOCATION_AREA_ID",location_area_id);

        String selection = SterixContract.DeviceMonitoring.COLUMN_SERVICE_ORDER_ID +" = ? and "+ SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION +" = ''";
        String selectionArgs[] = {service_order_id};
        String sortOrder = SterixContract.DeviceMonitoring.COLUMN_CLIENT_LOCATION_AREA_ID +" ASC";

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

            out.add(temp);

        }

        database.close();

        return out;
    }

    public void viewSkippedDevicesInArea(View v){

        Log.d("HOY",v.getTag().toString());

        Intent deviceSummaryIntent = new Intent(getApplicationContext(), SkippedDevicesByAreaExpandActivity.class);
        deviceSummaryIntent.putExtra("SERVICE_ORDER_ID", service_order_id);
        deviceSummaryIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
        deviceSummaryIntent.putExtra("DEVICE_MONITORING_PARCEL", m);
        deviceSummaryIntent.putExtra("CLIENT_LOCATION_AREA_ID", v.getTag().toString());

        startActivity(deviceSummaryIntent);

    }




//    public void editDevice(View v){
//
//        Log.d("HERE","HERE");
//
//        Intent editInaccessibleDeviceIntent = new Intent(getApplicationContext(), EditSkippedDevice.class);
////        importDeviceIntent.putExtra("DEVICES", devices);
////        editInaccessibleDeviceIntent.putExtra("LOCATION_AREA", location_area);
//        editInaccessibleDeviceIntent.putExtra("SERVICE_ORDER_ID", service_order_id);
//        editInaccessibleDeviceIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
//        editInaccessibleDeviceIntent.putExtra("DEVICE_MONITORING_PARCEL", m);
//        editInaccessibleDeviceIntent.putExtra("POSITION", v.getTag(R.id.device_position)+"" );
//        editInaccessibleDeviceIntent.putExtra("DEVICE_ID", v.getTag(R.id.device_id)+"");
//        editInaccessibleDeviceIntent.putExtra("DEVICE_CODE", v.getTag(R.id.device_code)+"");
//        editInaccessibleDeviceIntent.putExtra("CLIENT_LOCATION_AREA_ID", v.getTag(R.id.client_location_area_id)+"");
//        startActivity(editInaccessibleDeviceIntent);
//
////        Log.d("TAG",v.getTag(R.id.device_position)+"");
////        Log.d("TAG",v.getTag(R.id.device_id)+"");
////        Log.d("TAG",v.getTag(R.id.device_code)+"");
//
//    }





}
