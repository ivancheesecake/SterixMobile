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
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class SyncActivity extends AppCompatActivity {

    public String ip,uploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        String userId = sharedPref.getString("USER_ID", "");
        ip = sharedPref.getString("IP", "");
        uploaded = sharedPref.getString("DM_IMAGES_UPLOADED","");

        ProgressBar progress;
        progress = (ProgressBar) findViewById(R.id.progress2);
        progress.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        if(isOnline()) {
//          Complicated Way
//          syncData(userId);

//          EZ Way?

            Intent a = new Intent(this, ServiceOrdersActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);


        }
        else{
            Toast t = Toast.makeText(this,"You are offline. Please connect to the internet to sync data with the server.",Toast.LENGTH_LONG);
            t.show();
            finish();
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public void syncData(String user_id){

        final SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",user_id);

        // Process Task Updates QUEUE
        // Cheap string implementation, will I transition to SQL? IDK.
//        Log.d("TASK_UPDATES",sharedPref.getString("TASK_UPDATES",""));
//        Log.d("MAHIRAP","SYNCING DEEP");
//        Log.d("MAHIRAP","device monitoring queue "+processDeviceMonitoringQueue().toString());
        params.put("task_updates",sharedPref.getString("TASK_UPDATES",""));
        params.put("urgent_task_updates",processServiceOrdersTaskQueue());
        params.put("device_monitoring_updates",processDeviceMonitoringQueue());
        params.put("area_monitoring_updates",processAreaMonitoringQueue());
        params.put("area_monitoring_pest_updates",processAreaMonitoringPestQueue());

        Log.d("URGENT",processServiceOrdersTaskQueue().toString());

        // Process existing images
        params.put("existing_images",processExistingImages());

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+ip+"/SterixBackend/sync2.php";

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new SyncActivity.ProcessResponseTask().execute(response);
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
            Log.d("Working","True");
            JSONObject response = params[0];
            try {

//                Log.d("TAGHIRAP",response.get("inloop").toString());
//                Log.d("TAGHIRAP",response.get("if").toString());
                insertServiceOrders(new JSONArray(response.get("service_orders").toString()));

                insertServiceOrderTasks(new JSONArray(response.get("service_order_tasks").toString()));
                insertServiceOrderAreas(new JSONArray(response.get("service_order_areas").toString()));

                insertDeviceMonitoring(new JSONArray(response.get("device_monitoring").toString()));
                insertDeviceMonitoringPest(new JSONArray(response.get("device_monitoring_pest").toString()));

//                            Log.d("AREA_MONITORING",response.get("area_monitoring").toString());
                insertAreaMonitoring(new JSONArray(response.get("area_monitoring").toString()));
                insertAreaMonitoringPest(new JSONArray(response.get("area_monitoring_pest").toString()));

                // Empty update queues
                SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("TASK_UPDATES","");
                editor.commit();

                // EMPTY QUEUES, bakit mo nalimutan to?
                SQLiteDatabase database = new SterixDBHelper(getApplicationContext()).getWritableDatabase();
                database.execSQL("delete from "+SterixContract.ServiceOrderTaskQueue.TABLE_NAME);
                database.execSQL("delete from "+SterixContract.DeviceMonitoringQueue.TABLE_NAME);
                database.execSQL("delete from "+SterixContract.DeviceMonitoringPestQueue.TABLE_NAME);
                database.execSQL("delete from "+SterixContract.AreaFindingQueue.TABLE_NAME);
                database.execSQL("delete from "+SterixContract.AreaMonitoringPestQueue.TABLE_NAME);
                database.close();



                // Decode Base64 Images
                decodeImages(new JSONArray(response.get("images").toString()));


            } catch (JSONException e) {
                Log.d("HEY","NO SERVICE ORDERS WERE FOUND.");
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
//            progressWrapper.setVisibility(View.GONE);
//            prepareServiceOrders();
            Toast t = Toast.makeText(getApplicationContext(),"Sync completed.",Toast.LENGTH_LONG);
            t.show();

            finish();

        }

//        public void execute(JSONObject response) {
//        }
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
            image =  cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_IMAGE));
            notes =  cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_NOTES));
            queue_number = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.DeviceMonitoringQueue.COLUMN_QUEUE_NUMBER));


            JSONObject temp = new JSONObject();

            try {
                Log.d("MAHIRAP","In Loop");
                Log.d("MAHIRAP","device_code " +device_code);
                Log.d("MAHIRAP","device_condition_id " +device_condition_id);
                Log.d("MAHIRAP","activity_id " +activity_id);

                temp.put("service_order_id", service_order_id);
                temp.put("client_location_area_id", client_location_area_id);
                temp.put("device_code", device_code);
                temp.put("device_condition_id", device_condition_id);
                temp.put("activity_id", activity_id);
                temp.put("timestamp", timestamp);
                temp.put("notes", notes);
                temp.put("pests", processDeviceMonitoringPestQueue(queue_number,database).toString());

                Log.d("MAHIRAP","temp "+temp.get("device_condition_id"));
                out.put(temp);

                if(image!=null) {
                    temp.put("image", image.split("/")[9]);
                    Log.d("QUEUE","NANDITO");
                }
                else {
                    temp.put("image", "");
                    image="";
                    Log.d("QUEUE","NAROON");
                }




                //Upload photo
                uploadPhoto(image);

                // There will be redundant queries in the server
                // processDeviceMonitoringPestQueue(queue_number)
            }
            catch (Exception e){
                Log.d("QUEUE",e.getMessage());
                Log.d("QUEUE","OUTSIDE");
            }

        }

        database.close();
        Log.d("MAHIRAP","out "+out.toString());


        return out.toString();
    }

    public String processServiceOrdersTaskQueue(){

        JSONArray out = new JSONArray();

//        out.put("pests",processDeviceMonitoringPestQueue().toString());

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.ServiceOrderTaskQueue.COLUMN_SERVICE_ORDER_ID,
                SterixContract.ServiceOrderTaskQueue.COLUMN_TASK,
                SterixContract.ServiceOrderTaskQueue.COLUMN_START_TIME,
                SterixContract.ServiceOrderTaskQueue.COLUMN_END_TIME,
                SterixContract.ServiceOrderTaskQueue.COLUMN_STATUS
        };
        String sortOrder = SterixContract.AreaMonitoringPestQueue._ID +" ASC";
        Cursor cursor = database.query(
                SterixContract.ServiceOrderTaskQueue.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                // The sort order
        );

        String service_order_id,task,start_time,end_time,status;

        while(cursor.moveToNext()){

            service_order_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderTaskQueue.COLUMN_SERVICE_ORDER_ID));
            task = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderTaskQueue.COLUMN_TASK));
            start_time = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderTaskQueue.COLUMN_START_TIME));
            end_time = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderTaskQueue.COLUMN_END_TIME));
            status = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.ServiceOrderTaskQueue.COLUMN_STATUS));


            JSONObject temp = new JSONObject();

            try {
                temp.put("service_order_ID", service_order_id);
                temp.put("task", task);
                temp.put("start_time", start_time);
                temp.put("end_time", end_time);
                temp.put("status", status);
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
//                SterixContract.AreaFindingQueue.COLUMN_RECOMMENDATIONS,
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
//            recommendations = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFindingQueue.COLUMN_RECOMMENDATIONS));
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFindingQueue.COLUMN_TIMESTAMP));


            JSONObject temp = new JSONObject();

            try {
                temp.put("service_order_id", service_order_id);
                temp.put("client_location_area_id", client_location_area_id);
                temp.put("findings", findings);
//                temp.put("proposed_action", recommendations);
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

    public void uploadPhoto(String imgPath){
        Log.d("HI!","HI!");

        Log.d("Uploaded",uploaded);
        if(imgPath.compareTo("")!=0) {
            String filename = imgPath.split("/")[9];

            if (!uploaded.contains(filename)) {
                Log.d("Uploaded", "HINDI PA NAAUPLOAD ITO");
                encodeImagetoString(imgPath);
            } else {
                Log.d("Uploaded", "UPLOADED NA");

            }
        }
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
                triggerUpload(encodedString,imgPath);
                Log.d("IMG","Encoding Finished!");
            }
        }.execute(null, null, null);
    }

    String filenameShort = "";

    public void triggerUpload(String encoded, final String filename){

        filenameShort = filename.split("/")[9];

        HashMap<String,String> params = new HashMap<>();
        params.put("encoded",encoded);
        params.put("filename",filenameShort);
        //      JWT Code, COPY THIS EVERYWHERE!!!!!

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("JWT", "");
        params.put("jwt",jwt);



        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+ip+"/SterixBackend/imageUpload.php";

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("IMG","Success");
                        uploaded += filenameShort +",";
                        Log.d("UPLOADED_UPDATE",uploaded);


                        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("DM_IMAGES_UPLOADED",uploaded);
                        editor.commit();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error: ", "WHY LISA, WHY?");
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        Log.d("IMG","Uploading");
        queue.add(request_json);

//        Toast t = Toast.makeText(getr);
    }

    public String processExistingImages(){

        String existingImages ="\"NONE\",";
        String storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File f = new File(storageDir);
        File files[] = f.listFiles();

        for(int i=0; i<files.length; i++){
            Log.d("EXISTING",files[i].getName());
            existingImages += '"'+files[i].getName()+'"'+",";
        }
        Log.d("EXISTIING",existingImages.substring(0,existingImages.length()-1));

        return existingImages.substring(0,existingImages.length()-1);
    }

    public void decodeImages(JSONArray images){

        for(int i=0; i<images.length(); i++){
            try{

                JSONObject obj = images.getJSONObject(i);
                Log.d("DECODING",obj.get("filename").toString());

                FileOutputStream fos = null;
                BufferedOutputStream bos = null;

                String base64ImageData = obj.get("encoded").toString();
                Log.d("DECODING",getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+"/"+obj.get("filename").toString());

                try {
                    if (base64ImageData != null) {
//                    fos = getApplicationContext().openFileOutput(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+"/"+obj.get("filename").toString(), Context.MODE_PRIVATE);
                        bos = new BufferedOutputStream(new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+"/"+obj.get("filename").toString()));
                        byte[] imageAsBytes = Base64.decode(base64ImageData.getBytes(), 0);
                        bos.write(imageAsBytes);
                        bos.flush();
                        bos.close();
                    }

                } catch (Exception e) {

                    Log.d("DECODING","FAIL");

                } finally {
                    if (bos != null) {
                        bos = null;
                    }
                }

            }catch (Exception e){}
        }

    }


/*
* INSEEERTS
*
* */

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
                String photo = obj.getString("photo");
//                Log.d("PATH",getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/files/Photos/"+photo);
                if(!photo.equals(""))
                    values.put(SterixContract.DeviceMonitoring.COLUMN_IMAGE,getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+photo);
                else
                    values.put(SterixContract.DeviceMonitoring.COLUMN_IMAGE,photo);
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
                values.put(SterixContract.AreaFinding.COLUMN_PROPOSED_ACTION,obj.getString("proposed_action"));
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

}
