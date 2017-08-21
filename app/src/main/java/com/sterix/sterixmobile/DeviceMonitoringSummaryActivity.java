package com.sterix.sterixmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceMonitoringSummaryActivity extends AppCompatActivity {

    ArrayList<HashMap<String,String>> devices;

    String service_order_location,location_area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_monitoring_summary);


        Intent i = getIntent();
        devices = (ArrayList<HashMap<String,String>>) i.getSerializableExtra("DEVICES");
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        location_area = i.getStringExtra("LOCATION_AREA");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(service_order_location);
        setSupportActionBar(toolbar);

        TextView tv_location = (TextView) findViewById(R.id.device_summary_location);
        tv_location.setText(location_area);



        for(int j=0; j<devices.size();j++){

            HashMap<String,String> currDevice = devices.get(j);

            final TableLayout summary = (TableLayout) findViewById(R.id.device_summary_row2);
            final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.tablerow, null);

            TextView tv;

            tv = (TextView) tableRow.findViewById(R.id.cell_deviceID);
            tv.setText(currDevice.get("device_code"));

            tv = (TextView) tableRow.findViewById(R.id.cell_Condition);
            tv.setText(currDevice.get("device_condition"));

            tv = (TextView) tableRow.findViewById(R.id.cell_Activity);
            tv.setText(currDevice.get("activity"));

//            tv = (TextView) tableRow.findViewById(R.id.cell_Status);
//            tv.setText("");

            if((j+1)%2 == 1)
                tableRow.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDisabled));

            summary.addView(tableRow);
        }

    }

    public void backToDeviceMonitoring(View v){

       finish();
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
