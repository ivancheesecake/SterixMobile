package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ServiceOrdersActivity extends AppCompatActivity {

    private List<ServiceOrder> serviceOrders = new ArrayList<>();
    private RecyclerView recyclerView;
    private ServiceOrderAdapter soAdapter;
    private ServiceOrder so;
    public String ip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ProgressBar progress = (ProgressBar) findViewById(R.id.progress);

        progress.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        recyclerView = (RecyclerView) findViewById(R.id.service_order_recycler_view);

        soAdapter = new ServiceOrderAdapter(serviceOrders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(soAdapter);

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        String userId = sharedPref.getString("USER_ID", "");
        ip = sharedPref.getString("IP", "");

        syncData("12");


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


            }
        });

    }

    public void syncData(String user_id){

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String max_service_order_id = sharedPref.getString("MAX_SERVICE_ORDER_ID", "0");

        Log.d("SYNC DATA",max_service_order_id);

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",user_id);
        params.put("max_service_order_id",max_service_order_id);

//        Toast t = Toast.makeText(getApplicationContext(),"Here",Toast.LENGTH_LONG);
//        t.show();
//        Log.d("USER_ID_2",user_id);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+ip+"/SterixBackend/sync.php";

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

//                            Log.d("Response",response.get("service_orders").toString());
                            insertServiceOrders(new JSONArray(response.get("service_orders").toString()));
                            prepareServiceOrders();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error: ", "WHY LISA, WHY?");
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        queue.add(request_json);

    }

    public void insertServiceOrders(JSONArray serviceOrders){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        Log.d("HUUUUUY","FRIEEEEEND");

        // Initialize Service Orders
        int maxID = 0;
        int currID = 0;


        SharedPreferences sharedPref = ServiceOrdersActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        for(int i=0;i<serviceOrders.length();i++) {
            try{

                ContentValues values = new ContentValues();

                JSONObject obj = serviceOrders.getJSONObject(i);
                currID = Integer.parseInt(obj.getString("service_order_ID"));

                if(maxID < currID) {
                    // THIS IS INEFFICIENT
                    maxID = currID;
                    editor.putString("MAX_SERVICE_ORDER_ID", Integer.toString(maxID));
                    editor.commit();

                }
                values.put(SterixContract.ServiceOrder._ID,obj.getString("service_order_ID"));
                values.put(SterixContract.ServiceOrder.COLUMN_SERVICE_TYPE,obj.getString("service_type_name"));
                values.put(SterixContract.ServiceOrder.COLUMN_LOCATION,obj.getString("client_location_name"));
                values.put(SterixContract.ServiceOrder.COLUMN_START_DATE,obj.getString("start_date"));
                values.put(SterixContract.ServiceOrder.COLUMN_START_TIME,obj.getString("start_time"));
                values.put(SterixContract.ServiceOrder.COLUMN_END_DATE,obj.getString("end_date"));
                values.put(SterixContract.ServiceOrder.COLUMN_END_TIME,obj.getString("end_time"));
                values.put(SterixContract.ServiceOrder.COLUMN_STATUS,obj.getString("service_order_status_type_ID"));

                database.insert(SterixContract.ServiceOrder.TABLE_NAME, null, values);

            }catch(Exception e){};

        }

        database.close();

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

        database.close();
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
