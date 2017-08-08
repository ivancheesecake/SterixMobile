package com.sterix.sterixmobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AcknowledgementActivity extends AppCompatActivity {

    private List<Acknowledgement> acknowledgements = new ArrayList<>();
    private RecyclerView recyclerView;
    private AcknowledgementAdapter acknowledgementAdapter;
    private Acknowledgement a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgement);


    }



}
