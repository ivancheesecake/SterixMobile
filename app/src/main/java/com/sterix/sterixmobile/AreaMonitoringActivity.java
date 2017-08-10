package com.sterix.sterixmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AreaMonitoringActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_monitoring);

        Intent i = getIntent();
        Monitoring m =  i.getParcelableExtra("AREA_MONITORING_PARCEL");
        TextView tv_location = (TextView) findViewById(R.id.area_monitoring_location);
        tv_location.setText(m.getLocation());

    }
}
