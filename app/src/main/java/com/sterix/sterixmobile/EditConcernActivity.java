package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class EditConcernActivity extends AppCompatActivity {

    Bitmap imageBitmap;
    Bundle extras;
    String user_id,service_order_location, service_order_id,finding,proposedAction,actionsTaken,personInCharge,riskAssessment, status, finding_id,position,photosForDeletionStr,photosFindingsUpload,photosActionTakenUpload,actiontaken_by_client;
    public static String uploaded;
//    Monitoring m;
    Uri kwonURI;
    public static ArrayList<Uri> kwonURIDeul;
    public static ArrayList<String> findingPhotoPaths;
    public static ArrayList<String> actionPhotoPaths;
    public static ArrayList<String> newFindingPhotoPaths;
    public static ArrayList<String> newActionPhotoPaths;
    public static ArrayList<String> photoPaths;
    public static ArrayList<String> photosForDeletion;
    Spinner statusSpinner;
    Spinner riskAssessmentSpinner;
    HashMap<String,String> risks;
    HashMap<String,String> statusDeul;

    ArrayList<String> risksList;
    ArrayList<String> statusList;



    public String ip;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_concern);


        Intent i = getIntent();
//        m = i.getParcelableExtra("AREA_MONITORING_PARCEL");

        service_order_id = i.getStringExtra("SERVICE_ORDER_ID");
        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");

        position = i.getStringExtra("POSITION");

        Log.d("POSITION",position);

        finding = i.getStringExtra("FINDING");
        finding_id = i.getStringExtra("FINDING_ID");
        proposedAction = i.getStringExtra("PROPOSED_ACTION");
        actionsTaken = i.getStringExtra("ACTIONS_TAKEN");
        personInCharge = i.getStringExtra("PERSON_IN_CHARGE");
        riskAssessment = i.getStringExtra("RISK_ASSESSMENT");
        status = i.getStringExtra("STATUS");
        actiontaken_by_client = i.getStringExtra("ACTIONTAKEN_BY_CLIENT");


        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        ip = sharedPref.getString("IP", "");
        uploaded = sharedPref.getString("AM_IMAGES_UPLOADED","");
        user_id = sharedPref.getString("USER_ID","");

        EditText findingET = (EditText) findViewById(R.id.area_findings);
        EditText proposedActionsET = (EditText) findViewById(R.id.area_proposed_action);
        EditText actionsTakenET = (EditText) findViewById(R.id.area_findings_actions_taken);
        EditText personInChargeET = (EditText) findViewById(R.id.area_person_in_charge);
        TextView actiontakenByClientTV = (TextView) findViewById(R.id.area_add_finding_row15) ;


//        Spinner riskAssessmentSpinner = (Spinner) findViewById(R.id.area_risk_assessment);

        findingET.setText(finding);
        proposedActionsET.setText(proposedAction);
        actionsTakenET.setText(actionsTaken);
        personInChargeET.setText(personInCharge);
        actiontakenByClientTV.setText(actiontaken_by_client);

        findingPhotoPaths = loadFindingPhotos();
        actionPhotoPaths = loadActionTakenPhotos();
        newFindingPhotoPaths = new ArrayList<>();
        newActionPhotoPaths = new ArrayList<>();

        photosForDeletion = new ArrayList<>();
        photosForDeletionStr = "";
        photosFindingsUpload= "";
        photosActionTakenUpload = "";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(service_order_location);

        risks = new HashMap<String,String>();
        risks.put("Low","Low");
        risks.put("Critical","Critical");

        risksList = new ArrayList<>();
