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
import android.widget.Button;
import android.widget.EditText;
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

    Button condition,addPhotosNotes,action,ptl,viewSummary,viewPhotosNotes,save;
    EditText ar,gr,ant,mos,hf,bf,bf2,df,ff,nr,cb,rr,liz,sp,bi;

    CameraSource cameraSource;
    SurfaceView cameraView;
    TextView barcodeInfo;
    String service_order_location,service_order_id,location_area_id;
    ArrayList<HashMap<String,String>> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_monitoring);

        Intent i = getIntent();
        Monitoring m =  i.getParcelableExtra("DEVICE_MONITORING_PARCEL");
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        service_order_id = i.getStringExtra("SERVICE_ORDER_ID");
        location_area_id = i.getStringExtra("LOCATION_AREA_ID");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(service_order_location);
        setSupportActionBar(toolbar);

        // Fetch data from previous activity

        TextView tv_location = (TextView) findViewById(R.id.device_monitoring_location);
        tv_location.setText(m.getLocation());

        // Fetch all devices

        //Log.d("HEY",service_order_id);
        //Log.d("HEY",location_area_id);

        devices = fetchDevices(service_order_id,location_area_id);

        for(int j=0; j<devices.size();j++){

            Log.d("DEVICE",devices.get(j).get("device_code"));

        }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Disable everything at the beginning

        condition = (Button) findViewById(R.id.device_condition);
        condition.setClickable(false);

        addPhotosNotes = (Button) findViewById(R.id.device_add_photo);
        addPhotosNotes.setClickable(false);

        action = (Button) findViewById(R.id.device_action);
        action.setClickable(false);

        ptl = (Button) findViewById(R.id.device_ptl);
        ptl.setClickable(false);

        ar = (EditText) findViewById(R.id.device_ar);
        ar.setEnabled(false);
        ar.setMaxWidth(ar.getWidth());

        gr = (EditText) findViewById(R.id.device_gr);
        gr.setEnabled(false);
        gr.setMaxWidth(gr.getWidth());

        ant = (EditText) findViewById(R.id.device_ant);
        ant.setEnabled(false);
        ant.setMaxWidth(ant.getWidth());

        mos = (EditText) findViewById(R.id.device_mos);
        mos.setEnabled(false);
        mos.setMaxWidth(mos.getWidth());

        hf = (EditText) findViewById(R.id.device_hf);
        hf.setEnabled(false);
        hf.setMaxWidth(hf.getWidth());

        bf = (EditText) findViewById(R.id.device_bf);
        bf.setEnabled(false);
        bf.setMaxWidth(bf.getWidth());

        bf2 = (EditText) findViewById(R.id.device_bf2);
        bf2.setEnabled(false);
        bf2.setMaxWidth(bf2.getWidth());

        df = (EditText) findViewById(R.id.device_df);
        df.setEnabled(false);
        df.setMaxWidth(df.getWidth());

        ff = (EditText) findViewById(R.id.device_ff);
        ff.setEnabled(false);
        ff.setMaxWidth(ff.getWidth());

        nr = (EditText) findViewById(R.id.device_nr);
        nr.setEnabled(false);
        nr.setMaxWidth(nr.getWidth());

        cb = (EditText) findViewById(R.id.device_cb);
        cb.setEnabled(false);
        cb.setMaxWidth(cb.getWidth());

        rr = (EditText) findViewById(R.id.device_rr);
        rr.setEnabled(false);
        rr.setMaxWidth(rr.getWidth());

        liz = (EditText) findViewById(R.id.device_liz);
        liz.setEnabled(false);
        liz.setMaxWidth(liz.getWidth());

        sp = (EditText) findViewById(R.id.device_sp);
        sp.setEnabled(false);
        sp.setMaxWidth(sp.getWidth());


        bi = (EditText) findViewById(R.id.device_bi);
        bi.setEnabled(false);
        bi.setMaxWidth(bi.getWidth());

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
                .setRequestedPreviewSize(640, 480)
                .build();

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

                            for(int a = 0; a< devices.size(); a++){

                                Log.d("A", devices.get(a).get("device_code"));
                                Log.d("B", barcodes.valueAt(0).displayValue);
//                                Log.d("C", barcodes.valueAt(0).displayValue.equals(devices.get(a).get("device_code")));


                                if(devices.get(a).get("device_code").equals(barcodes.valueAt(0).displayValue)){

                                    Log.d("HERE", barcodes.valueAt(0).displayValue);

                                    barcodeInfo.setText(    // Update the TextView
                                            barcodes.valueAt(0).displayValue
                                    );


                                    cameraSource.stop();
                                    barcodeDetector.release();
                                    cameraView.setVisibility(View.GONE);


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

        condition.setClickable(true);
        condition.setBackgroundResource(R.drawable.curved_borders_blue);
        addPhotosNotes.setClickable(true);
        addPhotosNotes.setBackgroundResource(R.drawable.curved_borders_blue);

        action.setClickable(true);
        action.setBackgroundResource(R.drawable.curved_borders_blue);
        ptl.setClickable(true);
        ptl.setBackgroundResource(R.drawable.curved_borders_blue);

        ar.setEnabled(true);
        gr.setEnabled(true);
        ant.setEnabled(true);
        mos.setEnabled(true);
        hf.setEnabled(true);
        bf.setEnabled(true);
        bf2.setEnabled(true);
        df.setEnabled(true);
        ff.setEnabled(true);
        nr.setEnabled(true);
        cb.setEnabled(true);
        rr.setEnabled(true);
        liz.setEnabled(true);
        sp.setEnabled(true);
        bi.setEnabled(true);

        viewPhotosNotes.setClickable(true);
        viewPhotosNotes.setBackgroundResource(R.drawable.curved_borders_blue);

        save.setClickable(true);
        save.setBackgroundResource(R.drawable.curved_borders_blue);


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

        };

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

            HashMap<String,String> temp = new HashMap<>();

            temp.put("id",id);
            temp.put("service_order_id",so_id);
            temp.put("client_location_area_id",loc_id);
            temp.put("device_code",dcode);
            temp.put("device_condition_id",dc_id);
            temp.put("device_condition",dc);
            temp.put("activity_id",a_id);
            temp.put("activity",a);

            out.add(temp);

        }

        return out;
    }

}
