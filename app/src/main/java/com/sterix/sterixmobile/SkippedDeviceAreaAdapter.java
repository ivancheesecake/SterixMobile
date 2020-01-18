package com.sterix.sterixmobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SkippedDeviceAreaAdapter extends RecyclerView
        .Adapter<SkippedDeviceAreaAdapter
        .DataObjectHolder> {


    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<HashMap<String,String>> devices;
    private ArrayList<String> areas;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        Button area;


        public DataObjectHolder(View itemView) {
            super(itemView);

            area =  (Button) itemView.findViewById(R.id.skipped_device_area_button);


            Log.i(LOG_TAG, "Adding Listener");
//            itemView.setOnClickListener(this);
        }


    }


    public SkippedDeviceAreaAdapter(ArrayList<String> myDataset) {
        areas = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.skipped_devices_area, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

//        holder.area.setText("Area: " + ServiceOrdersActivity.location_areas.get(areas));
//        holder.area.setText(areas.get(position));
        holder.area.setText(ServiceOrdersActivity.location_areas.get(areas.get(position)).toString());
        holder.area.setTag(areas.get(position));



        //        holder.edit.setTag(R.id.finding_id,findings.get(position).getId());


    }

//    public void addItem(Finding dataObj, int index) {
//        findings.add(index, dataObj);
//        notifyItemInserted(index);
//    }
//
//    public void deleteItem(int index) {
//        findings.remove(index);
//        notifyItemRemoved(index);
//    }

    @Override
    public int getItemCount() {
        return areas.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}