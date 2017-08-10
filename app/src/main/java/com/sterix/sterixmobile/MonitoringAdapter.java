package com.sterix.sterixmobile;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ivan Escamos on 8/7/2017.
 */

public class MonitoringAdapter extends RecyclerView.Adapter<MonitoringAdapter.MonitoringViewHolder>{

    private List<List<Monitoring>> monitoring;
    private Context context;

    public class MonitoringViewHolder extends RecyclerView.ViewHolder {


        public TextView monitoringLocationTextView;
        public Button monitoringButton1;
        public Button monitoringButton2;


        public MonitoringViewHolder(View view) {
            super(view);
            monitoringLocationTextView = (TextView) view.findViewById(R.id.monitoring_location);
            monitoringButton1 = (Button) view.findViewById(R.id.monitoring_button1);
            monitoringButton2 = (Button) view.findViewById(R.id.monitoring_button2);

        }
    }

    public MonitoringAdapter(List<List<Monitoring>> monitoring, Context context) {
        this.monitoring = monitoring;
        this.context = context;
    }


    @Override
    public MonitoringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monitoring_row, parent, false);

        return new MonitoringViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MonitoringViewHolder holder, int position) {
        List<Monitoring> m = monitoring.get(position);
        holder.monitoringLocationTextView.setText(m.get(0).getLocation());
        holder.monitoringButton1.setText(m.get(0).getMonitoringTask());
        holder.monitoringButton2.setText(m.get(1).getMonitoringTask());
        holder.monitoringButton1.setTag(m.get(0));
        holder.monitoringButton2.setTag(m.get(1));

        if(m.get(0).getStatus().equals("0"))
            holder.monitoringButton1.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_red));
        else if(m.get(0).getStatus().equals("1"))
            holder.monitoringButton1.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_yellow));
        else
            holder.monitoringButton1.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_green));

        if(m.get(1).getStatus().equals("0"))
            holder.monitoringButton2.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_red));
        else if(m.get(1).getStatus().equals("1"))
            holder.monitoringButton2.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_yellow));
        else
            holder.monitoringButton2.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_green));

    }

    @Override
    public int getItemCount() {
        return monitoring.size();
    }



}
