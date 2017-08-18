package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ServiceOrdersActivity extends AppCompatActivity {

    private List<ServiceOrder> serviceOrders = new ArrayList<>();
    private RecyclerView recyclerView;
    private ServiceOrderAdapter soAdapter;
    private ServiceOrder so;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
                serviceOrdersIntent.putExtra("SERVICE_ORDER_ID",so.getId());
                serviceOrdersIntent.putExtra("SERVICE_ORDER_LOCATION",so.getLocation());
                startActivity(serviceOrdersIntent);
                //finish();

            }
        });

    }


    private void prepareServiceOrders(){

        // Fetch data from server

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        String[] projection = {
                SterixContract.ServiceOrder._ID,
                SterixContract.ServiceOrder.COLUMN_SERVICE_TYPE,
                SterixContract.ServiceOrder.COLUMN_LOCATION,
                SterixContract.ServiceOrder.COLUMN_START_DATE,
                SterixContract.ServiceOrder.COLUMN_START_TIME,
                SterixContract.ServiceOrder.COLUMN_END_DATE,
                SterixContract.ServiceOrder.COLUMN_END_TIME,
                SterixContract.ServiceOrder.COLUMN_STATUS

        };

        String sortOrder = SterixContract.ServiceOrder._ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.ServiceOrder.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(cursor.moveToNext()){

            //SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            //String month_name = month_date.format(date);

            String date_formatted = "";

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrder._ID));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrder.COLUMN_LOCATION));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrder.COLUMN_SERVICE_TYPE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrder.COLUMN_START_DATE));
            //date = date.replace("-","/");

            try {
                Log.d("HEY",date);
                Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                date_formatted = new SimpleDateFormat("MMM dd").format(parse);


            }
            catch(Exception e){
                date_formatted = "NULL";
            }

            so = new ServiceOrder(id,date_formatted, location, type);
            serviceOrders.add(so);
        }


        soAdapter.notifyDataSetChanged();

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
