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
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class ImportDeviceActivity extends AppCompatActivity {

//    ArrayList<HashMap<String,String>> devices;
    String service_order_location,location_area,service_order_id,detected_device_code,location_id,user_id;
    Monitoring m;
    CameraSource cameraSource;
    SurfaceView cameraView;
    TextView barcodeInfo;
    Button deviceImport;
    Spinner deviceTypeSpinner;
    SortedMap<String,String> deviceTypes;
    ArrayList<String> deviceTypesList;




    public static String photoPath = "";
    public static String photoNotes = "";
    HashMap<String,String> currentDevice;


    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_device);

        Intent i = getIntent();
//        devices = (ArrayList<HashMap<String,String>>) i.getSerializableExtra("DEVICES");
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        location_area = i.getStringExtra("LOCATION_AREA");
        service_order_id = i.getStringExtra("SERVICE_ORDER_ID");
        m =  i.getParcelableExtra("DEVICE_MONITORING_PARCEL");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(service_order_location);
        setSupportActionBar(toolbar);

        // Fetch IP Address

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs", Context.MODE_PRIVATE);
        ip = sharedPref.getString("IP", "");
        location_id =  sharedPref.getString("LOCATION_ID","");
        user_id = sharedPref.getString("USER_ID","");

        // Initialize interface

        TextView tv_location = (TextView) findViewById(R.id.import_device_location);
        tv_location.setText(location_area);

        deviceImport = (Button) findViewById(R.id.device_import);
        deviceImport.setClickable(false);

        deviceTypeSpinner = (Spinner) findViewById(R.id.device_type);

        deviceTypes = fetchDeviceTypes();
        deviceTypesList = new ArrayList<>();
