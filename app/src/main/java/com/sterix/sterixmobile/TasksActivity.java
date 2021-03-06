package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TasksActivity extends AppCompatActivity {

    private List<Task> tasks = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView locationTextView;
    private TaskAdapter taskAdapter;
    private Task t;
    public String service_order_id,service_order_location;

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

        t = new Task(id,time, task, status,"monitoring");
        tasks.remove(tasks.size()-1);
        tasks.add(t);


        taskAdapter.notifyDataSetChanged();

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

                // Perform change locally

                // Perform change on server

                updateStatusOnServer(currentTask.getId(),currentTask.getStatus());

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

    public void proceedToMonitoring(View v){

        Intent monitoringIntent = new Intent(getApplicationContext(), MonitoringActivity.class);
        monitoringIntent.putExtra("SERVICE_ORDER_ID",service_order_id);
        monitoringIntent.putExtra("SERVICE_ORDER_LOCATION",service_order_location);
        startActivity(monitoringIntent);

    }

    public void updateStatusOnServer(String task_id,String status){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.63.39/SterixBackend/updateTaskStatus.php";

        HashMap params = new HashMap<String,String>();
        params.put("task_id",task_id);
        params.put("status",status);

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

}