//        risksList.add("- Please select -");

        for (String key:risks.keySet()){
            risksList.add(key);
        }


        riskAssessmentSpinner  = (Spinner) findViewById(R.id.area_risk_assessment_edit);
        ArrayAdapter<String> riskAssessmentAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,risksList);
        riskAssessmentAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        riskAssessmentSpinner.setAdapter(riskAssessmentAdapter);

        if(riskAssessment.equals("Critical")){

            riskAssessmentSpinner.setSelection(0);
        }
        else{
            riskAssessmentSpinner.setSelection(1);
        }



        statusDeul = new HashMap<String,String>();
        statusDeul.put("Closed","Closed");
        statusDeul.put("Open","Open");



        ArrayList<String> statusList = new ArrayList<String>();

        for (String key:statusDeul.keySet()){
            statusList.add(key);
        }

        statusSpinner  = (Spinner) findViewById(R.id.area_status_spinner);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,statusList);
        statusAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        statusSpinner.setAdapter(statusAdapter);

        if(status.equals("Closed")){

            statusSpinner.setSelection(0);
        }
        else{
            statusSpinner.setSelection(1);
        }



        //        statusSpinner.setSelection();




//        riskAssessmentSpinner.set


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

    public ArrayList<String> loadFindingPhotos(){

        photoPaths = new ArrayList<>();

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

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

        return photoPaths;
    }

    public ArrayList<String> loadActionTakenPhotos(){

        photoPaths = new ArrayList<>();

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();

        String[] projection = {
                SterixContract.AreaMonitoringFindingPhotos.COLUMN_FILENAME,
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

        return photoPaths;
    }

    public void viewFindingPhotos(View v){

        String tag = v.getTag().toString();
//        String finding_id = v.getTag(R.id.finding_id).toString();


//        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();


//        Log.d("VIEW PHOTOS",tag);
//        Log.d("VIEW PHOTOS",v.getTag(R.id.finding_id).toString());

//        if(tag.equals("findings-edit")){
//
//            String[] projection = {
//                    SterixContract.AreaMonitoringFindingPhotos.COLUMN_FILENAME,
//            };
//
//            String selection = SterixContract.AreaMonitoringFindingPhotos.COLUMN_AREA_MONITORING_ID +" = ?";
//            String selectionArgs[] = {finding_id};
//            String sortOrder = SterixContract.AreaMonitoringFindingPhotos._ID +" ASC";
//
//            Cursor cursor = database.query(
//                    SterixContract.AreaMonitoringFindingPhotos.TABLE_NAME,                     // The table to query
//                    projection,                               // The columns to return
//                    selection,                                // The columns for the WHERE clause
//                    selectionArgs,                            // The values for the WHERE clause
//                    null,                                     // don't group the rows
//                    null,                                     // don't filter by row groups
//                    sortOrder                                 // The sort order
//            );
//
//            while(cursor.moveToNext()){
//
//                String filename = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringFindingPhotos.COLUMN_FILENAME));
//                photoPaths.add(filename);
//
//            }
//
//            photoPaths.addAll(findingPhotoPaths);



//        }
//        else if(tag.equals("actions-edit")){

//            String[] projection = {
//                    SterixContract.AreaMonitoringActionTakenPhotos.COLUMN_FILENAME,
//            };
//
//            String selection = SterixContract.AreaMonitoringActionTakenPhotos.COLUMN_AREA_MONITORING_ID +" = ?";
//            String selectionArgs[] = {finding_id};
//            String sortOrder = SterixContract.AreaMonitoringActionTakenPhotos._ID +" ASC";
//
//            Cursor cursor = database.query(
//                    SterixContract.AreaMonitoringActionTakenPhotos.TABLE_NAME,                     // The table to query
//                    projection,                               // The columns to return
//                    selection,                                // The columns for the WHERE clause
//                    selectionArgs,                            // The values for the WHERE clause
//                    null,                                     // don't group the rows
//                    null,                                     // don't filter by row groups
//                    sortOrder                                 // The sort order
//            );
//
//            while(cursor.moveToNext()){
//
//                String filename = cursor.getString(cursor.getColumnIndexOrThrow(SterixContract.AreaMonitoringActionTakenPhotos.COLUMN_FILENAME));
//                photoPaths.add(filename);
//
//            }
//
//            photoPaths.addAll(actionPhotoPaths);
//        }

        Intent intent = new Intent(this, OpenConcernViewPhotoActivity.class);
//        intent.putExtra("AREA_MONITORING_PARCEL",m);
        intent.putExtra("SERVICE_ORDER_LOCATION",service_order_location);
        intent.putExtra("TAG",tag);
        startActivity(intent);


    }



    static final int REQUEST_IMAGE_CAPTURE = 1;

//    TAKE PHOTO

    public void takePhoto(View v) {

        String tag = v.getTag().toString();

        Log.d("TAKE PHOTO",tag);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(tag);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                kwonURI = FileProvider.getUriForFile(this,
                        "com.sterix.sterixmobile.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, kwonURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                kwonURIDeul.add(kwonURI);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

//            ImageView thumb = (ImageView) findViewById(R.id.area_add_finding_row4);
////            extras = data.getExtras();
////            imageBitmap = (Bitmap) extras.get("data");
//
////            Bitmap thumbBitmap = new Bitmap();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), kwonURI);
//                //Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bitmap, 900, 500);
//                thumb.setImageBitmap(bitmap);
//                thumb.setVisibility(View.VISIBLE);
//            }
//            catch(IOException e){}
//
//            Button b = (Button) findViewById(R.id.area_add_finding_row5);
//            b.setText("Change Photo");

//            DO STUFF HERE

            Toast t = Toast.makeText(getApplicationContext(),"Image captured.",Toast.LENGTH_SHORT);
            t.show();

            Log.d("TAKE","TAKE PHOTO");


        }
    }

    String mCurrentPhotoPath;

    public File createImageFile(String tag) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_"+service_order_id+"_";
//        Log.d("ADD FINDING 2",m.getService_order_id());
//        Log.d("ADD FINDING 2",imageFileName);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        if(tag.equals("findings")) {
            newFindingPhotoPaths.add(mCurrentPhotoPath);
            findingPhotoPaths.add(mCurrentPhotoPath);
        }
        else {
            newActionPhotoPaths.add(mCurrentPhotoPath);
            actionPhotoPaths.add(mCurrentPhotoPath);
        }

        return image;
    }

    public void saveFinding(View v){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        // Initialize Service Orders
        EditText findingsET = (EditText) findViewById(R.id.area_findings);
        EditText proposedActionET = (EditText) findViewById(R.id.area_proposed_action);
        EditText actionsTakenET = (EditText) findViewById(R.id.area_findings_actions_taken);

        EditText personInChargeET = (EditText) findViewById(R.id.area_person_in_charge);
        Spinner riskAssessmentSpinner = (Spinner) findViewById(R.id.area_risk_assessment_edit);


        String findings = findingsET.getText().toString();
        String proposedAction = proposedActionET.getText().toString();
        String actionsTaken = actionsTakenET.getText().toString();
        String personInCharge = personInChargeET.getText().toString();
        String riskAssessment = riskAssessmentSpinner.getSelectedItem().toString();
        String status = statusSpinner.getSelectedItem().toString();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();

        values.put(SterixContract.AreaFinding.COLUMN_FINDINGS,findings);
        values.put(SterixContract.AreaFinding.COLUMN_PROPOSED_ACTION,proposedAction);
        values.put(SterixContract.AreaFinding.COLUMN_ACTION_TAKEN,actionsTaken);
        values.put(SterixContract.AreaFinding.COLUMN_PERSON_IN_CHARGE,personInCharge);
        values.put(SterixContract.AreaFinding.COLUMN_RISK_ASSESSMENT,riskAssessment);
        values.put(SterixContract.AreaFinding.COLUMN_STATUS,status);
        values.put(SterixContract.AreaFinding.COLUMN_TIMESTAMP,timeStamp);

        Log.d("UPDATE",finding_id.toString());

        database.update(SterixContract.AreaFinding.TABLE_NAME,values, SterixContract.AreaFinding._ID+" = "+finding_id.toString(),null);
        int numrows = database.update(SterixContract.AreaFindingQueue.TABLE_NAME,values, SterixContract.AreaFindingQueue._ID+" = "+finding_id.toString(),null);
        Log.d("NUMROWS",Integer.toString(numrows));
        //      ALSO UPDATE THE QUEUE

        Log.d("POSITION",position);
        Log.d("ORAS", new SimpleDateFormat("MMM dd").format(new Date()).toString());
        OpenConcernsActivity.findings.get(Integer.parseInt(position)).setFinding(findings);
        OpenConcernsActivity.findings.get(Integer.parseInt(position)).setProposedAction(proposedAction);
        OpenConcernsActivity.findings.get(Integer.parseInt(position)).setActionTaken(actionsTaken);
        OpenConcernsActivity.findings.get(Integer.parseInt(position)).setTimestamp(timeStamp);
        OpenConcernsActivity.findings.get(Integer.parseInt(position)).setRiskAssessment(riskAssessment);
        OpenConcernsActivity.findings.get(Integer.parseInt(position)).setPersonInCharge(personInCharge);
        OpenConcernsActivity.findings.get(Integer.parseInt(position)).setStatus(status);
//        OpenConcernsActivity.findings.get(Integer.parseInt(position)).setTimestamp(new SimpleDateFormat("MMM dd").format(new Date()).toString());

        if(status.equals("Closed")){

            OpenConcernsActivity.findings.remove(Integer.parseInt(position));
        }

        OpenConcernsActivity.findingAdapter.notifyDataSetChanged();
        OpenConcernsActivity.recyclerView.setAdapter(( OpenConcernsActivity.findingAdapter));

        // Process images to be deleted
        for(int i=0; i<photosForDeletion.size();i++){

            database.delete(SterixContract.AreaMonitoringActionTakenPhotos.TABLE_NAME,"filename = ?",new String[]{photosForDeletion.get(i)});
            database.delete(SterixContract.AreaMonitoringFindingPhotos.TABLE_NAME,"filename = ?",new String[]{photosForDeletion.get(i)});

            database.delete(SterixContract.AreaMonitoringActionTakenPhotosQueue.TABLE_NAME,"filename = ?",new String[]{photosForDeletion.get(i)});
            database.delete(SterixContract.AreaMonitoringFindingPhotosQueue.TABLE_NAME,"filename = ?",new String[]{photosForDeletion.get(i)});


            String filenameShort = photosForDeletion.get(i).split("/")[9];
            photosForDeletionStr +=filenameShort+",";
        }

        Log.d("DELETE",photosForDeletionStr);

        // Process images to be saved

        for (int i = 0; i < newFindingPhotoPaths.size(); i++) {

            values = new ContentValues();

            values.put(SterixContract.AreaMonitoringFindingPhotos.COLUMN_AREA_MONITORING_ID, finding_id);
            values.put(SterixContract.AreaMonitoringFindingPhotos.COLUMN_FILENAME, newFindingPhotoPaths.get(i));
            values.put(SterixContract.AreaMonitoringFindingPhotos.COLUMN_TIMESTAMP, timeStamp);

            database.insert(SterixContract.AreaMonitoringFindingPhotos.TABLE_NAME, null, values);

            if(numrows>0) {
                Log.d("EDIT", "Still in queue.");
                database.insert(SterixContract.AreaMonitoringFindingPhotosQueue.TABLE_NAME, null, values);
            }

            String filenameShort = newFindingPhotoPaths.get(i).split("/")[9];
            photosFindingsUpload += filenameShort +",";


        }

        // Insert action taken photos to database

        for (int i = 0; i < newActionPhotoPaths.size(); i++) {

            values = new ContentValues();

            values.put(SterixContract.AreaMonitoringActionTakenPhotos.COLUMN_AREA_MONITORING_ID, finding_id);
            values.put(SterixContract.AreaMonitoringActionTakenPhotos.COLUMN_FILENAME, newActionPhotoPaths.get(i));
            values.put(SterixContract.AreaMonitoringActionTakenPhotos.COLUMN_TIMESTAMP, timeStamp);

            database.insert(SterixContract.AreaMonitoringActionTakenPhotos.TABLE_NAME, null, values);
            if(numrows>0) {
                Log.d("EDIT","Still in queue.");
                database.insert(SterixContract.AreaMonitoringActionTakenPhotosQueue.TABLE_NAME, null, values);
            }
            String filenameShort = newActionPhotoPaths.get(i).split("/")[9];
            photosActionTakenUpload += filenameShort +",";

        }



        if(isOnline()){

            HashMap<String,String> params = new HashMap<>();
            params.put("finding_id",finding_id);
            params.put("findings",findings);
            params.put("proposed_action",proposedAction);
            params.put("action_taken",actionsTaken);
            params.put("risk_assessment",riskAssessment);
            params.put("person_in_charge",personInCharge);
            params.put("photos_deletion",photosForDeletionStr);
            params.put("action_photos",photosActionTakenUpload);
            params.put("finding_photos",photosFindingsUpload);
            params.put("status",status);
            params.put("timestamp",timeStamp);
            params.put("user_id",user_id);

            //      JWT Code, COPY THIS EVERYWHERE!!!!!

            SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
            String jwt = sharedPref.getString("JWT", "");
            params.put("jwt",jwt);

            Log.d("PHOTOS",finding_id);
            Log.d("PHOTOS",photosActionTakenUpload);
            Log.d("PHOTOOS",photosFindingsUpload);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url ="http://"+ip+"/SterixBackend/updateConcern.php";

            JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("PHOTOS-RESP",response.get("action_photos").toString());
                                Log.d("PHOTOS-RESP",response.get("finding_photos").toString());
                                Log.d("PHOTOS-RESP",response.get("jombag").toString());
                            }catch (Exception e){};


                            Toast t = Toast.makeText(getApplicationContext(),"The server has been notified of the finding's update.",Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error: ", "WHY LISA, WHY?");
                    VolleyLog.e("Error: ", error.getMessage());
                }
            });

            queue.add(request_json);

