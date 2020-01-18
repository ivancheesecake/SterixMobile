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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FindingsSummaryActivity extends AppCompatActivity {

    Monitoring m;
    String service_order_id,service_order_location;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    public static RecyclerView.Adapter mAdapter;
    public static ArrayList<String> photoPaths;
    public static ArrayList<Finding> findings;
    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findings_summary);

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        ip = sharedPref.getString("IP", "");

        Intent i = getIntent();

        service_order_id = i.getStringExtra("SERVICE_ORDER_ID");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//        toolbar.setSubtitle(service_order_location);

        recyclerView = (RecyclerView) findViewById(R.id.view_finding_summary_recyclerview);
//
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
//
        findings = getFindings();
        mAdapter = new FindingSummaryAdapter(findings);
        recyclerView.setAdapter(mAdapter);
//
//        photoPaths = new ArrayList<>();


//


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
                Log.d("LOGOUT","BITCH!");

                // Remove all sharedpreferences
                SharedPreferences sharedPref = getSharedPreferences("sterix_prefs", Context.MODE_PRIVATE);
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

            case R.id.menu_sync:

                Intent syncIntent = new Intent(this.getApplicationContext(), SyncActivity.class);
                startActivity(syncIntent);
                return true;

            case R.id.menu_open_concerns:

                Intent openConcernsIntent = new Intent(this.getApplicationContext(), OpenConcernsActivity.class);
                startActivity(openConcernsIntent);
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    private ArrayList<Finding> getFindings(){
//        Log.d("FINDING","HERE");
//        Log.d("FINDING",m.getService_order_id());
//        Log.d("FINDING",m.getLocation_area_id());

        ArrayList<Finding> results = new ArrayList<>();

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.AreaFinding._ID,
                SterixContract.AreaFinding.COLUMN_SERVICE_ORDER_ID,
                SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_AREA_ID,
                SterixContract.AreaFinding.COLUMN_IMAGE,
                SterixContract.AreaFinding.COLUMN_FINDINGS,
                SterixContract.AreaFinding.COLUMN_PROPOSED_ACTION,
                SterixContract.AreaFinding.COLUMN_ACTION_TAKEN,
                SterixContract.AreaFinding.COLUMN_PERSON_IN_CHARGE,
                SterixContract.AreaFinding.COLUMN_RISK_ASSESSMENT,
                SterixContract.AreaFinding.COLUMN_STATUS,
                SterixContract.AreaFinding.COLUMN_TIMESTAMP
        };

        String selection = SterixContract.AreaFinding.COLUMN_SERVICE_ORDER_ID +"= ?";
        String selectionArgs[] = {service_order_id};
        String sortOrder = SterixContract.AreaFinding._ID +" ASC";

        Cursor cursor = database.query(
                SterixContract.AreaFinding.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(cursor.moveToNext()){

            //SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            //String month_name = month_date.format(date);

//            String date_formatted = "";

            String id = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding._ID));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_IMAGE));
            String finding = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_FINDINGS));
            String proposedAction = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_PROPOSED_ACTION));
            String actionTaken = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_ACTION_TAKEN));
            String personInCharge = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_PERSON_IN_CHARGE));
            String riskAssessment = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_RISK_ASSESSMENT));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_STATUS));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_TIMESTAMP));
            String serviceOrderId = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_SERVICE_ORDER_ID));
            String areaId = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_AREA_ID));


            Log.d("FINDING",finding);
            Finding findingObj = new Finding(id,image,finding,proposedAction,actionTaken,personInCharge,riskAssessment,status,timestamp,serviceOrderId,areaId);
            results.add(findingObj);

        }

        return results;

    }

    public void viewPhotos(View v){

        String tag = v.getTag().toString();
        String finding_id = v.getTag(R.id.finding_id).toString();

        photoPaths = new ArrayList<>();

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();


        Log.d("VIEW PHOTOS",tag);
        Log.d("VIEW PHOTOS",v.getTag(R.id.finding_id).toString());

        if(tag.equals("findings-view")){

            String[] projection = {
                    SterixContract.AreaMonitoringFindingPhotos.COLUMN_FILENAME,
            };

            String selection = SterixContract.AreaMonitoringFindingPhotos.COLUMN_AREA_MONITORING_ID +" = ?";
            String selectionArgs[] = {finding_id};
            String sortOrder = SterixContract.AreaMonitoringFindingPhotos._ID +" ASC";

            Cursor cursor = database.query(
                    SterixContract.AreaMonitoringFindingPhotos.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            while(cursor.moveToNext()){

                String filename = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringFindingPhotos.COLUMN_FILENAME));
                photoPaths.add(filename);

            }

        }
        else if(tag.equals("actions-view")){

            String[] projection = {
                    SterixContract.AreaMonitoringActionTakenPhotos.COLUMN_FILENAME,
            };

            String selection = SterixContract.AreaMonitoringActionTakenPhotos.COLUMN_AREA_MONITORING_ID +" = ?";
            String selectionArgs[] = {finding_id};
            String sortOrder = SterixContract.AreaMonitoringActionTakenPhotos._ID +" ASC";

            Cursor cursor = database.query(
                    SterixContract.AreaMonitoringActionTakenPhotos.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            while(cursor.moveToNext()){

                String filename = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringActionTakenPhotos.COLUMN_FILENAME));
                photoPaths.add(filename);

            }

        }

        Intent intent = new Intent(this, AreaViewFindingPhotosActivity.class);
        intent.putExtra("AREA_MONITORING_PARCEL",m);
        intent.putExtra("SERVICE_ORDER_LOCATION",service_order_location);
        intent.putExtra("TAG",tag);
        startActivity(intent);

    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
