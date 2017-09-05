package com.sterix.sterixmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    public void backToDeviceMonitoring(View v){
        finish();
    }
}
