package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
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
    public ProgressBar progress;
    public RelativeLayout progressWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = (ProgressBar) findViewById(R.id.progress);
        progress.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
        progressWrapper = (RelativeLayout) findViewById(R.id.progress_wrapper);

        progressWrapper.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.service_order_recycler_view);

        soAdapter = new ServiceOrderAdapter(serviceOrders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(soAdapter);

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        String userId = sharedPref.getString("USER_ID", "");
        ip = sharedPref.getString("IP", "");

        Log.d("AM_PEST_QUEUE",processAreaMonitoringPestQueue());


//        Log.d("MAY INTERNET?",Boolean.toString(isOnline()));

        //processDeviceMonitoringPestQueue()
        if(isOnline())
            syncData(userId);
        else {
            prepareServiceOrders();
            progressWrapper.setVisibility(View.GONE);
            Toast t = Toast.makeText(this,"You are offline. Please connect to the internet to sync data with the Sterix server.",Toast.LENGTH_LONG);
            t.show();

            Log.d("DEVICE_MONITORING_QUEUE",processDeviceMonitoringQueue());
        }

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                so = serviceOrders.get(position);

                Log.d("Date",so.getDate());
                Log.d("Location",so.getLocation());
                Log.d("Order",so.getOrder());

                Context context = getApplicationContext();

                Intent serviceOrdersIntent = new Intent(context, TasksActivity.class);
                serviceOrdersIntent.putExtra("SERVICE_ORDER_ID",so.getId());
                serviceOrdersIntent.putExtra("SERVICE_ORDER_LOCATION",so.getLocation());
                startActivity(serviceOrdersIntent);


            }
        });

        imageUpload();

    }

    public void syncData(String user_id){

        final SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",user_id);

        // Process Task Updates QUEUE
        // Cheap string implementation, will I transition to SQL? IDK.
        params.put("task_updates",sharedPref.getString("TASK_UPDATES",""));

        // Process Device Monitoring QUEUE
        params.put("device_monitoring_updates",processDeviceMonitoringQueue());
//        Log.d("DEVICE_MONITORING_QUEUE",processDeviceMonitoringQueue());

        params.put("area_monitoring_updates",processAreaMonitoringQueue());
        params.put("area_monitoring_pest_updates",processAreaMonitoringPestQueue());

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+ip+"/SterixBackend/sync.php";

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new ProcessResponseTask().execute(response);
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

    private class ProcessResponseTask extends AsyncTask<JSONObject, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            displayProgressBar("Downloading...");
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject response = params[0];
            try {

//                            Log.d("Response",response.get("service_orders").toString());
                insertServiceOrders(new JSONArray(response.get("service_orders").toString()));

                insertServiceOrderTasks(new JSONArray(response.get("service_order_tasks").toString()));
                insertServiceOrderAreas(new JSONArray(response.get("service_order_areas").toString()));

                insertDeviceMonitoring(new JSONArray(response.get("device_monitoring").toString()));
                insertDeviceMonitoringPest(new JSONArray(response.get("device_monitoring_pest").toString()));

//                            Log.d("AREA_MONITORING",response.get("area_monitoring").toString());
                insertAreaMonitoring(new JSONArray(response.get("area_monitoring").toString()));
                insertAreaMonitoringPest(new JSONArray(response.get("area_monitoring_pest").toString()));




                // Empty update queues
                SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("TASK_UPDATES","");
                editor.commit();

                // EMPTY QUEUES, bakit mo nalimutan to?
                SQLiteDatabase database = new SterixDBHelper(getApplicationContext()).getWritableDatabase();
                database.execSQL("delete from "+SterixContract.DeviceMonitoringQueue.TABLE_NAME);
                database.execSQL("delete from "+SterixContract.DeviceMonitoringPestQueue.TABLE_NAME);
                database.execSQL("delete from "+SterixContract.AreaFindingQueue.TABLE_NAME);
                database.execSQL("delete from "+SterixContract.AreaMonitoringPestQueue.TABLE_NAME);
                database.close();



            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "Finished.";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressWrapper.setVisibility(View.GONE);
            prepareServiceOrders();
            Toast t = Toast.makeText(getApplicationContext(),"Sync completed.",Toast.LENGTH_LONG);
            t.show();

        }
    }





    public void insertServiceOrders(JSONArray serviceOrders){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        database.execSQL("delete from "+SterixContract.ServiceOrder.TABLE_NAME);

        for(int i=0;i<serviceOrders.length();i++) {
            try{

                ContentValues values = new ContentValues();

                JSONObject obj = serviceOrders.getJSONObject(i);

                values.put(SterixContract.ServiceOrder._ID,obj.getString("service_order_ID"));
                values.put(SterixContract.ServiceOrder.COLUMN_SERVICE_TYPE,obj.getString("service_type_name"));
                values.put(SterixContract.ServiceOrder.COLUMN_LOCATION,obj.getString("client_location_name"));
                values.put(SterixContract.ServiceOrder.COLUMN_START_DATE,obj.getString("start_date"));
                values.put(SterixContract.ServiceOrder.COLUMN_START_TIME,obj.getString("start_time"));
                values.put(SterixContract.ServiceOrder.COLUMN_END_DATE,obj.getString("end_date"));
                values.put(SterixContract.ServiceOrder.COLUMN_END_TIME,obj.getString("end_time"));
                values.put(SterixContract.ServiceOrder.COLUMN_STATUS,obj.getString("service_order_status_type_ID"));

                database.insert(SterixContract.ServiceOrder.TABLE_NAME, null, values);

            }catch(Exception e){}

        }

        database.close();

    }

    public void insertServiceOrderTasks(JSONArray serviceOrderTasks){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
//
        database.execSQL("delete from "+SterixContract.ServiceOrderTask.TABLE_NAME);

        for(int i=0;i<serviceOrderTasks.length();i++) {
            try{

                ContentValues values = new ContentValues();

                JSONObject obj = serviceOrderTasks.getJSONObject(i);

                values.put(SterixContract.ServiceOrderTask._ID,obj.getString("id"));
                values.put(SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID,obj.getString("service_order_ID"));
                values.put(SterixContract.ServiceOrderTask.COLUMN_TASK,obj.getString("task"));
                values.put(SterixContract.ServiceOrderTask.COLUMN_START_TIME,obj.getString("start_time"));
                values.put(SterixContract.ServiceOrderTask.COLUMN_STATUS,obj.getString("status"));

                database.insert(SterixContract.ServiceOrderTask.TABLE_NAME, null, values);

            }catch(Exception e){}

        }

        database.close();

//        Log.d("Service order tasks",serviceOrderTasks.toString());
    }

    public void insertServiceOrderAreas(JSONArray serviceOrderAreas){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        database.execSQL("delete from "+SterixContract.ServiceOrderArea.TABLE_NAME);

        for(int i=0;i<serviceOrderAreas.length();i++) {
            try{
                ContentValues values = new ContentValues();

                JSONObject obj = serviceOrderAreas.getJSONObject(i);

                values.put(SterixContract.ServiceOrderArea.COLUMN_SERVICE_ORDER_ID,obj.getString("service_order_ID"));
                values.put(SterixContract.ServiceOrderArea.COLUMN_CLIENT_LOCATION_AREA_ID,obj.getString("client_location_area_ID"));
                values.put(SterixContract.ServiceOrderArea.COLUMN_CLIENT_LOCATION_AREA,obj.getString("client_location_area_name"));
                values.put(SterixContract.ServiceOrderArea.COLUMN_STATUS,obj.getString("status"));

//                Log.d("HEY",obj.getString("client_location_area_name"));

                database.insert(SterixContract.ServiceOrderArea.TABLE_NAME, null, values);

            }catch(Exception e){}

        }

        database.close();

//        Log.d("Service order tasks",serviceOrderTasks.toString());
    }

    public void insertDeviceMonitoring(JSONArray deviceMonitoring){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        database.execSQL("delete from "+SterixContract.DeviceMonitoring.TABLE_NAME);

        for(int i=0;i<deviceMonitoring.length();i++) {
            try{
                ContentValues values = new ContentValues();

                JSONObject obj = deviceMonitoring.getJSONObject(i);

                values.put(SterixContract.DeviceMonitoring.COLUMN_SERVICE_ORDER_ID,obj.getString("service_order_ID"));
                values.put(SterixContract.DeviceMonitoring.COLUMN_CLIENT_LOCATION_AREA_ID,obj.getString("client_location_area_ID"));
                values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CODE,obj.getString("device_code"));
                values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION_ID,obj.getString("device_condition_ID"));
                values.put(SterixContract.DeviceMonitoring.COLUMN_DEVICE_CONDITION,obj.getString("device_condition"));
                values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY_ID,obj.getString("activity_ID"));
                values.put(SterixContract.DeviceMonitoring.COLUMN_ACTIVITY,obj.getString("activity"));
                values.put(SterixContract.DeviceMonitoring.COLUMN_IMAGE,obj.getString("photo"));
                values.put(SterixContract.DeviceMonitoring.COLUMN_NOTES,obj.getString("notes"));
                values.put(SterixContract.DeviceMonitoring.COLUMN_TIMESTAMP,"timestamp");

