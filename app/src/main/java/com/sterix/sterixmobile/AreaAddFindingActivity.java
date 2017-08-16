package com.sterix.sterixmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class AreaAddFindingActivity extends AppCompatActivity {

    Bitmap imageBitmap;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_area_add_finding);


        ImageView thumb = (ImageView) findViewById(R.id.area_add_finding_row7);

        if(savedInstanceState != null) {


            Bitmap bitmap = savedInstanceState.getParcelable("image");
            if(bitmap!=null)
                thumb.setImageBitmap(bitmap);

        }

        thumb.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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


    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void saveFinding(View v){

        // Insert rows to database
        // Save photo to disk
        // Destroy activity

        finish();


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            ImageView thumb = (ImageView) findViewById(R.id.area_add_finding_row7);
            extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            thumb.setImageBitmap(imageBitmap);
            thumb.setVisibility(View.VISIBLE);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {


        outState.putParcelable("image", imageBitmap);
        super.onSaveInstanceState(outState);
    }


}