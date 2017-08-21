package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AreaViewFindingActivity extends AppCompatActivity {

    Monitoring m;
    String service_order_location;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_view_finding);

        Intent i = getIntent();
        m =  i.getParcelableExtra("AREA_MONITORING_PARCEL");
        TextView tv_location = (TextView) findViewById(R.id.area_view_finding_location);
        tv_location.setText(m.getLocation());
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(service_order_location);

        recyclerView = (RecyclerView) findViewById(R.id.view_finding_recyvlerview);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FindingAdapter(getFindings());
        recyclerView.setAdapter(mAdapter);


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

    private ArrayList<Finding> getFindings(){

        ArrayList<Finding> results = new ArrayList<>();

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.AreaFinding._ID,
                SterixContract.AreaFinding.COLUMN_SERVICE_ORDER_ID,
                SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_AREA_ID,
                SterixContract.AreaFinding.COLUMN_IMAGE,
                SterixContract.AreaFinding.COLUMN_FINDINGS,
                SterixContract.AreaFinding.COLUMN_RECOMMENDATIONS,
                SterixContract.AreaFinding.COLUMN_TIMESTAMP
        };

        String selection = SterixContract.AreaFinding.COLUMN_SERVICE_ORDER_ID +"= ? and " +SterixContract.AreaFinding.COLUMN_CLIENT_LOCATION_AREA_ID +"= ? ";
        String selectionArgs[] = {m.getService_order_id(),m.getLocation_area_id()};
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

            String image = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_IMAGE));
            String finding = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_FINDINGS));
            String recommendation = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaFinding.COLUMN_RECOMMENDATIONS));

            Finding findingObj = new Finding(image,finding,recommendation);
            results.add(findingObj);
        }

        return results;

    }

}
