package com.sterix.sterixmobile;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;

public class UrgentTask extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    public boolean startTime;
    public String service_order_id;
    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgent_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        service_order_id =  i.getStringExtra("SERVICE_ORDER_ID");

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        ip = sharedPref.getString("IP", "");

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        EditText start_et = (EditText) findViewById(R.id.start_time_et);
        EditText end_et = (EditText) findViewById(R.id.end_time_et);

        start_et.setText(String.format("%02d",hour)+":"+String.format("%02d",minute)+":00");
        end_et.setText(String.format("%02d",hour)+":"+String.format("%02d",minute)+":00");

        Button start_time_button = (Button) findViewById(R.id.start_time_button);
        start_time_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"Time Picker");
                startTime = true;
            }
        });

        Button end_time_button = (Button) findViewById(R.id.end_time_button);
        end_time_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"Time Picker");
                startTime = false;
            }
        });


    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hr, int min) {

        EditText et;
        if(startTime){
             et = (EditText) findViewById(R.id.start_time_et);
        }

        else{
            et = (EditText) findViewById(R.id.end_time_et);
        }

        et.setText(String.format("%02d",hr)+":"+String.format("%02d",min));
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

            default:

                return super.onOptionsItemSelected(item);

        }
    }


    public void saveTask(View v){

        Log.d("TASK","HERE");

        EditText task_et, start_time_et, end_time_et;
        String task, start_time, end_time;

        task_et = (EditText) findViewById(R.id.urgent_tast_et);
        start_time_et = (EditText) findViewById(R.id.start_time_et);
        end_time_et = (EditText) findViewById(R.id.end_time_et);

        task = task_et.getText().toString();
        start_time = start_time_et.getText().toString();
        end_time = end_time_et.getText().toString();

//        Log.d("MAHIRAP","insertDeviceMonitoringToQueue");
//
//        Log.d("MAHIRAP","device code "+device_code+" device condition id"+device_condition_ID);
        SQLiteDatabase db = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID,service_order_id);
        values.put(SterixContract.ServiceOrderTask.COLUMN_TASK,task);
        values.put(SterixContract.ServiceOrderTask.COLUMN_STATUS,7);
        values.put(SterixContract.ServiceOrderTask.COLUMN_START_TIME,start_time);
        values.put(SterixContract.ServiceOrderTask.COLUMN_END_TIME,end_time);
//

        db.insert(SterixContract.ServiceOrderTask.TABLE_NAME, null, values);
//

//      0 kapag walang area!!!!!
        Task t = new Task("69",start_time, task, "7","generic","0");
        TasksActivity.tasks.add(t);

        TasksActivity.taskAdapter.notifyDataSetChanged();

        Toast tusta = Toast.makeText(this,"Urgent task added.",Toast.LENGTH_LONG);
        tusta.show();


        if(isOnline()){

            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="http://"+ip+"/SterixBackend/addUrgentTask.php";



            HashMap params = new HashMap<String,String>();
            params.put("service_order_ID",service_order_id);
            params.put("task",task);
            params.put("start_time",start_time);
            params.put("end_time",end_time);

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
                                    Toast toast = Toast.makeText(getApplicationContext(),"Server was notified of urgent task.", Toast.LENGTH_SHORT);
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

        else{

            ContentValues values2 = new ContentValues();

            values2.put(SterixContract.ServiceOrderTaskQueue.COLUMN_SERVICE_ORDER_ID,service_order_id);
            values2.put(SterixContract.ServiceOrderTaskQueue.COLUMN_TASK,task);
            values2.put(SterixContract.ServiceOrderTaskQueue.COLUMN_STATUS,7);
            values2.put(SterixContract.ServiceOrderTaskQueue.COLUMN_START_TIME,start_time);
            values2.put(SterixContract.ServiceOrderTaskQueue.COLUMN_END_TIME,end_time);
//
            db.insert(SterixContract.ServiceOrderTaskQueue.TABLE_NAME, null, values2);


        }

        db.close();
        finish();
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
