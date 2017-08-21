package com.sterix.sterixmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class AreaMonitoringActivity extends AppCompatActivity {

    Monitoring m;
    String service_order_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_monitoring);

        Intent i = getIntent();
        m =  i.getParcelableExtra("AREA_MONITORING_PARCEL");
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        TextView tv_location = (TextView) findViewById(R.id.area_monitoring_location);
        tv_location.setText(m.getLocation());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(service_order_location);





        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


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


    public void addFindings(View v){

        Log.d("D","Add Findings");
        Intent intent = new Intent(this, AreaAddFindingActivity.class);
        intent.putExtra("AREA_MONITORING_PARCEL",m);
        intent.putExtra("SERVICE_ORDER_LOCATION",service_order_location);
        startActivity(intent);

    }

    public void viewFindings(View v){

        Log.d("D","View Findings");
        Intent intent = new Intent(this, AreaViewFindingActivity.class);
        intent.putExtra("AREA_MONITORING_PARCEL",m);
        intent.putExtra("SERVICE_ORDER_LOCATION",service_order_location);
        startActivity(intent);

    }

}
