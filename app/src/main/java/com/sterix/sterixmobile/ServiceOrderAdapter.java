package com.sterix.sterixmobile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ivan Escamos on 8/7/2017.
 */

public class ServiceOrderAdapter extends RecyclerView.Adapter<ServiceOrderAdapter.ServiceOrderViewHolder>{

    private List<ServiceOrder> serviceOrders;

    public class ServiceOrderViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView locationTextView;
        public TextView orderTextView;

        public ServiceOrderViewHolder(View view) {
            super(view);
            dateTextView = (TextView) view.findViewById(R.id.service_order_date);
            locationTextView = (TextView) view.findViewById(R.id.service_order_location);
            orderTextView = (TextView) view.findViewById(R.id.service_order_order);

        }
    }

    public ServiceOrderAdapter(List<ServiceOrder> serviceOrders) {
        this.serviceOrders = serviceOrders;
    }


    @Override
    public ServiceOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_order_row, parent, false);

        return new ServiceOrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServiceOrderViewHolder holder, int position) {
        ServiceOrder so = serviceOrders.get(position);
        holder.dateTextView.setText(so.getDate());
        holder.locationTextView.setText(so.getLocation());
        holder.orderTextView.setText(so.getOrder());

    }

    @Override
    public int getItemCount() {
        return serviceOrders.size();
    }



}
