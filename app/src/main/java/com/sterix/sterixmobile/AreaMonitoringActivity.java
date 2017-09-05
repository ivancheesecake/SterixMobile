package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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

import java.util.HashMap;

public class AreaMonitoringActivity extends AppCompatActivity {

    Monitoring m;
    String service_order_location;
    String ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_monitoring);

        Intent i = getIntent();
        m =  i.getParcelableExtra("AREA_MONITORING_PARCEL");
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        TextView tv_location = (TextView) findViewById(R.id.area_monitoring_location);
        tv_location.setText(m.getLocation());

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        ip = sharedPref.getString("IP", "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(service_order_location);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        populatePests();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:

                // Remove all sharedpreferences
                SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();

                // Destroy all previous activities and go back to login screen
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                Toast t = Toast.makeText(getApplicationContext(),"Successfully logged out!",Toast.LENGTH_SHORT);
                t.show();

                // Destroy current activity
                finish();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }


    public void addFindings(View v){

        Log.d("D","Add Findings");
        Intent intent = new Intent(this, AreaAddFindingActivity.class);
        intent.putExtra("AREA_MONITORING_PARCEL",m);
        intent.putExtra("SERVICE_ORDER_LOCATION",service_order_location);
        startActivity(intent);

    }

    public void viewFindings(View v){

        Log.d("D","View Findings");
        Intent intent = new Intent(this, AreaViewFindingActivity.class);
        intent.putExtra("AREA_MONITORING_PARCEL",m);
        intent.putExtra("SERVICE_ORDER_LOCATION",service_order_location);
        startActivity(intent);

    }

    public void saveAreaMonitoring(View v){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values;

        String ant,ar,bf,bi,cb,df,ff,fm,gr,hf,hm,liz,mos,nr,pf,rfb,rr,sp,oth;

        EditText et = (EditText) findViewById(R.id.area_ar);
        ar = et.getText().toString();

        et = (EditText) findViewById(R.id.area_gr);
        gr = et.getText().toString();

        et = (EditText) findViewById(R.id.area_ant);
        ant = et.getText().toString();

        et = (EditText) findViewById(R.id.area_mos);
        mos = et.getText().toString();

        et = (EditText) findViewById(R.id.area_hf);
        hf = et.getText().toString();

        et = (EditText) findViewById(R.id.area_bf);
        bf = et.getText().toString();

        et = (EditText) findViewById(R.id.area_df);
        df = et.getText().toString();

        et = (EditText) findViewById(R.id.area_pf);
        pf = et.getText().toString();

        et = (EditText) findViewById(R.id.area_ff);
        ff = et.getText().toString();

        et = (EditText) findViewById(R.id.area_fm);
        fm = et.getText().toString();

        et = (EditText) findViewById(R.id.area_hm);
        hm = et.getText().toString();

        et = (EditText) findViewById(R.id.area_rr);
        rr = et.getText().toString();

        et = (EditText) findViewById(R.id.area_nr);
        nr = et.getText().toString();

        et = (EditText) findViewById(R.id.area_cb);
        cb = et.getText().toString();

        et = (EditText) findViewById(R.id.area_rfb);
        rfb = et.getText().toString();

        et = (EditText) findViewById(R.id.area_liz);
        liz = et.getText().toString();

        et = (EditText) findViewById(R.id.area_sp);
        sp = et.getText().toString();

        et = (EditText) findViewById(R.id.area_bi);
        bi = et.getText().toString();

        et = (EditText) findViewById(R.id.area_oth);
        oth = et.getText().toString();

        String[] pestNumbers = {ar,gr,ant,mos,hf,bf,df,pf,ff,fm,hm,rr,nr,cb,rfb,liz,sp,bi,oth};

        for(int i=0; i<pestNumbers.length; i++){

            if(!pestNumbers[i].equals("")){

                // Insert!
                values = new ContentValues();
                values.put(SterixContract.AreaMonitoringPest.COLUMN_SERVICE_ORDER_ID,m.getService_order_id());
                values.put(SterixContract.AreaMonitoringPest.COLUMN_SERVICE_ORDER_AREA_ID,m.getLocation_area_id());
                values.put(SterixContract.AreaMonitoringPest.COLUMN_PEST_ID,i+"");
                values.put(SterixContract.AreaMonitoringPest.COLUMN_NUMBER,pestNumbers[i]);
                database.insert(SterixContract.AreaMonitoringPest.TABLE_NAME, null, values);

                if(isOnline()){
                    // One day, minimize number of requests, this works for now
                    insertAreaMonitoringPestToServer(m.getService_order_id(),m.getLocation_area_id(),i+"",pestNumbers[i]);
                }
                else{
                    database.insert(SterixContract.AreaMonitoringPestQueue.TABLE_NAME, null, values);
                }

            }
        }

        database.close();

        Toast t = Toast.makeText(getApplicationContext(),"Area updated.",Toast.LENGTH_SHORT);
        t.show();
    }

    public void populatePests(){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.AreaMonitoringPest._ID,
                SterixContract.AreaMonitoringPest.COLUMN_SERVICE_ORDER_ID,
                SterixContract.AreaMonitoringPest.COLUMN_SERVICE_ORDER_AREA_ID,
                SterixContract.AreaMonitoringPest.COLUMN_PEST_ID,
                SterixContract.AreaMonitoringPest.COLUMN_NUMBER
        };


        String selection = SterixContract.AreaMonitoringPest.COLUMN_SERVICE_ORDER_ID +" = ? and "+ SterixContract.AreaMonitoringPest.COLUMN_SERVICE_ORDER_AREA_ID +" = ?";
        String selectionArgs[] = {m.getService_order_id(),m.getLocation_area_id()};
        String sortOrder = SterixContract.AreaMonitoringPest._ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.AreaMonitoringPest.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        HashMap <String, Integer> pestResource = new HashMap<>();

        pestResource.put("0",R.id.area_ar);
        pestResource.put("1",R.id.area_gr);
        pestResource.put("2",R.id.area_ant);
        pestResource.put("3",R.id.area_mos);
        pestResource.put("4",R.id.area_hf);
        pestResource.put("5",R.id.area_bf);
        pestResource.put("6",R.id.area_df);
        pestResource.put("7",R.id.area_pf);
        pestResource.put("8",R.id.area_ff);
        pestResource.put("9",R.id.area_fm);
        pestResource.put("10",R.id.area_hm);
        pestResource.put("11",R.id.area_rr);
        pestResource.put("12",R.id.area_nr);
        pestResource.put("13",R.id.area_cb);
        pestResource.put("14",R.id.area_rfb);
        pestResource.put("15",R.id.area_liz);
        pestResource.put("16",R.id.area_sp);
        pestResource.put("17",R.id.area_bi);
        pestResource.put("18",R.id.area_oth);


        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringPest._ID));
            String pest_id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringPest.COLUMN_PEST_ID));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringPest.COLUMN_NUMBER));

            EditText et = (EditText) findViewById(pestResource.get(pest_id));
            et.setText(number);
        }

        database.close();

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void insertAreaMonitoringPestToServer(String service_order_id, String client_location_area_ID,String pest_id, String number){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://"+ip+"/SterixBackend/insertAreaMonitoringPest.php";

        HashMap params = new HashMap<String,String>();
        params.put("service_order_id",service_order_id);
        params.put("client_location_area_id",client_location_area_ID);
        params.put("pest_id",pest_id);
        params.put("number",number);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.get("success").toString().equals("true")) {
                                Toast toast = Toast.makeText(getApplicationContext(),"Area monitoring pest data for area "+service_order_location+" was successfully updated in the server!", Toast.LENGTH_SHORT);
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
