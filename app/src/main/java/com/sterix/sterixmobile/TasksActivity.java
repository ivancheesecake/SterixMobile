package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TasksActivity extends AppCompatActivity {

    private List<Task> tasks = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView locationTextView;
    private TaskAdapter taskAdapter;
    private Task t;
    public String service_order_id,service_order_location;
    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        Intent i = getIntent();
        service_order_id =  i.getStringExtra("SERVICE_ORDER_ID");
        service_order_location =  i.getStringExtra("SERVICE_ORDER_LOCATION");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(service_order_location);


        setSupportActionBar(toolbar);

        Log.d("HEY",service_order_id);

//        locationTextView = (TextView) findViewById(R.id.tasks_location);
//        locationTextView.setText(service_order_location);

        recyclerView = (RecyclerView) findViewById(R.id.tasks_recycler_view);

        taskAdapter = new TaskAdapter(tasks,getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(taskAdapter);

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        ip = sharedPref.getString("IP", "");


        prepareTasks();


    }

    private void prepareTasks() {


        // Fetch data from server

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        String[] projection = {
                SterixContract.ServiceOrderTask._ID,
                SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID,
                SterixContract.ServiceOrderTask.COLUMN_TASK,
                SterixContract.ServiceOrderTask.COLUMN_START_TIME,
                SterixContract.ServiceOrderTask.COLUMN_STATUS

        };

        String selection = SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID +" = ?";
        String selectionArgs[] = {service_order_id};
        String sortOrder = SterixContract.ServiceOrderTask._ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.ServiceOrderTask.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        String id="",time="",task="",status="";

        while(cursor.moveToNext()){

            id= cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderTask._ID));
            time = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderTask.COLUMN_START_TIME));
            task = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderTask.COLUMN_TASK));
            status = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderTask.COLUMN_STATUS));

            Log.d("Prepare Tasks - Status",status);
            t = new Task(id,time, task, status,"generic");
            tasks.add(t);

        }

        //t = new Task(id,time, task, status,"monitoring");
        //tasks.remove(tasks.size()-1);
        //tasks.add(t);

        taskAdapter.notifyDataSetChanged();
        database.close();

    }

    public void updateStatus(View v){

        Task currentTask;
        String id = v.getTag().toString();

        SQLiteDatabase db = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();


        // I'm going to do the inefficient thing

        for(int i=0; i<tasks.size();i++){

            currentTask = tasks.get(i);
            if(currentTask.getId()==id){

                // Perform change locally

                currentTask.setStatus(Integer.toString(((Integer.parseInt(currentTask.getStatus())+1)%3)));
                tasks.set(i,currentTask);

                values.put(SterixContract.ServiceOrderTask.COLUMN_STATUS, currentTask.getStatus());

                String selection = SterixContract.ServiceOrderTask._ID + "= ?";
                String[] selectionArgs = {currentTask.getId()};

                int count = db.update(
                        SterixContract.ServiceOrderTask.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);


                Log.d("count",Integer.toString(count));
                Log.d("status",currentTask.getStatus());


                // Perform change on server
                if (isOnline())
                    updateStatusOnServer(currentTask.getId(),currentTask.getStatus());
                else{
                    // Store updates locally

                    SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
                    String tasksUpdate = sharedPref.getString("TASK_UPDATES", "");
                    tasksUpdate += currentTask.getId()+","+currentTask.getStatus()+","+new SimpleDateFormat("HH:mm:ss").format(new Date())+"|";
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("TASK_UPDATES",tasksUpdate);
                    editor.commit();

                    Log.d("TASK_UPDATES",tasksUpdate);

                }


                if(currentTask.getStatus().equals("0")) {
                    v.setBackgroundColor(getResources().getColor(R.color.red));
                }
                else if(currentTask.getStatus().equals("1")) {
                    v.setBackgroundColor(getResources().getColor(R.color.yellow));
                }

                if(currentTask.getStatus().equals("2")) {
                    v.setBackgroundColor(getResources().getColor(R.color.green));
                }

                break;

                }
            }


//        Context context = getApplicationContext();
//        CharSequence text = v.getTag().toString();
//        int duration = Toast.LENGTH_SHORT;
//
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();

        db.close();
        }

    public void proceedToAcknowledgement(View v){

        Intent acknowledgementIntent = new Intent(getApplicationContext(), AcknowledgementActivity.class);
        startActivity(acknowledgementIntent);
    }

    public void openTask(View v){

        Log.d("HEY","HEY");
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
                SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
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

    public void proceedToMonitoring(View v){

        Intent monitoringIntent = new Intent(getApplicationContext(), MonitoringActivity.class);
        monitoringIntent.putExtra("SERVICE_ORDER_ID",service_order_id);
        monitoringIntent.putExtra("SERVICE_ORDER_LOCATION",service_order_location);
        startActivity(monitoringIntent);

    }

    public void updateStatusOnServer(String task_id,String status){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+ip+"/SterixBackend/updateTaskStatus.php";

        HashMap params = new HashMap<String,String>();
        params.put("task_id",task_id);
        params.put("status",status);
        params.put("timestamp",new SimpleDateFormat("HH:mm:ss").format(new Date()));

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.get("success").toString().equals("true")) {
                                Toast toast = Toast.makeText(getApplicationContext(),"Status successfully updated!", Toast.LENGTH_SHORT);
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
