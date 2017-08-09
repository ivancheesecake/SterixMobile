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

    private List<Monitoring> monitoring = new ArrayList<>();
    private RecyclerView recyclerView;
    private MonitoringAdapter monitoringAdapter;
    private Monitoring m;


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

        m = new Monitoring("0","Outer Perimeter",new String[] {"Review Area Report","Review Devices"},new String[]{"2","2"});
        monitoring.add(m);
        m = new Monitoring("1","Warehouse 1",new String[] {"Review Area Report","Review Devices"},new String[]{"2","2"});
        monitoring.add(m);
        m = new Monitoring("2","Warehouse 2",new String[] {"Review Area Report","Start Device Monitoring"},new String[]{"1","0"});
        monitoring.add(m);
        m = new Monitoring("3","Production Area",new String[] {"Start Area Monitoring","Start Device Monitoring"},new String[]{"0","0"});
        monitoring.add(m);
        m = new Monitoring("4","Pantry",new String[] {"Start Area Monitoring","Start Device Monitoring"}, new String[]{"0","0"});
        monitoring.add(m);

        monitoringAdapter.notifyDataSetChanged();

    }



}