//        deviceTypesList.add("Device Type");

        for (String key:deviceTypes.keySet()){
            deviceTypesList.add(key);
        }

        ArrayAdapter<String> deviceTypesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,deviceTypesList);
        deviceTypeSpinner.setAdapter(deviceTypesAdapter);


        // Initialize QR Scanner

        cameraView = (SurfaceView)findViewById(R.id.import_camera_view);
        barcodeInfo = (TextView)findViewById(R.id.import_device_code);

        final BarcodeDetector barcodeDetector =  new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480).setAutoFocusEnabled(true).build();


        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {

//                            for(int a = 0; a< devices.size(); a++){
//
//                                Log.d("A", devices.get(a).get("device_code"));
//                                Log.d("B", barcodes.valueAt(0).displayValue);
////                                Log.d("C", barcodes.valueAt(0).displayValue.equals(devices.get(a).get("device_code")));
//
//
//                                if(devices.get(a).get("device_code").equals(barcodes.valueAt(0).displayValue.toString())){
//
//                                    Log.d("HERE", barcodes.valueAt(0).displayValue);
//
//                                    barcodeInfo.setText(    // Update the TextView
//                                            barcodes.valueAt(0).displayValue
//                                    );
//
//
//                                    cameraSource.stop();
//                                    barcodeDetector.release();
//                                    cameraView.setVisibility(View.GONE);
//
//                                    photoPath = "";
//                                    photoNotes = "";
//
//                                    // In the future, delete the photo!!!!
//
//                                    currentDevice = devices.get(a);
//                                    Toast toast = Toast.makeText(getApplicationContext(),"Device "+barcodes.valueAt(0).displayValue+" was detected!", Toast.LENGTH_SHORT);
//                                    toast.show();
////                                    deviceIsScanned();
////                                    enableForms();
//
//                                    break;
//                                }
//                                else{
//                                    Log.d("HUHU","HUHUHU");
//                                }
                                detected_device_code = barcodes.valueAt(0).displayValue;
                                barcodeInfo.setText(detected_device_code);
                                enableImport();


//                            }

                        }
                    });
                }

            }
        });





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

            case R.id.menu_open_concerns:

                Intent openConcernsIntent = new Intent(this.getApplicationContext(), OpenConcernsActivity.class);
                startActivity(openConcernsIntent);
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    public void rescanDevice(View v){

        Intent importDeviceIntent = new Intent(getApplicationContext(), ImportDeviceActivity.class);
//        importDeviceIntent.putExtra("DEVICES", devices);
//        importDeviceIntent.putExtra("LOCATION_AREA", location_area);
////        areaMonitoringIntent.putExtra("SERVICE_ORDER_ID", m.getService_order_id());
//        importDeviceIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);


//        importDeviceIntent.putExtra("DEVICES", devices);
        importDeviceIntent.putExtra("LOCATION_AREA", m.getLocation());
        importDeviceIntent.putExtra("SERVICE_ORDER_ID", m.getService_order_id());
        importDeviceIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
        importDeviceIntent.putExtra("DEVICE_MONITORING_PARCEL", m);
        startActivity(importDeviceIntent);



        // Finish current instance
        finish();

    }

    public void backToDeviceSummary(View v){

        finish();
    }

    public void enableImport(){


        deviceImport.setClickable(true);
        deviceImport.setBackgroundResource(R.drawable.curved_borders_blue);

    }

    public SortedMap<String,String> fetchDeviceTypes(){

        HashMap out = new HashMap<String,String>();

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.DeviceType.COLUMN_DEVICE_TYPE_ID,
                SterixContract.DeviceType.COLUMN_DEVICE_TYPE_NAME
        };

        String sortOrder = SterixContract.DeviceType.COLUMN_DEVICE_TYPE_ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.DeviceType.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clausedd
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceType.COLUMN_DEVICE_TYPE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceType.COLUMN_DEVICE_TYPE_NAME));

            out.put(name,id);

        }

        database.close();

        SortedMap<String,String> out2 = new TreeMap<String,String>(out);
        return out2;

    }



    public void importDevice(View v){

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String [] tokens = timestamp.split(" ");

        SQLiteDatabase db = new SterixDBHelper(this).getWritableDatabase();

//      Original code as of 02/09/2019

//        ContentValues values = new ContentValues();
//        values.put(SterixContract.DeviceMonitoring.COLUMN_SERVICE_ORDER_ID,service_order_id);
//        values.put(SterixContract.DeviceMonitoring.COLUMN_CLIENT_LOCATION_AREA_ID,m.getLocation_area_id());
//        values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CODE,detected_device_code);
//        values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION_ID,0);
//        values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION,"");
//        values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY_ID,7);
//        values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY,"Installed");
//        values.put(SterixContract.DeviceMonitoring.COLUMN_IMAGE,"");
//        values.put(SterixContract.DeviceMonitoring.COLUMN_NOTES,"");
//        values.put(SterixContract.DeviceMonitoring.COLUMN_TIMESTAMP,timestamp);
//
//        long lastid = db.insert(SterixContract.DeviceMonitoring.TABLE_NAME, null, values);
//
//        HashMap<String,String> temp = new HashMap<>();
//
//        temp.put("id",Long.toString(lastid));
//        temp.put("service_order_id",service_order_id);
//        temp.put("client_location_area_id",m.getLocation_area_id());
//        temp.put("device_code",detected_device_code);
//        temp.put("device_condition_id","0");
//        temp.put("device_condition","");
//        temp.put("activity_id","7");
//        temp.put("activity","Installed");
//        temp.put("image","");
//        temp.put("notes","");

//        DeviceMonitoringActivity.devices.add(temp);

//      New code as of 02/09/2019
//      Under the assumption that device codes are unique

        ContentValues values = new ContentValues();

        values.put(SterixContract.DeviceMonitoring.COLUMN_CLIENT_LOCATION_AREA_ID,m.getLocation_area_id());

        db.update(SterixContract.DeviceMonitoring.TABLE_NAME, values, SterixContract.DeviceMonitoring.COLUMN_DEVICE_CODE + " = ?", new String[]{detected_device_code});

//        DeviceMonitoringActivity.devices = DeviceMonitoringActivity.fetchDevices(m.getService_order_id(),m.getLocation_area_id());

//        for(int i=0; i<DeviceMonitoringActivity.devices.size();i++){
//
//            currentDevice = DeviceMonitoringActivity.devices.get(i);
//
//            Log.d("DEVICE_CODE",Integer.toString(DeviceMonitoringActivity.devices.size()));
//            Log.d("DEVICE_CODE",detected_device_code);
//            Log.d("DEVICE_CODE",DeviceMonitoringActivity.devices.get(i).get("device_code"));
//
//            if(DeviceMonitoringActivity.devices.get(i).get("device_code").equals(detected_device_code)){
//                Log.d("MATCH","MATCH");
//                currentDevice.put("client_location_area_id",m.getLocation_area_id());
//                DeviceMonitoringActivity.devices.set(i,currentDevice);
//                break;
//            }
//        }


        String deviceType = deviceTypeSpinner.getSelectedItem().toString();

        if(isOnline()){

            HashMap params = new HashMap<String,String>();
            params.put("device_type_id",deviceTypes.get(deviceType));
            params.put("device_code",detected_device_code);
            params.put("client_location_area_id",m.getLocation_area_id());
            params.put("client_location_id",location_id);
            params.put("date_deployed",tokens[0]);
            params.put("time_deployed",tokens[1]);
            params.put("user_id",user_id);
            params.put("timestamp",timestamp);
            params.put("service_order_id",service_order_id);

            //      JWT Code, COPY THIS EVERYWHERE!!!!!

            SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
            String jwt = sharedPref.getString("JWT", "");
            params.put("jwt",jwt);



            Log.d("PARAMS",params.toString());

            // Request a string response from the provided URL.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="http://"+ip+"/SterixBackend/importDevice.php";

            JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    try {

                        if(response.get("success").toString().equals("true")) {

                            Toast toast = Toast.makeText(getApplicationContext(),"Device successfully imported to area.", Toast.LENGTH_SHORT);
                            toast.show();
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

        }else {

//            Insert to Queue


            ContentValues values2 = new ContentValues();
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_SERVICE_ORDER_ID,service_order_id);
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_DEVICE_TYPE_ID,deviceTypes.get(deviceType));
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_DEVICE_CODE,detected_device_code);
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_CLIENT_LOCATION_AREA_ID,m.getLocation_area_id());
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_CLIENT_LOCATION_ID,location_id);
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_DATE_DEPLOYED,tokens[0]);
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_TIME_DEPLOYED,tokens[1]);
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_DEVICE_CONDITION_ID,0);
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_ACTIVITY_ID,7);
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_TIMESTAMP,timestamp);
            values2.put(SterixContract.InstalledDeviceQueue.COLUMN_USER_ID,user_id);

            db.insert(SterixContract.InstalledDeviceQueue.TABLE_NAME, null, values2);

            Toast toast = Toast.makeText(getApplicationContext(), "Device successfully imported.", Toast.LENGTH_SHORT);
            toast.show();

        }

        db.close();

