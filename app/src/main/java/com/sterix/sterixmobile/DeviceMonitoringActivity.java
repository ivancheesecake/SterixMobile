package com.sterix.sterixmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DeviceMonitoringActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_monitoring);


        Intent i = getIntent();
        Monitoring m =  i.getParcelableExtra("DEVICE_MONITORING_PARCEL");
        TextView tv_location = (TextView) findViewById(R.id.device_monitoring_location);
        tv_location.setText(m.getLocation());
    }
}
