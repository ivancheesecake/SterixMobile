package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DeviceMonitoringActivity extends AppCompatActivity {

    public static Button addPhotosNotes;
    Button condition,action,ptl,viewSummary,viewPhotosNotes,save;
    EditText ar,gr,ant,mos,hf,bf,df,ff,nr,cb,rr,liz,sp,bi,pf,fm,hm,oth,rfb;

    CameraSource cameraSource;
    SurfaceView cameraView;
    TextView barcodeInfo;
    String service_order_location,service_order_id,location_area_id;
    ArrayList<HashMap<String,String>> devices;
    HashMap<String,String> activities;
    HashMap<String,String> conditions;
    HashMap<String,String> currentDevice;
    ArrayList<String> conditionsList;
    ArrayList<String> activitiesList;
    Spinner conditionsSpinner,activitySpinner;
    Monitoring m;



    public static String photoPath = "";
    public static String photoNotes = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_monitoring);

        Intent i = getIntent();
        m =  i.getParcelableExtra("DEVICE_MONITORING_PARCEL");
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        service_order_id = i.getStringExtra("SERVICE_ORDER_ID");
        location_area_id = i.getStringExtra("LOCATION_AREA_ID");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(service_order_location);
        setSupportActionBar(toolbar);

        // Fetch data from previous activity

        TextView tv_location = (TextView) findViewById(R.id.device_monitoring_location);
        tv_location.setText(m.getLocation());

        // Fetch conditions (na dapat manggagaling sa previous activity)

        conditions = fetchConditions();
        conditionsList = new ArrayList<>();
        conditionsList.add("Condition");

        for (String key:conditions.keySet()){
            conditionsList.add(key);
        }

        // Fetch activities (na dapat manggaling din sa previous activity

        activities = fetchActivities();
        activitiesList = new ArrayList<>();
        activitiesList.add("Activity");

        for (String key:activities.keySet()){
            activitiesList.add(key);
        }

        // Setup spinners
        
        conditionsSpinner = (Spinner) findViewById(R.id.device_condition);
        activitySpinner = (Spinner) findViewById(R.id.device_activity);

        ArrayAdapter<String> conditionsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,conditionsList);
        conditionsSpinner.setAdapter(conditionsAdapter);

        ArrayAdapter<String> activityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,activitiesList);
        activitySpinner.setAdapter(activityAdapter);

        // Fetch all devices

        devices = fetchDevices(service_order_id,location_area_id);

        for(int j=0; j<devices.size();j++){

            Log.d("DEVICE",devices.get(j).get("device_code"));

        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Disable everything at the beginning

        //conditionsSpinner = (Spinner) findViewById(R.id.device_condition);
        conditionsSpinner.setEnabled(false);
        conditionsSpinner.setClickable(false);

        addPhotosNotes = (Button) findViewById(R.id.device_add_photo);
        addPhotosNotes.setClickable(false);

        //activitySpinner = (Spinner) findViewById(R.id.device_activity);
        activitySpinner.setEnabled(false);
        activitySpinner.setClickable(false);

        ptl = (Button) findViewById(R.id.device_ptl);
        ptl.setClickable(false);

        ar = (EditText) findViewById(R.id.device_ar);
        ar.setClickable(false);

        gr = (EditText) findViewById(R.id.device_gr);
        gr.setClickable(false);

        ant = (EditText) findViewById(R.id.device_ant);
        ant.setClickable(false);

        mos = (EditText) findViewById(R.id.device_mos);
        mos.setClickable(false);

        hf = (EditText) findViewById(R.id.device_hf);
        hf.setClickable(false);

        bf = (EditText) findViewById(R.id.device_bf);
        bf.setClickable(false);

        df = (EditText) findViewById(R.id.device_df);
        df.setClickable(false);

        pf = (EditText) findViewById(R.id.device_pf);
        pf.setClickable(false);

        ff = (EditText) findViewById(R.id.device_ff);
        ff.setClickable(false);

        fm = (EditText) findViewById(R.id.device_fm);
        fm.setClickable(false);

        hm = (EditText) findViewById(R.id.device_hm);
        hm.setClickable(false);

        rr = (EditText) findViewById(R.id.device_rr);
        rr.setClickable(false);

        nr = (EditText) findViewById(R.id.device_nr);
        nr.setClickable(false);

        cb = (EditText) findViewById(R.id.device_cb);
        cb.setClickable(false);

        rfb = (EditText) findViewById(R.id.device_rfb);
        rfb.setClickable(false);

        liz = (EditText) findViewById(R.id.device_liz);
        liz.setClickable(false);

        sp = (EditText) findViewById(R.id.device_sp);
        sp.setClickable(false);

        bi = (EditText) findViewById(R.id.device_bi);
        bi.setClickable(false);

        oth = (EditText) findViewById(R.id.device_oth);
        oth.setClickable(false);

        viewPhotosNotes = (Button) findViewById(R.id.device_button_view_photos_notes);
        viewPhotosNotes.setClickable(false);

        save = (Button) findViewById(R.id.device_save);
        save.setClickable(false);

        // Initialize QR Scanner

        cameraView = (SurfaceView)findViewById(R.id.camera_view);
        barcodeInfo = (TextView)findViewById(R.id.device_code);

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




                            //Log.d("CODE", Boolean.toString(barcodes.valueAt(0).displayValue.toString().equals("a")) );

//                            if(barcodes.valueAt(0).displayValue.toString().equals("a")) {
//
//                                barcodeInfo.setText(    // Update the TextView
//                                        barcodes.valueAt(0).displayValue
//                                );
//
//
//                                cameraSource.stop();
//                                barcodeDetector.release();
//                                cameraView.setVisibility(View.GONE);
//
//
//                                Toast toast = Toast.makeText(getApplicationContext(), "Device " + barcodes.valueAt(0).displayValue + " was detected!", Toast.LENGTH_SHORT);
//                                toast.show();
//                                enableForms();
//                            }
//                            else{
//
//                                Log.d("WHY","WHY?");
//                            }

                            for(int a = 0; a< devices.size(); a++){

                                Log.d("A", devices.get(a).get("device_code"));
                                Log.d("B", barcodes.valueAt(0).displayValue);
//                                Log.d("C", barcodes.valueAt(0).displayValue.equals(devices.get(a).get("device_code")));


                                if(devices.get(a).get("device_code").equals(barcodes.valueAt(0).displayValue.toString())){

                                    Log.d("HERE", barcodes.valueAt(0).displayValue);

                                    barcodeInfo.setText(    // Update the TextView
                                            barcodes.valueAt(0).displayValue
                                    );


                                    cameraSource.stop();
                                    barcodeDetector.release();
                                    cameraView.setVisibility(View.GONE);

                                    photoPath = "";
                                    photoNotes = "";

                                    // In the future, delete the photo!!!!

                                    currentDevice = devices.get(a);
                                    Toast toast = Toast.makeText(getApplicationContext(),"Device "+barcodes.valueAt(0).displayValue+" was detected!", Toast.LENGTH_SHORT);
                                    toast.show();
                                    enableForms();

                                    break;
                                }
                                else{
                                    Log.d("HUHU","HUHUHU");
                                }

                            }

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

    public void enableForms(){
        Log.d("HEY","HEYY");

        conditionsSpinner.setEnabled(true);
        conditionsSpinner.setClickable(true);
        addPhotosNotes.setClickable(true);
        addPhotosNotes.setBackgroundResource(R.drawable.curved_borders_blue);

        activitySpinner.setEnabled(true);
        activitySpinner.setClickable(true);

        ptl.setClickable(true);
        ptl.setBackgroundResource(R.drawable.curved_borders_blue);

        ar.setEnabled(true);
        gr.setEnabled(true);
        ant.setEnabled(true);
        mos.setEnabled(true);
        hf.setEnabled(true);
        bf.setEnabled(true);
        df.setEnabled(true);
        pf.setEnabled(true);
        ff.setEnabled(true);
        nr.setEnabled(true);
        cb.setEnabled(true);
        rr.setEnabled(true);
        liz.setEnabled(true);
        sp.setEnabled(true);
        bi.setEnabled(true);
        fm.setEnabled(true);
        oth.setEnabled(true);
        hm.setEnabled(true);
        rfb.setEnabled(true);

        populatePests();

        viewPhotosNotes.setClickable(true);
        viewPhotosNotes.setBackgroundResource(R.drawable.curved_borders_blue);

        save.setClickable(true);
        save.setBackgroundResource(R.drawable.curved_borders_blue);

        // Initialize values

        for(int i=0; i<conditionsSpinner.getCount();i++){

            if(conditionsSpinner.getItemAtPosition(i).toString().equals(currentDevice.get("device_condition"))){
                conditionsSpinner.setSelection(i);
                break;
            }
        }

        for(int i=0; i<activitySpinner.getCount();i++){

            if(activitySpinner.getItemAtPosition(i).toString().equals(currentDevice.get("activity"))){
                activitySpinner.setSelection(i);
                break;
            }
        }

        photoPath = currentDevice.get("image");
        photoNotes = currentDevice.get("notes");

        if(!currentDevice.get("image").equals("")){

            addPhotosNotes.setText("Change Photo/Notes");
        }



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

        };

        Log.d("LOCATION_AREA_ID",location_area_id);

        String selection = SterixContract.DeviceMonitoring.COLUMN_SERVICE_ORDER_ID +" = ? and "+ SterixContract.DeviceMonitoring.COLUMN_CLIENT_LOCATION_AREA_ID +" = ?";
        String selectionArgs[] = {service_order_id,location_area_id};
        String sortOrder = SterixContract.ServiceOrderArea._ID +" ASC";

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

    public HashMap<String,String> fetchConditions(){

        HashMap out = new HashMap<String,String>();

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.DeviceCondition.COLUMN_CONDITION_ID,
                SterixContract.DeviceCondition.COLUMN_CONDITION_NAME
        };

        String sortOrder = SterixContract.DeviceCondition.COLUMN_CONDITION_ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.DeviceCondition.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceCondition.COLUMN_CONDITION_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceCondition.COLUMN_CONDITION_NAME));

            out.put(name,id);

        }

        database.close();

        return out;

    }

    public HashMap<String,String> fetchActivities(){

        HashMap out = new HashMap<String,String>();

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_ID,
                SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_NAME
        };

        String sortOrder = SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.DeviceActivity.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceActivity.COLUMN_DEVICE_ACTIVITY_NAME));

            out.put(name,id);

        }

        database.close();
        return out;

    }


    public void proceedToDeviceSummary(View v){

        Intent deviceSummaryIntent = new Intent(getApplicationContext(), DeviceMonitoringSummaryActivity.class);
        deviceSummaryIntent.putExtra("DEVICES", devices);
        deviceSummaryIntent.putExtra("LOCATION_AREA", m.getLocation());
//        areaMonitoringIntent.putExtra("SERVICE_ORDER_ID", m.getService_order_id());
        deviceSummaryIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
        startActivity(deviceSummaryIntent);


    }

    public void proceedToAddPhoto(View v){

        Intent deviceSummaryIntent = new Intent(getApplicationContext(), DeviceMonitoringPhotoActivity.class);
        deviceSummaryIntent.putExtra("LOCATION_AREA", m.getLocation());
        deviceSummaryIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
        startActivity(deviceSummaryIntent);

    }

    public void proceedToViewPhoto(View v){

        Intent deviceSummaryIntent = new Intent(getApplicationContext(), DeviceMonitoringViewPhotoActivity.class);
        deviceSummaryIntent.putExtra("LOCATION_AREA", m.getLocation());
        deviceSummaryIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
        startActivity(deviceSummaryIntent);

    }

    public void saveDeviceMonitoring(View v){

        // Update device monitoring row
        SQLiteDatabase db = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        
        String condition = conditionsSpinner.getSelectedItem().toString();
        String conditionId="";

        if(!condition.equals("Condition"))
            conditionId = conditions.get(condition);

        values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION_ID,conditionId);

        if(condition.equals("Condition"))
            condition = "";

        values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION,condition);

        String activity = activitySpinner.getSelectedItem().toString();
        String activityId="";

        if(!activity.equals("Activity"))
            activityId = activities.get(activity);

        values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY_ID,activityId);

        if(activity.equals("Activity"))
            activity = "";

        values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY,activity);

        values.put(SterixContract.DeviceMonitoring.COLUMN_IMAGE,photoPath);
        values.put(SterixContract.DeviceMonitoring.COLUMN_NOTES,photoNotes);

        db.update(SterixContract.DeviceMonitoring.TABLE_NAME,values,SterixContract.DeviceMonitoring._ID + " = ?",new String[]{currentDevice.get("id")});

        // Save pest information

        String ant,ar,bf,bi,cb,df,ff,fm,gr,hf,hm,liz,mos,nr,pf,rfb,rr,sp,oth;

        EditText et = (EditText) findViewById(R.id.device_ar);
        ar = et.getText().toString();

        et = (EditText) findViewById(R.id.device_gr);
        gr = et.getText().toString();

        et = (EditText) findViewById(R.id.device_ant);
        ant = et.getText().toString();

        et = (EditText) findViewById(R.id.device_mos);
        mos = et.getText().toString();

        et = (EditText) findViewById(R.id.device_hf);
        hf = et.getText().toString();

        et = (EditText) findViewById(R.id.device_bf);
        bf = et.getText().toString();

        et = (EditText) findViewById(R.id.device_df);
        df = et.getText().toString();

        et = (EditText) findViewById(R.id.device_pf);
        pf = et.getText().toString();

        et = (EditText) findViewById(R.id.device_ff);
        ff = et.getText().toString();

        et = (EditText) findViewById(R.id.device_fm);
        fm = et.getText().toString();

        et = (EditText) findViewById(R.id.device_hm);
        hm = et.getText().toString();

        et = (EditText) findViewById(R.id.device_rr);
        rr = et.getText().toString();

        et = (EditText) findViewById(R.id.device_nr);
        nr = et.getText().toString();

        et = (EditText) findViewById(R.id.device_cb);
        cb = et.getText().toString();

        et = (EditText) findViewById(R.id.device_rfb);
        rfb = et.getText().toString();

        et = (EditText) findViewById(R.id.device_liz);
        liz = et.getText().toString();

        et = (EditText) findViewById(R.id.device_sp);
        sp = et.getText().toString();

        et = (EditText) findViewById(R.id.device_bi);
        bi = et.getText().toString();

        et = (EditText) findViewById(R.id.device_oth);
        oth = et.getText().toString();

        String[] pestNumbers = {ar,gr,ant,mos,hf,bf,df,pf,ff,fm,hm,rr,nr,cb,rfb,liz,sp,bi,oth};

        for(int i=0; i<pestNumbers.length; i++){

            if(!pestNumbers[i].equals("")){

                // Insert!
                values = new ContentValues();
                values.put(SterixContract.DeviceMonitoringPest.COLUMN_SERVICE_ORDER_ID,m.getService_order_id());
                values.put(SterixContract.DeviceMonitoringPest.COLUMN_DEVICE_MONITORING_ID,currentDevice.get("id"));
                values.put(SterixContract.DeviceMonitoringPest.COLUMN_PEST_ID,i+"");
                values.put(SterixContract.DeviceMonitoringPest.COLUMN_NUMBER,pestNumbers[i]);
                db.insert(SterixContract.DeviceMonitoringPest.TABLE_NAME, null, values);

            }
        }
        

        Toast t = Toast.makeText(getApplicationContext(),"Device information updated.",Toast.LENGTH_SHORT);
        t.show();

        db.close();
        //Start another instance of Device Monitoring

