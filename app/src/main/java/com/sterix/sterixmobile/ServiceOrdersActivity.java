package com.sterix.sterixmobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ServiceOrdersActivity extends AppCompatActivity {

    private List<ServiceOrder> serviceOrders = new ArrayList<>();
    private RecyclerView recyclerView;
    private ServiceOrderAdapter soAdapter;
    private ServiceOrder so;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_orders);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.service_order_recycler_view);

        soAdapter = new ServiceOrderAdapter(serviceOrders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(soAdapter);

        prepareServiceOrders();

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                so = serviceOrders.get(position);

                Log.d("Date",so.getDate());
                Log.d("Location",so.getLocation());
                Log.d("Order",so.getOrder());

                Context context = getApplicationContext();
//                CharSequence text = so.getDate();
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

                Intent serviceOrdersIntent = new Intent(context, TasksActivity.class);
                startActivity(serviceOrdersIntent);
                //finish();

            }
        });

    }


    private void prepareServiceOrders() {

        // Fetch data from server

        so = new ServiceOrder("Jul 31", "Nestle Cabuyao", "Regular Inspection");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 7", "Nestle Cabuyao", "Regular Inspection");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 14", "Nestle Cabuyao", "Treatment");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 7", "Nestle Cabuyao", "Regular Inspection");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 14", "Nestle Cabuyao", "Treatment");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 7", "Nestle Cabuyao", "Regular Inspection");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 14", "Nestle Cabuyao", "Treatment");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 7", "Nestle Cabuyao", "Regular Inspection");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 14", "Nestle Cabuyao", "Treatment");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 7", "Nestle Cabuyao", "Regular Inspection");
        serviceOrders.add(so);

        so = new ServiceOrder("Aug 14", "Nestle Cabuyao", "Treatment");
        serviceOrders.add(so);

        soAdapter.notifyDataSetChanged();

    }


}