//            Upload photos

            for (int i = 0; i < newFindingPhotoPaths.size(); i++) {

                uploadPhoto(newFindingPhotoPaths.get(i));
            }

            for (int i = 0; i < newActionPhotoPaths.size(); i++) {

                uploadPhoto(newActionPhotoPaths.get(i));
            }

        }
        else{

//            IF IN QUEUE
//            Update the row in the queue
//
//            IF NOT IN QUEUE (nasa server na siya)
            if(numrows == 0) {
                Log.d("EDIT","ALREADY IN SERVER");
                values = new ContentValues();

                values.put(SterixContract.FindingUpdatesQueue.COLUMN_FINDING_ID, finding_id);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_FINDINGS, findings);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_PROPOSED_ACTION, proposedAction);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_ACTION_TAKEN, actionsTaken);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_PERSON_IN_CHARGE,personInCharge);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_RISK_ASSESSMENT,riskAssessment);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_TIMESTAMP,timeStamp);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_PHOTOS_DELETION, photosForDeletionStr);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_FINDING_PHOTOS, photosFindingsUpload);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_ACTION_PHOTOS, photosActionTakenUpload);
                values.put(SterixContract.FindingUpdatesQueue.COLUMN_STATUS, status);
                database.insert(SterixContract.FindingUpdatesQueue.TABLE_NAME, null, values);
//
                Toast t = Toast.makeText(getApplicationContext(), "Finding updated.", Toast.LENGTH_SHORT);
                t.show();
            }
        }

        // Reset the path arraylists
        newFindingPhotoPaths = new ArrayList<>();
        newActionPhotoPaths = new ArrayList<>();
        photosActionTakenUpload = "";
        photosFindingsUpload = "";

        database.close();

        finish();
    }

    /*
     *
     * Image upload utilities
     *
     * */

    public void uploadPhoto(String imgPath){
        Log.d("HI!","HI!");

        Log.d("Uploaded",uploaded);
        try {
            String filename = imgPath.split("/")[9];

            if (!uploaded.contains(filename)) {
                Log.d("Uploaded", "HINDI PA NAAUPLOAD ITO");
                encodeImagetoString(imgPath);
            } else {
                Log.d("Uploaded", "UPLOADED NA");

            }
        }catch (Exception e){
            Log.d("PHOTO_PATH","No photo was added.");
        }
    }

    public static String encodedString;
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
                        editor.putString("AM_IMAGES_UPLOADED",uploaded);
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

    /*
     *  Misc. Utilities
     * */

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }




}
