package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MonitoringActivity extends AppCompatActivity {

    private List<List<Monitoring>> monitoring = new ArrayList<>();
    private List<Monitoring> monitoring_temp;
    private RecyclerView recyclerView;
    private MonitoringAdapter monitoringAdapter;
    private Monitoring m,n;
    public String service_order_id,service_order_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        Intent i = getIntent();
        service_order_id =  i.getStringExtra("SERVICE_ORDER_ID");
        service_order_location =  i.getStringExtra("SERVICE_ORDER_LOCATION");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(service_order_location);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.monitoring_recycler_view);

        monitoringAdapter = new MonitoringAdapter(monitoring, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(monitoringAdapter);

        prepareMonitoring();

         /*
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                t = tasks.get(position);

                Log.d("Time",t.getTime());
                Log.d("Task",t.getTask());

                if(t.getType().equals("monitoring")){

                    Log.d("Task","Monitoring");
                    Intent monitoringIntent = new Intent(getApplicationContext(), MonitoringActivity.class);
                    startActivity(monitoringIntent);
                }
                else if(t.getType().equals("treatment")){

                    Log.d("Task","Treatment");
                    Intent treatmentIntent = new Intent(getApplicationContext(), TreatmentActivity.class);
                    startActivity(treatmentIntent);
                }
        */


    }

    private void prepareMonitoring() {

        // Fetch data from server

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        String[] projection = {
                SterixContract.ServiceOrderArea._ID,
                SterixContract.ServiceOrderArea.COLUMN_SERVICE_ORDER_ID,
                SterixContract.ServiceOrderArea.COLUMN_CLIENT_LOCATION_AREA_ID,
                SterixContract.ServiceOrderArea.COLUMN_CLIENT_LOCATION_AREA,
                SterixContract.ServiceOrderArea.COLUMN_STATUS

        };

        String selection = SterixContract.ServiceOrderArea.COLUMN_SERVICE_ORDER_ID +" = ?";
        String selectionArgs[] = {service_order_id};
        String sortOrder = SterixContract.ServiceOrderArea._ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.ServiceOrderArea.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );


        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderArea._ID));
            String so_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderArea.COLUMN_SERVICE_ORDER_ID));
            String location_area_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderArea.COLUMN_CLIENT_LOCATION_AREA_ID));
            String location_area = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderArea.COLUMN_CLIENT_LOCATION_AREA));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderArea.COLUMN_STATUS));

            m = new Monitoring(id,location_area,"Start Area Monitoring",status,so_id,location_area_id);
            n = new Monitoring(id,location_area,"Start Device Monitoring",status,so_id,location_area_id);

            monitoring_temp = new ArrayList<>();
            monitoring_temp.add(m);
            monitoring_temp.add(n);
            monitoring.add(monitoring_temp);

        }


//        m = new Monitoring("0","Outer Perimeter","Review Area Report","2");
//        n = new Monitoring("0","Outer Perimeter","Review Devices","2");
//        monitoring_temp = new ArrayList<>();
//        monitoring_temp.add(m);
//        monitoring_temp.add(n);
//        monitoring.add(monitoring_temp);

        monitoringAdapter.notifyDataSetChanged();

    }

    public void proceedToMonitoring(View v){

        Monitoring m = (Monitoring) v.getTag();
        Log.d("HEY",m.getLocation());
        Log.d("HEY",m.getLocation_area_id());
        Log.d("HEY",m.getService_order_id());

        if(m.getMonitoringTask().equals("Start Area Monitoring")) {
            Intent areaMonitoringIntent = new Intent(getApplicationContext(), AreaMonitoringActivity.class);
            areaMonitoringIntent.putExtra("AREA_MONITORING_PARCEL", m);
            areaMonitoringIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
            startActivity(areaMonitoringIntent);
        }

        else if(m.getMonitoringTask().equals("Start Device Monitoring")) {
            Intent areaMonitoringIntent = new Intent(getApplicationContext(), DeviceMonitoringActivity.class);
            areaMonitoringIntent.putExtra("DEVICE_MONITORING_PARCEL", m);
            areaMonitoringIntent.putExtra("LOCATION_AREA_ID", m.getLocation_area_id());
            areaMonitoringIntent.putExtra("SERVICE_ORDER_ID", m.getService_order_id());
            areaMonitoringIntent.putExtra("SERVICE_ORDER_LOCATION", service_order_location);
            startActivity(areaMonitoringIntent);
        }
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


}



