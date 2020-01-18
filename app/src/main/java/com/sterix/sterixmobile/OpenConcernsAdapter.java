package com.sterix.sterixmobile;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Ivan Escamos on 8/7/2017.
 */

public class OpenConcernsAdapter extends RecyclerView.Adapter<OpenConcernsAdapter.OpenConcernsViewHolder>{

    private List<Finding> findings;

    public class OpenConcernsViewHolder extends RecyclerView.ViewHolder {
        public TextView finding;
        public TextView findingSite;
        public TextView findingArea;
        public TextView findingServiceOrderId;
        public TextView findingTimestamp;
        public LinearLayout findingOutline;


        public OpenConcernsViewHolder(View view) {
            super(view);
            finding = (TextView) view.findViewById(R.id.finding_finding);
            findingSite = (TextView) view.findViewById(R.id.finding_site);
            findingArea = (TextView) view.findViewById(R.id.finding_area);
            findingServiceOrderId = (TextView) view.findViewById(R.id.finding_service_order_id);
            findingTimestamp = (TextView) view.findViewById(R.id.finding_timestamp);
            findingOutline = (LinearLayout) view.findViewById(R.id.finding_outline);

//            dateTextView = (TextView) view.findViewById(R.id.service_order_date);
//            locationTextView = (TextView) view.findViewById(R.id.service_order_location);
//            orderTextView = (TextView) view.findViewById(R.id.service_order_order);

        }
    }

    public OpenConcernsAdapter(List<Finding> findings) {
        this.findings = findings;
    }


    @Override
    public OpenConcernsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.finding_row, parent, false);

        return new OpenConcernsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OpenConcernsViewHolder holder, int position) {
        Finding f = findings.get(position);

        String date_formatted;
        try {
//                Log.d("HEY",date);
            Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(f.getTimestamp());
             date_formatted = new SimpleDateFormat("MMM dd").format(parse);


        }
        catch(Exception e){
            date_formatted = "NULL";
        }


        holder.findingServiceOrderId.setText("Service Ref. ID: "+f.getServiceOrderId());
        holder.findingArea.setText(f.getClient_location_area_name());
        holder.findingSite.setText(f.getClient_location_name());
        holder.finding.setText(f.getFinding());
        holder.findingTimestamp.setText(date_formatted);

        if(f.getRiskAssessment().equals("Critical") && f.getMark_as_done().equals("Closed")) {
            holder.findingTimestamp.setBackground(ContextCompat.getDrawable(holder.findingTimestamp.getContext(), R.drawable.curved_borders_orange));
            holder.findingOutline.setBackground(ContextCompat.getDrawable(holder.findingOutline.getContext(), R.drawable.curved_borders_orange_outline));
        }
        else if(f.getRiskAssessment().equals("Critical") && !f.getMark_as_done().equals("Closed")) {
            holder.findingTimestamp.setBackground(ContextCompat.getDrawable(holder.findingTimestamp.getContext(), R.drawable.curved_borders_red));
            holder.findingOutline.setBackground(ContextCompat.getDrawable(holder.findingOutline.getContext(), R.drawable.curved_borders_red_outline));
        }

        else if(f.getRiskAssessment().equals("Low") && f.getMark_as_done().equals("Closed")) {
            holder.findingTimestamp.setBackground(ContextCompat.getDrawable(holder.findingTimestamp.getContext(), R.drawable.curved_borders_yellow));
            holder.findingOutline.setBackground(ContextCompat.getDrawable(holder.findingOutline.getContext(), R.drawable.curved_borders_yellow_outline));
        }
        else if (f.getRiskAssessment().equals("Low") && !f.getMark_as_done().equals("Closed")) {
            holder.findingTimestamp.setBackground(ContextCompat.getDrawable(holder.findingTimestamp.getContext(), R.drawable.curved_borders_blue));
            holder.findingOutline.setBackground(ContextCompat.getDrawable(holder.findingOutline.getContext(), R.drawable.curved_borders));
        }
            //        holder.dateTextView.setText(so.getDate());
//        holder.locationTextView.setText(so.getLocation());
//        holder.orderTextView.setText(so.getOrder());

    }

    @Override
    public int getItemCount() {
        return findings.size();
    }



}
