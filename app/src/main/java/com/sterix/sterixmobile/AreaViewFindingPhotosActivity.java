package com.sterix.sterixmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AreaViewFindingPhotosActivity extends AppCompatActivity {

    Monitoring m;
    String service_order_location,tag;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList <String> photoPaths;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_view_finding_photos);

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        ip = sharedPref.getString("IP", "");

        Intent i = getIntent();
        m =  i.getParcelableExtra("AREA_MONITORING_PARCEL");
        TextView tv_location = (TextView) findViewById(R.id.area_view_finding_location);
//        tv_location.setText(m.getLocation());


        TextView area = (TextView) findViewById(R.id.area_view_finding_location);
        area.setText(m.getLocation());

        service_order_location = i.getStringExtra("SERVICE_ORDER_LOCATION");
        tag = i.getStringExtra("TAG");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(service_order_location);

        recyclerView = (RecyclerView) findViewById(R.id.view_finding_photos_recyclerview);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        if(tag.equals("findings"))
            mAdapter = new FindingPhotosAdapter(AreaAddFindingActivity.findingPhotoPaths);
        else if(tag.equals("actions"))
            mAdapter = new FindingPhotosAdapter(AreaAddFindingActivity.actionPhotoPaths);

        else if(tag.equals("actions-view") || tag.equals("findings-view")){
//            photoPaths = new ArrayList<>();
//            mAdapter = new FindingPhotosAdapter(AreaViewFindingActivity.photoPaths);

//            photoPaths = new ArrayList<>();
            mAdapter = new FindingViewPhotosAdapter(AreaViewFindingActivity.photoPaths);
        }
        else if(tag.equals("findings-edit")){
//            photoPaths = new ArrayList<>();
//            mAdapter = new FindingPhotosAdapter(AreaViewFindingActivity.photoPaths);

//            photoPaths = new ArrayList<>();
            mAdapter = new FindingPhotosAdapter(AreaEditFindingActivity.findingPhotoPaths);
        }

        else if(tag.equals("actions-edit")) {

            mAdapter = new FindingPhotosAdapter(AreaEditFindingActivity.actionPhotoPaths);

        }

            recyclerView.setAdapter(mAdapter);

//        Log.d("IMAGE",AreaAddFindingActivity.kwonURIDeul.get(0).toString());
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

    private ArrayList<Finding> getFindingPhotos(){


        ArrayList<Finding> results = new ArrayList<>();



        return results;

    }

    public void deletePhoto(final View v){
        Log.d("DELETE","DELETE PHOTO "+v.getTag());


        if (tag.equals("actions") || tag.equals("findings")) {
            final int delete_id = Integer.parseInt(v.getTag().toString());
            new MaterialDialog.Builder(this)
                    .title(R.string.alert_delete_photo_title)
                    .content(R.string.alert_delete_photo)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {

                            if (tag.equals("findings"))
                                AreaAddFindingActivity.findingPhotoPaths.remove(delete_id);
                            else
                                AreaAddFindingActivity.actionPhotoPaths.remove(delete_id);

                            mAdapter.notifyDataSetChanged();
                        }
                    })
                    .positiveText(R.string.delete)
                    .negativeText(R.string.disagree)
                    .show();

        }
        else if(tag.equals("actions-edit") || tag.equals("findings-edit")){
//            Log.d("DELETE","ELSE");
            final int delete_id = Integer.parseInt(v.getTag().toString());

            new MaterialDialog.Builder(this)
                    .title(R.string.alert_delete_photo_title)
                    .content(R.string.alert_delete_photo)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {

//                  MAMAYA MO PA GAGAWIN ITO
                        String filename = v.getTag(R.id.image_path).toString();
//                            Log.d("DELETE",filename);
//
////                      Remove from adapter
//                            AreaEditFindingActivity.photoPaths.remove(delete_id);
//                            mAdapter.notifyDataSetChanged();
//
////                      Remove from local DB
//                            SQLiteDatabase database = new SterixDBHelper(getApplicationContext()).getWritableDatabase();
//
//                            database.delete(SterixContract.AreaMonitoringActionTakenPhotos.TABLE_NAME,"filename = ?",new String[]{filename});
//                            database.delete(SterixContract.AreaMonitoringFindingPhotos.TABLE_NAME,"filename = ?",new String[]{filename});
//
//                            database.close();
//
////                      Delete in Server
//
//                            String filenameShort = filename.split("/")[9];
//
//                            HashMap<String,String> params = new HashMap<>();
//                            params.put("filename",filenameShort);
//
//                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                            String url ="https://"+ip+"/SterixBackend/deleteAreaMonitoringImage.php";
//
//                            JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
//                                    new Response.Listener<JSONObject>() {
//                                        @Override
//                                        public void onResponse(JSONObject response) {
//                                            Toast t = Toast.makeText(getApplicationContext(),"The server has been notified of the image's deletion.",Toast.LENGTH_SHORT);
//                                            t.show();
//                                        }
//                                    }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    Log.d("Error: ", "WHY LISA, WHY?");
//                                    VolleyLog.e("Error: ", error.getMessage());
//                                }
//                            });
//
//                            queue.add(request_json);


//                      Delete the file, tho there is no need ATM

//                            if (tag.equals("findings"))
//                                AreaAddFindingActivity.findingPhotoPaths.remove(delete_id);
//                            else
//                                AreaAddFindingActivity.actionPhotoPaths.remove(delete_id);
//
//                            mAdapter.notifyDataSetChanged();

                            if (tag.equals("findings-edit"))
                                AreaEditFindingActivity.findingPhotoPaths.remove(Integer.parseInt(v.getTag().toString()));
                            else
                                AreaEditFindingActivity.actionPhotoPaths.remove(Integer.parseInt(v.getTag().toString()));

                            AreaEditFindingActivity.photosForDeletion.add(filename);
                            mAdapter.notifyDataSetChanged();

                        }
                    })
                    .positiveText(R.string.delete)
                    .negativeText(R.string.disagree)
                    .show();





        }
    }

    public void sendAlert(View v){

        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
        final String location_id = sharedPref.getString("LOCATION_ID", "");

        Log.d("LOCATION_ID",location_id);

        new MaterialDialog.Builder(this)
                .title(R.string.alert_title)
                .content(R.string.alert_content)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES  | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .input(R.string.alert_hint, R.string.blank, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
//                        alertMessage = input.toString();
                        Log.d("HEY","An alert was sent. "+input);

                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url ="http://"+ip+"/SterixBackend/sendAlert.php";

                        HashMap params = new HashMap<String,String>();
                        params.put("alert",input.toString());
                        params.put("client_location_ID",location_id);

                        //      JWT Code, COPY THIS EVERYWHERE!!!!!

                        SharedPreferences sharedPref = getSharedPreferences("sterix_prefs",Context.MODE_PRIVATE);
                        String jwt = sharedPref.getString("JWT", "");
                        params.put("jwt",jwt);


                        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {

                                            if(response.get("success").toString().equals("true")) {
                                                Toast toast = Toast.makeText(getApplicationContext(),"Alert sent", Toast.LENGTH_SHORT);
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
                })
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }


}
