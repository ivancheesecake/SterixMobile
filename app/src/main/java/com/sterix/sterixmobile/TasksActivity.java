package com.sterix.sterixmobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {

    private List<Task> tasks = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private Task t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

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

        t = new Task("0","6:00 AM", "Login at guard house", "0","generic");
        tasks.add(t);

        t = new Task("1","6:10 AM", "Prepare Materials", "0","generic");
        tasks.add(t);

        t = new Task("2","7:00 AM", "Conduct pre Treatment Monitoring", "0","monitoring");
        tasks.add(t);

        t = new Task("3","9:00 AM", "Conduct Treatment", "0","treatment");
        tasks.add(t);

        t = new Task("4","3:00 PM", "Conduct post treatment monitoring. ", "0","monitoring");
        tasks.add(t);

        taskAdapter.notifyDataSetChanged();

    }

    public void updateStatus(View v){

        Task currentTask;
        String id = v.getTag().toString();

        // I'm going to do the inefficient thing

        for(int i=0; i<tasks.size();i++){

            currentTask = tasks.get(i);
            if(currentTask.getId()==id){

                currentTask.setStatus(Integer.toString(((Integer.parseInt(currentTask.getStatus())+1)%3)));
                tasks.set(i,currentTask);

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

        }

    public void proceedToAcknowledgement(View v){

        Intent acknowledgementIntent = new Intent(getApplicationContext(), AcknowledgementActivity.class);
        startActivity(acknowledgementIntent);
    }

    public void openTask(View v){

        Log.d("HEY","HEY");
    }

}
