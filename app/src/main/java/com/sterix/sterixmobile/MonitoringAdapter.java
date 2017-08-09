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

    private List<Monitoring> monitoring;
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

    public MonitoringAdapter(List<Monitoring> monitoring, Context context) {
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
        Monitoring m = monitoring.get(position);
        holder.monitoringLocationTextView.setText(m.getLocation());
        holder.monitoringButton1.setText(m.getMonitoringTasks()[0]);
        holder.monitoringButton2.setText(m.getMonitoringTasks()[1]);
        if(m.getStatus()[0].equals("0"))
            holder.monitoringButton1.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_red));
        else if(m.getStatus()[0].equals("1"))
            holder.monitoringButton1.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_yellow));
        else
            holder.monitoringButton1.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_green));

        if(m.getStatus()[1].equals("0"))
            holder.monitoringButton2.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_red));
        else if(m.getStatus()[1].equals("1"))
            holder.monitoringButton2.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_yellow));
        else
            holder.monitoringButton2.setBackground(ContextCompat.getDrawable(context, R.drawable.curved_borders_green));

    }

    @Override
    public int getItemCount() {
        return monitoring.size();
    }



}
