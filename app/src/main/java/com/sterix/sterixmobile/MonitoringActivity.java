package com.sterix.sterixmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MonitoringActivity extends AppCompatActivity {

    private List<List<Monitoring>> monitoring = new ArrayList<>();
    private List<Monitoring> monitoring_temp;
    private RecyclerView recyclerView;
    private MonitoringAdapter monitoringAdapter;
    private Monitoring m,n;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        recyclerView = (RecyclerView) findViewById(R.id.monitoring_recycler_view);

        monitoringAdapter = new MonitoringAdapter(monitoring, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(monitoringAdapter);

        prepareMonitoring();

         /*
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
        */


    }

    private void prepareMonitoring() {

        // Fetch data from server

        m = new Monitoring("0","Outer Perimeter","Review Area Report","2");
        n = new Monitoring("0","Outer Perimeter","Review Devices","2");
        monitoring_temp = new ArrayList<>();
        monitoring_temp.add(m);
        monitoring_temp.add(n);
        monitoring.add(monitoring_temp);


        m = new Monitoring("1","Warehouse 1","Review Area Report", "2");
        n = new Monitoring("1","Warehouse 1","Review Devices","2");
        monitoring_temp = new ArrayList<>();
        monitoring_temp.add(m);
        monitoring_temp.add(n);
        monitoring.add(monitoring_temp);

        m = new Monitoring("2","Warehouse 2","Review Area Report","1");
        n = new Monitoring("2","Warehouse 2","Start Device Monitoring","0");
        monitoring_temp = new ArrayList<>();
        monitoring_temp.add(m);
        monitoring_temp.add(n);
        monitoring.add(monitoring_temp);

        m = new Monitoring("3","Production Area","Start Area Monitoring","0");
        n = new Monitoring("3","Production Area","Start Device Monitoring","0");
        monitoring_temp = new ArrayList<>();
        monitoring_temp.add(m);
        monitoring_temp.add(n);
        monitoring.add(monitoring_temp);

        m = new Monitoring("4","Pantry","Start Area Monitoring","0");
        n = new Monitoring("4","Pantry","Start Device Monitoring", "0");
        monitoring_temp = new ArrayList<>();
        monitoring_temp.add(m);
        monitoring_temp.add(n);
        monitoring.add(monitoring_temp);

        monitoringAdapter.notifyDataSetChanged();

    }

    public void proceedToMonitoring(View v){

        Monitoring m = (Monitoring) v.getTag();
        Log.d("HEY",m.getLocation());

        if(m.getMonitoringTask().equals("Start Area Monitoring")) {
            Intent areaMonitoringIntent = new Intent(getApplicationContext(), AreaMonitoringActivity.class);
            areaMonitoringIntent.putExtra("AREA_MONITORING_PARCEL", m);
            startActivity(areaMonitoringIntent);
        }

        else if(m.getMonitoringTask().equals("Start Device Monitoring")) {
            Intent areaMonitoringIntent = new Intent(getApplicationContext(), DeviceMonitoringActivity.class);
            areaMonitoringIntent.putExtra("DEVICE_MONITORING_PARCEL", m);
            startActivity(areaMonitoringIntent);
        }
    }


}