//        DeviceMonitoringSummaryActivity.recyclerView.setAdapter(DeviceMonitoringSummaryActivity.mAdapter);
        finish();


        Log.d("FINISHED","FINISHED");
        DeviceMonitoringActivity.devices = fetchDevices(m.getService_order_id(),m.getLocation_area_id());
//        DeviceMonitoringSummaryActivity.mAdapter.notifyDataSetChanged();

        DeviceMonitoringSummaryActivity.mAdapter = new DeviceSummaryAdapter(DeviceMonitoringActivity.devices);

        DeviceMonitoringSummaryActivity.recyclerView.setAdapter(DeviceMonitoringSummaryActivity.mAdapter);



//        Intent deviceSummaryIntent = new Intent(getApplicationContext(), DeviceMonitoringSummaryActivity.class);
////        deviceSummaryIntent.putExtra("DEVICES", devices);
//        deviceSummaryIntent.putExtra("LOCATION_AREA", m.getLocation());
//        deviceSummaryIntent.putExtra("SERVICE_ORDER_ID", m.getService_order_id());
//        deviceSummaryIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
//        deviceSummaryIntent.putExtra("DEVICE_MONITORING_PARCEL", m);
//        startActivity(deviceSummaryIntent);



    }



    public ArrayList <HashMap<String,String>> fetchDevices(String service_order_id, String location_area_id){

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

        Log.d("LOCATION_AREA_ID",location_area_id);

        String selection = SterixContract.DeviceMonitoring.COLUMN_SERVICE_ORDER_ID +" = ? and "+ SterixContract.DeviceMonitoring.COLUMN_CLIENT_LOCATION_AREA_ID +" = ?";
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
            temp.put("schedule",schedule);

            if(schedule.contains(ServiceOrdersActivity.dayOfService))
                temp.put("is_scheduled","true");
            else
                temp.put("is_scheduled","false");

            out.add(temp);

        }

        database.close();

        return out;
    }




    public void sendAlert(View v){

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        final String location_id = sharedPref.getString("LOCATION_ID", "");

        Log.d("LOCATION_ID",location_id);

        new MaterialDialog.Builder(this)
                .title(R.string.alert_title)
                .content(R.string.alert_content)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES  | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .input(R.string.alert_hint, R.string.blank, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
//                        alertMessage = input.toString();
                        Log.d("HEY","An alert was sent. "+input);

                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url ="http://"+ip+"/SterixBackend/sendAlert.php";

                        HashMap params = new HashMap<String,String>();
                        params.put("alert",input.toString());
                        params.put("client_location_ID",location_id);

                        //      JWT Code, COPY THIS EVERYWHERE!!!!!

                        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
                        String jwt = sharedPref.getString("JWT", "");
                        params.put("jwt",jwt);


                        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {

                                            if(response.get("success").toString().equals("true")) {
                                                Toast toast = Toast.makeText(getApplicationContext(),"Alert sent", Toast.LENGTH_SHORT);
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
                })
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
