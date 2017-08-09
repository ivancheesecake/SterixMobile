package com.sterix.sterixmobile;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText et_username;
    EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is logged in

    }

    public void login(View b){


        String username;
        String password;

        // Fetch login credentials

        et_username = (EditText) findViewById(R.id.login_username);
        et_password = (EditText) findViewById(R.id.login_password);

        username = et_username.getText().toString();
        password = et_password.getText().toString();

        Log.d("Username",username);
        Log.d("Username",password);



        // Perform authentication on server

        // If successful login, intent to next activity and destroy login activity

        // Is login retained? Forever?

        Intent serviceOrdersIntent = new Intent(this, ServiceOrdersActivity.class);
        startActivity(serviceOrdersIntent);
        finish();


    }


}
