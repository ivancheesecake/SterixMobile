package com.sterix.sterixmobile;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText et_username;
    EditText et_password;
    HashMap<String,String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is logged in

        // Insert values for database

        //insertToDB();


    }

    public void insertToDB(){

        SQLiteDatabase database = new SterixDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SterixContract.ServiceOrder.COLUMN_SERVICE_TYPE,"Monitoring");
        values.put(SterixContract.ServiceOrder.COLUMN_LOCATION,"Jollibee Junction");
        values.put(SterixContract.ServiceOrder.COLUMN_START_DATE,"2017-07-26");
        values.put(SterixContract.ServiceOrder.COLUMN_START_TIME,"00:00:00");
        values.put(SterixContract.ServiceOrder.COLUMN_END_DATE,"2017-07-26");
        values.put(SterixContract.ServiceOrder.COLUMN_END_TIME,"00:00:00");
        values.put(SterixContract.ServiceOrder.COLUMN_STATUS,"Accepted/In Progress");

        database.insert(SterixContract.ServiceOrder.TABLE_NAME, null, values);

        values.put(SterixContract.ServiceOrder.COLUMN_SERVICE_TYPE,"Monitoring");
        values.put(SterixContract.ServiceOrder.COLUMN_LOCATION,"Bonchon Solenad 3");
        values.put(SterixContract.ServiceOrder.COLUMN_START_DATE,"2017-12-22");
        values.put(SterixContract.ServiceOrder.COLUMN_START_TIME,"00:00:00");
        values.put(SterixContract.ServiceOrder.COLUMN_END_DATE,"2017-07-26");
        values.put(SterixContract.ServiceOrder.COLUMN_END_TIME,"00:00:00");
        values.put(SterixContract.ServiceOrder.COLUMN_STATUS,"Accepted/In Progress");

        database.insert(SterixContract.ServiceOrder.TABLE_NAME, null, values);


        values.put(SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID,"1");
        values.put(SterixContract.ServiceOrderTask.COLUMN_TASK,"Login at Guard House");
        values.put(SterixContract.ServiceOrderTask.COLUMN_START_TIME,"6:00 AM");
        values.put(SterixContract.ServiceOrderTask.COLUMN_STATUS,"0");
        database.insert(SterixContract.ServiceOrderTask.TABLE_NAME, null, values);

        values.put(SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID,"1");
        values.put(SterixContract.ServiceOrderTask.COLUMN_TASK,"Prepare Materials");
        values.put(SterixContract.ServiceOrderTask.COLUMN_START_TIME,"6:10 AM");
        values.put(SterixContract.ServiceOrderTask.COLUMN_STATUS,"0");
        database.insert(SterixContract.ServiceOrderTask.TABLE_NAME, null, values);

        values.put(SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID,"1");
        values.put(SterixContract.ServiceOrderTask.COLUMN_TASK,"Conduct Monitoring");
        values.put(SterixContract.ServiceOrderTask.COLUMN_START_TIME,"7:00 AM");
        values.put(SterixContract.ServiceOrderTask.COLUMN_STATUS,"0");
        database.insert(SterixContract.ServiceOrderTask.TABLE_NAME, null, values);

        values.put(SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID,"2");
        values.put(SterixContract.ServiceOrderTask.COLUMN_TASK,"Login at Guard House");
        values.put(SterixContract.ServiceOrderTask.COLUMN_START_TIME,"3:00 PM");
        values.put(SterixContract.ServiceOrderTask.COLUMN_STATUS,"0");
        database.insert(SterixContract.ServiceOrderTask.TABLE_NAME, null, values);

        values.put(SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID,"2");
        values.put(SterixContract.ServiceOrderTask.COLUMN_TASK,"Prepare Materials");
        values.put(SterixContract.ServiceOrderTask.COLUMN_START_TIME,"3:10 PM");
        values.put(SterixContract.ServiceOrderTask.COLUMN_STATUS,"0");
        database.insert(SterixContract.ServiceOrderTask.TABLE_NAME, null, values);

        values.put(SterixContract.ServiceOrderTask.COLUMN_SERVICE_ORDER_ID,"2");
        values.put(SterixContract.ServiceOrderTask.COLUMN_TASK,"Conduct Monitoring");
        values.put(SterixContract.ServiceOrderTask.COLUMN_START_TIME,"4:00 PM");
        values.put(SterixContract.ServiceOrderTask.COLUMN_STATUS,"0");
        database.insert(SterixContract.ServiceOrderTask.TABLE_NAME, null, values);


    }


    public void login(View b){


        String username;
        String password;

        // Fetch login credentials

        et_username = (EditText) findViewById(R.id.login_username);
        et_password = (EditText) findViewById(R.id.login_password);

        username = et_username.getText().toString();
        password = et_password.getText().toString();

        params = new HashMap<String,String>();
        params.put("username",username);
        params.put("password",password);

        Log.d("Username",username);
        Log.d("Password",password);

        // Intent agad para walang login

        Intent serviceOrdersIntent = new Intent(getApplicationContext(), ServiceOrdersActivity.class);
        startActivity(serviceOrdersIntent);
        finish();


        // Request a string response from the provided URL.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.63.39/SterixBackend/login.php";
//        String url ="http://192.168.1.17/Sterix/login.php";
/*
        final TextView mTextView = (TextView) findViewById(R.id.credentials);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.get("success").toString().equals("true")) {
                                Toast toast = Toast.makeText(getApplicationContext(),"Welcome, "+response.get("username").toString()+"!", Toast.LENGTH_SHORT);
                                toast.show();

                                Intent serviceOrdersIntent = new Intent(getApplicationContext(), ServiceOrdersActivity.class);
                                startActivity(serviceOrdersIntent);
                                finish();
                            }
                            else{

                                Toast toast = Toast.makeText(getApplicationContext(),"Invalid login credentials.", Toast.LENGTH_SHORT);
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
*/
        // Perform authentication on server

        // If successful login, intent to next activity and destroy login activity

        // Is login retained? Forever?

    }


}
