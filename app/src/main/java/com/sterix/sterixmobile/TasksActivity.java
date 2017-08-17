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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {

    private List<Task> tasks = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private Task t;
    public String service_order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        service_order_id =  i.getStringExtra("SERVICE_ORDER_ID");

        Log.d("HEY",service_order_id);


        recyclerView = (RecyclerView) findViewById(R.id.tasks_recycler_view);

        taskAdapter = new TaskAdapter(tasks,getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(taskAdapter);

        prepareTasks();

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



//                Context context = getApplicationContext();
//                CharSequence text = so.getDate();
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

                //Intent serviceOrdersIntent = new Intent(context, TasksActivity.class);
                //startActivity(serviceOrdersIntent);
                //finish();

            }
        });

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

}