//        Intent areaMonitoringIntent = new Intent(getApplicationContext(), DeviceMonitoringActivity.class);
//        areaMonitoringIntent.putExtra("DEVICE_MONITORING_PARCEL", m);
//        areaMonitoringIntent.putExtra("LOCATION_AREA_ID", m.getLocation_area_id());
//        areaMonitoringIntent.putExtra("SERVICE_ORDER_ID", m.getService_order_id());
//        areaMonitoringIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
//        startActivity(areaMonitoringIntent);
//
//        // Finish current instance
//        finish();

    }

    public void rescanDevice(View v){

        Intent areaMonitoringIntent = new Intent(getApplicationContext(), DeviceMonitoringActivity.class);
        areaMonitoringIntent.putExtra("DEVICE_MONITORING_PARCEL", m);
        areaMonitoringIntent.putExtra("LOCATION_AREA_ID", m.getLocation_area_id());
        areaMonitoringIntent.putExtra("SERVICE_ORDER_ID", m.getService_order_id());
        areaMonitoringIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
        startActivity(areaMonitoringIntent);

        // Finish current instance
        finish();

    }

    public void populatePests(){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.DeviceMonitoringPest._ID,
                SterixContract.DeviceMonitoringPest.COLUMN_SERVICE_ORDER_ID,
                SterixContract.DeviceMonitoringPest.COLUMN_DEVICE_MONITORING_ID,
                SterixContract.DeviceMonitoringPest.COLUMN_PEST_ID,
                SterixContract.DeviceMonitoringPest.COLUMN_NUMBER
        };


        String selection = SterixContract.DeviceMonitoringPest.COLUMN_DEVICE_MONITORING_ID +" = ?";
        String selectionArgs[] = {currentDevice.get("id")};
        String sortOrder = SterixContract.DeviceMonitoringPest._ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.DeviceMonitoringPest.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        HashMap <String, Integer> pestResource = new HashMap<>();

        pestResource.put("0",R.id.device_ar);
        pestResource.put("1",R.id.device_gr);
        pestResource.put("2",R.id.device_ant);
        pestResource.put("3",R.id.device_mos);
        pestResource.put("4",R.id.device_hf);
        pestResource.put("5",R.id.device_bf);
        pestResource.put("6",R.id.device_df);
        pestResource.put("7",R.id.device_pf);
        pestResource.put("8",R.id.device_ff);
        pestResource.put("9",R.id.device_fm);
        pestResource.put("10",R.id.device_hm);
        pestResource.put("11",R.id.device_rr);
        pestResource.put("12",R.id.device_nr);
        pestResource.put("13",R.id.device_cb);
        pestResource.put("14",R.id.device_rfb);
        pestResource.put("15",R.id.device_liz);
        pestResource.put("16",R.id.device_sp);
        pestResource.put("17",R.id.device_bi);
        pestResource.put("18",R.id.device_oth);


        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringPest._ID));
            String pest_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringPest.COLUMN_PEST_ID));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringPest.COLUMN_NUMBER));

            EditText et = (EditText) findViewById(pestResource.get(pest_id));
            et.setText(number);
        }

        database.close();

    }


}
