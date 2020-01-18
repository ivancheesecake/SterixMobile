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

public class DevicesSummaryAdapter extends RecyclerView
        .Adapter<DevicesSummaryAdapter
        .DataObjectHolder> {


    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<HashMap<String,String>> devices;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView deviceCode;
        TextView action;
        TextView condition;



        public DataObjectHolder(View itemView) {
            super(itemView);

            deviceCode =  (TextView) itemView.findViewById(R.id.card_device_code);
            action =  (TextView) itemView.findViewById(R.id.card_device_action);
            condition =  (TextView) itemView.findViewById(R.id.card_device_condition);

            Log.i(LOG_TAG, "Adding Listener");
//            itemView.setOnClickListener(this);
        }


    }


    public DevicesSummaryAdapter(ArrayList<HashMap<String,String>> myDataset) {
        devices = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.devices_cardview, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
//        holder.finding.setText(findings.get(position).getFinding());

//        devices.get(position).get("device_code").toString();
        holder.deviceCode.setText(devices.get(position).get("device_code"));
        holder.action.setText("Activity" +
                ": "+devices.get(position).get("activity"));
        holder.condition.setText("Condition: "+devices.get(position).get("device_condition"));
//
//        holder.deviceCode.setText("1");
//        holder.action.setText("2");
//        holder.condition.setText("3");
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