//                Log.d("IDM",obj.toString());
                database.insert(SterixContract.DeviceMonitoring.TABLE_NAME, null, values);

            }catch(Exception e){}

        }
        database.close();

//        Log.d("Service order tasks",serviceOrderTasks.toString());
    }

    public void insertDeviceMonitoringPest(JSONArray deviceMonitoringPest){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

//        database.execSQL("delete from "+SterixContract.DeviceMonitoringPest.TABLE_NAME);

        for(int i=0;i<deviceMonitoringPest.length();i++) {
            try{
                ContentValues values = new ContentValues();

                JSONObject obj = deviceMonitoringPest.getJSONObject(i);

                values.put(SterixContract.DeviceMonitoringPest.COLUMN_SERVICE_ORDER_ID,obj.getString("service_order_ID"));
                values.put(SterixContract.DeviceMonitoringPest.COLUMN_DEVICE_MONITORING_ID,obj.getString("device_monitoring_ID"));
                values.put(SterixContract.DeviceMonitoringPest.COLUMN_DEVICE_CODE,obj.getString("device_code"));
                values.put(SterixContract.DeviceMonitoringPest.COLUMN_PEST_ID,obj.getString("pest_id"));
                values.put(SterixContract.DeviceMonitoringPest.COLUMN_NUMBER,obj.getString("number"));

                database.insert(SterixContract.DeviceMonitoringPest.TABLE_NAME, null, values);

            }catch(Exception e){}

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
//                Log.d("HEY",date);
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

    public JSONArray processDeviceMonitoringPestQueue(String queue_number, SQLiteDatabase db){

                                                    //String queue_number
        JSONArray out = new JSONArray();




        String[] projection = {
                SterixContract.DeviceMonitoringPestQueue.COLUMN_DEVICE_CODE,
                SterixContract.DeviceMonitoringPestQueue.COLUMN_PEST_ID,
                SterixContract.DeviceMonitoringPestQueue.COLUMN_NUMBER

        };

        String selection = SterixContract.DeviceMonitoringPestQueue.COLUMN_QUEUE_NUMBER+" = ?";
        String [] selectionValues = new String[]{queue_number};
        String sortOrder = SterixContract.DeviceMonitoringPestQueue._ID +" ASC";

        Cursor cursor = db.query(
                SterixContract.DeviceMonitoringPestQueue.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionValues,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                // The sort order
        );



        while(cursor.moveToNext()){


//            JSONArray devicePests = new JSONArray();
            JSONObject devicePest = new JSONObject();

//            String dcode = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringPestQueue.COLUMN_DEVICE_CODE));
            String pest_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringPestQueue.COLUMN_PEST_ID));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringPestQueue.COLUMN_NUMBER));


            try {

                devicePest.put("pest_ID",pest_id);
                devicePest.put("number",number);

                out.put(devicePest);

            }catch (Exception e){};
        }


        return out;

    }

    public String processDeviceMonitoringQueue(){

        JSONArray out = new JSONArray();

//        out.put("pests",processDeviceMonitoringPestQueue().toString());

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.DeviceMonitoringQueue.COLUMN_SERVICE_ORDER_ID,
                SterixContract.DeviceMonitoringQueue.COLUMN_CLIENT_LOCATION_AREA_ID,
                SterixContract.DeviceMonitoringQueue.COLUMN_DEVICE_CODE,
                SterixContract.DeviceMonitoringQueue.COLUMN_DEVICE_CONDITION_ID,
                SterixContract.DeviceMonitoringQueue.COLUMN_ACTIVITY_ID,
                SterixContract.DeviceMonitoringQueue.COLUMN_TIMESTAMP,
                SterixContract.DeviceMonitoringQueue.COLUMN_IMAGE,
                SterixContract.DeviceMonitoringQueue.COLUMN_NOTES,
                SterixContract.DeviceMonitoringQueue.COLUMN_QUEUE_NUMBER
        };

        String sortOrder =  SterixContract.DeviceMonitoringQueue.COLUMN_TIMESTAMP +" ASC";

        Cursor cursor = database.query(
                SterixContract.DeviceMonitoringQueue.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                // The sort order
        );

        String service_order_id,client_location_area_id,device_code,device_condition_id,activity_id,timestamp,queue_number,image,notes;

        while(cursor.moveToNext()){


            service_order_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_SERVICE_ORDER_ID));
            client_location_area_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_CLIENT_LOCATION_AREA_ID));
            device_code = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_DEVICE_CODE));
            device_condition_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_DEVICE_CONDITION_ID));
            activity_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_ACTIVITY_ID));
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_TIMESTAMP));
            queue_number = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_QUEUE_NUMBER));
            image =  cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_IMAGE));
            notes =  cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_NOTES));

            JSONObject temp = new JSONObject();

            try {
                temp.put("service_order_id", service_order_id);
                temp.put("client_location_area_id", client_location_area_id);
                temp.put("device_code", device_code);
                temp.put("device_condition_id", device_condition_id);
                temp.put("activity_id", activity_id);
                temp.put("timestamp", timestamp);
                temp.put("image", image);
                temp.put("notes", notes);
                // There will be redundant queries in the server
                // processDeviceMonitoringPestQueue(queue_number)
                temp.put("pests", processDeviceMonitoringPestQueue(queue_number,database).toString());
                out.put(temp);
            }
            catch (Exception e){};

        }

        database.close();

        return out.toString();
    }

    public String processAreaMonitoringQueue(){

        JSONArray out = new JSONArray();

//        out.put("pests",processDeviceMonitoringPestQueue().toString());

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.AreaFindingQueue.COLUMN_SERVICE_ORDER_ID,
                SterixContract.AreaFindingQueue.COLUMN_CLIENT_LOCATION_AREA_ID,
                SterixContract.AreaFindingQueue.COLUMN_FINDINGS,
                SterixContract.AreaFindingQueue.COLUMN_RECOMMENDATIONS,
                SterixContract.AreaFindingQueue.COLUMN_TIMESTAMP,
        };
        String sortOrder = SterixContract.AreaFindingQueue._ID+" ASC";
        Cursor cursor = database.query(
                SterixContract.AreaFindingQueue.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                // The sort order
        );

        String service_order_id,client_location_area_id,findings,recommendations,timestamp;

        while(cursor.moveToNext()){

            service_order_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFindingQueue.COLUMN_SERVICE_ORDER_ID));
            client_location_area_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFindingQueue.COLUMN_CLIENT_LOCATION_AREA_ID));
            findings = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFindingQueue.COLUMN_FINDINGS));
            recommendations = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFindingQueue.COLUMN_RECOMMENDATIONS));
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFindingQueue.COLUMN_TIMESTAMP));


            JSONObject temp = new JSONObject();

            try {
                temp.put("service_order_id", service_order_id);
                temp.put("client_location_area_id", client_location_area_id);
                temp.put("findings", findings);
                temp.put("proposed_action", recommendations);
                temp.put("timestamp", timestamp);
                // There will be redundant queries in the server
                // processDeviceMonitoringPestQueue(queue_number)
//                temp.put("pests", processDeviceMonitoringPestQueue(queue_number,database).toString());
                out.put(temp);
            }

            catch (Exception e){};

        }

        database.close();

        return out.toString();
    }

    public String processAreaMonitoringPestQueue(){

        JSONArray out = new JSONArray();

//        out.put("pests",processDeviceMonitoringPestQueue().toString());

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.AreaMonitoringPestQueue.COLUMN_SERVICE_ORDER_ID,
                SterixContract.AreaMonitoringPestQueue.COLUMN_SERVICE_ORDER_AREA_ID,
                SterixContract.AreaMonitoringPestQueue.COLUMN_PEST_ID,
                SterixContract.AreaMonitoringPestQueue.COLUMN_NUMBER,

        };
        String sortOrder = SterixContract.AreaMonitoringPestQueue._ID +" ASC";
        Cursor cursor = database.query(
                SterixContract.AreaMonitoringPestQueue.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                // The sort order
        );

        String service_order_id,service_order_area_id,pest_id,number;

        while(cursor.moveToNext()){

            service_order_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringPestQueue.COLUMN_SERVICE_ORDER_ID));
            service_order_area_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringPestQueue.COLUMN_SERVICE_ORDER_AREA_ID));
            pest_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringPestQueue.COLUMN_PEST_ID));
            number = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringPestQueue.COLUMN_NUMBER));


            JSONObject temp = new JSONObject();

            try {
                temp.put("service_order_id", service_order_id);
                temp.put("service_order_area_id", service_order_area_id);
                temp.put("pest_id", pest_id);
                temp.put("number", number);

                out.put(temp);
            }

            catch (Exception e){};

        }

        database.close();

        return out.toString();
    }

    public void insertAreaMonitoring(JSONArray areaMonitoring){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        database.execSQL("delete from "+SterixContract.AreaFinding.TABLE_NAME);

        for(int i=0;i<areaMonitoring.length();i++) {
            try{
                ContentValues values = new ContentValues();

                JSONObject obj = areaMonitoring.getJSONObject(i);
//                Log.d("AREA_MONITORING",obj.toString());

                values.put(SterixContract.AreaFinding.COLUMN_SERVICE_ORDER_ID,obj.getString("service_order_ID"));
                values.put(SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_AREA_ID,obj.getString("client_location_area_ID"));
                values.put(SterixContract.AreaFinding.COLUMN_FINDINGS,obj.getString("findings"));
                values.put(SterixContract.AreaFinding.COLUMN_RECOMMENDATIONS,obj.getString("proposed_action"));
                values.put(SterixContract.AreaFinding.COLUMN_TIMESTAMP,obj.getString("timestamp"));

                database.insert(SterixContract.AreaFinding.TABLE_NAME, null, values);

            }catch(Exception e){}

        }
        database.close();

//        Log.d("Service order tasks",serviceOrderTasks.toString());
    }

    public void insertAreaMonitoringPest(JSONArray areaMonitoringPest){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

//        Log.d("AREA_MONITORING",areaMonitoring.toString());
        database.execSQL("delete from "+SterixContract.AreaMonitoringPest.TABLE_NAME);

        for(int i=0;i<areaMonitoringPest.length();i++) {
            try{
                ContentValues values = new ContentValues();

                JSONObject obj = areaMonitoringPest.getJSONObject(i);
//                Log.d("AREA_MONITORING",obj.toString());

                values.put(SterixContract.AreaMonitoringPest.COLUMN_SERVICE_ORDER_ID,obj.getString("service_order_ID"));
                values.put(SterixContract.AreaMonitoringPest.COLUMN_SERVICE_ORDER_AREA_ID,obj.getString("client_location_area_ID"));
                values.put(SterixContract.AreaMonitoringPest.COLUMN_PEST_ID,obj.getString("pest_ID"));
                values.put(SterixContract.AreaMonitoringPest.COLUMN_NUMBER,obj.getString("number"));

                database.insert(SterixContract.AreaMonitoringPest.TABLE_NAME, null, values);

            }catch(Exception e){}

        }
        database.close();

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


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void imageUpload(){
        Log.d("HI!","HI!");

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.DeviceMonitoring.COLUMN_IMAGE
        };

        Cursor cursor = database.query(
                SterixContract.DeviceMonitoring.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        String imgPath ="";

        while(cursor.moveToNext()){
            imgPath = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoring.COLUMN_IMAGE));
            if(!imgPath.equals(""))
                break;
        }

        Log.d("IMG",imgPath);
        database.close();

        encodeImagetoString(imgPath);
    }

    String encodedString;
//    RequestParams params = new RequestParams();

    public void encodeImagetoString(final String imgPath) {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {

                Bitmap bitmap;

                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
//                prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
//                params.put("image", encodedString);
                // Trigger Image upload
                triggerUpload(encodedString,imgPath);
                Log.d("IMG","Encoding Finished!");
            }
        }.execute(null, null, null);
    }

    public void triggerUpload(String encoded,String imgPath){

        HashMap<String,String> params = new HashMap<>();
        params.put("encoded",encoded);
        params.put("filename","try50.jpg");

        Log.d("IMG",imgPath.split("/")[9]);

//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="http://"+ip+"/SterixBackend/imageUpload.php";
//
//        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
////                        new ProcessResponseTask().execute(response);
//                        Log.d("IMG","Success");
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("Error: ", "WHY LISA, WHY?");
//                VolleyLog.e("Error: ", error.getMessage());
//            }
//        });
//
//        Log.d("IMG","Uploading");
//        queue.add(request_json);

    }




}
