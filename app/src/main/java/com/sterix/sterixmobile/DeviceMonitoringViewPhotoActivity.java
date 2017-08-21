package com.sterix.sterixmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class DeviceMonitoringViewPhotoActivity extends AppCompatActivity {

    String service_order_location,location_area;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_monitoring_view_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();

        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        toolbar.setSubtitle(service_order_location);

        location_area = i.getStringExtra("LOCATION_AREA");
        TextView tv_location = (TextView) findViewById(R.id.device_monitoring_view_photo_location);
        tv_location.setText(location_area);

        ImageView imageView = (ImageView) findViewById(R.id.device_monitoring_view_photo_row2);
        TextView textView = (TextView) findViewById(R.id.device_monitoring_view_photo_row3);
        textView.setText(DeviceMonitoringActivity.photoNotes);
        //imageView.setVisibility(View.GONE);

        try{
            Log.d("Path",DeviceMonitoringActivity.photoPath);
            File image = new File(DeviceMonitoringActivity.photoPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.25),(int)(bitmap.getHeight()*0.25),true);
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e){
            Log.d("Path", "not found");
            imageView.setVisibility(View.GONE);
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

    public void backToDeviceMonitoring(View v){
        finish();
    }
}
