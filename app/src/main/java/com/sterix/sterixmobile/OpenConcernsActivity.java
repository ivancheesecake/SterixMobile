package com.sterix.sterixmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class OpenConcernsActivity extends AppCompatActivity {

    public static ArrayList<Finding> findings;
    public static OpenConcernsAdapter findingAdapter;
    public static RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_concerns);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findings = getFindings();
        Log.d("OPEN FINDINGS",Integer.toString(findings.size()));



        recyclerView = (RecyclerView) findViewById(R.id.open_concerns_recycler_view);

        findingAdapter = new OpenConcernsAdapter(findings);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(findingAdapter);


        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                Finding finding = findings.get(position);

//                Log.d("Date",so.getDate());
//                Log.d("Location",so.getLocation());
//                Log.d("Order",so.getOrder());
//
//                SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPref.edit();
//                editor.putString("LOCATION_ID", so.getLocation_id());
//                editor.commit();

                Context context = getApplicationContext();

                Log.d("POSITION",Integer.toString(position));

                Intent serviceOrdersIntent = new Intent(context, EditConcernActivity.class);
                serviceOrdersIntent.putExtra("SERVICE_ORDER_ID",finding.getServiceOrderId());
                serviceOrdersIntent.putExtra("SERVICE_ORDER_LOCATION", finding.getClient_location_area_name());
                serviceOrdersIntent.putExtra("POSITION",Integer.toString(position));
                serviceOrdersIntent.putExtra("FINDING",finding.getFinding());
                serviceOrdersIntent.putExtra("FINDING_ID",finding.getId());
                serviceOrdersIntent.putExtra("PROPOSED_ACTION",finding.getProposedAction());
                serviceOrdersIntent.putExtra("ACTIONS_TAKEN",finding.getActionTaken());
                serviceOrdersIntent.putExtra("PERSON_IN_CHARGE",finding.getPersonInCharge());
                serviceOrdersIntent.putExtra("RISK_ASSESSMENT",finding.getRiskAssessment());
                serviceOrdersIntent.putExtra("STATUS",finding.getStatus());
                serviceOrdersIntent.putExtra("ACTIONTAKEN_BY_CLIENT",finding.getActiontaken_by_client());
                startActivity(serviceOrdersIntent);



            }
        });




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
                SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_AREA_NAME,
                SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_NAME,
                SterixContract.AreaFinding.COLUMN_MARK_AS_DONE,
                SterixContract.AreaFinding.COLUMN_TIMESTAMP,
                SterixContract.AreaFinding.COLUMN_ACTIONTAKEN_BY_CLIENT,
        };

        String selection = SterixContract.AreaFinding.COLUMN_STATUS +" = 'Open'";
        String selectionArgs[] = {};
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

            String client_location_area_name = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_AREA_NAME));
            String client_location_name = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_NAME));
            String mark_as_done = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_MARK_AS_DONE));
            String actiontaken_by_client = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_ACTIONTAKEN_BY_CLIENT));

            Log.d("FINDING",finding);
            Finding findingObj = new Finding(id,image,finding,proposedAction,actionTaken,personInCharge,riskAssessment,status,timestamp,serviceOrderId,areaId,client_location_area_name,client_location_name,mark_as_done,actiontaken_by_client);
            results.add(findingObj);

        }

        return results;

    }



    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



}
