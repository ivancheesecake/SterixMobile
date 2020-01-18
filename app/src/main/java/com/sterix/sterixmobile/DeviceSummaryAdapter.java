package com.sterix.sterixmobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.transition.Visibility;
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

public class DeviceSummaryAdapter extends RecyclerView
        .Adapter<DeviceSummaryAdapter
        .DataObjectHolder> {


    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<HashMap<String,String>> devices;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView deviceCode;
        TextView action;
        TextView area;
        TextView condition;
        TextView schedule;
        TextView is_scheduled;
        Button edit;


        public DataObjectHolder(View itemView) {
            super(itemView);

            deviceCode =  (TextView) itemView.findViewById(R.id.card_device_code);
            area =  (TextView) itemView.findViewById(R.id.card_device_area);
            action =  (TextView) itemView.findViewById(R.id.card_device_action);
            condition =  (TextView) itemView.findViewById(R.id.card_device_condition);
            schedule =  (TextView) itemView.findViewById(R.id.card_device_schedule);
            is_scheduled =  (TextView) itemView.findViewById(R.id.card_device_is_scheduled);
            edit =  (Button) itemView.findViewById(R.id.edit_device);


            Log.i(LOG_TAG, "Adding Listener");
//            itemView.setOnClickListener(this);
        }


    }


    public DeviceSummaryAdapter(ArrayList<HashMap<String,String>> myDataset) {
        devices = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_cardview, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
//        holder.finding.setText(findings.get(position).getFinding());

//        devices.get(position).get("device_code").toString();
        holder.deviceCode.setText(devices.get(position).get("device_code"));
        holder.area.setText("Area: " + ServiceOrdersActivity.location_areas.get(devices.get(position).get("client_location_area_id")));
        holder.action.setText("Activity" +
                ": "+devices.get(position).get("activity"));

        holder.schedule.setText("Schedule: " + devices.get(position).get("schedule"));
        holder.schedule.setVisibility(View.GONE);

        if(devices.get(position).get("is_scheduled").equals("true"))
            holder.is_scheduled.setText("The device is scheduled today for inspection.");
        else
            holder.is_scheduled.setText("The device is not scheduled today for inspection.");

        holder.condition.setText("Condition: "+devices.get(position).get("device_condition"));

        if(devices.get(position).get("is_scheduled").equals("false")){

            holder.deviceCode.setTextColor(Color.parseColor("#cccccc"));
            holder.area.setTextColor(Color.parseColor("#cccccc"));
            holder.action.setTextColor(Color.parseColor("#cccccc"));
            holder.schedule.setTextColor(Color.parseColor("#cccccc"));
            holder.is_scheduled.setTextColor(Color.parseColor("#cccccc"));
            holder.condition.setTextColor(Color.parseColor("#cccccc"));
        }

//
//        holder.deviceCode.setText("1");
//        holder.action.setText("2");
//        holder.condition.setText("3");
        holder.edit.setTag(R.id.device_id,devices.get(position).get("id"));
        holder.edit.setTag(R.id.device_position,position);
        holder.edit.setTag(R.id.device_code,devices.get(position).get("device_code"));
        holder.edit.setTag(R.id.client_location_area_id,devices.get(position).get("client_location_area_id"));
        holder.edit.setTag(R.id.is_scheduled,devices.get(position).get("is_scheduled"));


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
        return devices.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}