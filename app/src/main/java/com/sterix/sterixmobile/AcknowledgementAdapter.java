package com.sterix.sterixmobile;

import android.content.Context;
import android.content.res.Resources;
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

public class AcknowledgementAdapter extends RecyclerView.Adapter<AcknowledgementAdapter.AcknowledgementViewHolder>{

    private List<Acknowledgement> acknowledgements;
    private Context context;

    public class AcknowledgementViewHolder extends RecyclerView.ViewHolder {
        public TextView acknowledgementTextView;
        public TextView acceptedTextView;

        public AcknowledgementViewHolder(View view) {
            super(view);
            acknowledgementTextView = (TextView) view.findViewById(R.id.acknowledgement_term);
            acceptedTextView = (TextView) view.findViewById(R.id.acknowledgement_accepted);


        }
    }

    public AcknowledgementAdapter(List<Acknowledgement> acknowledgements, Context context) {
        this.acknowledgements = acknowledgements;
        this.context = context;
    }


    @Override
    public AcknowledgementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_row, parent, false);

        return new AcknowledgementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AcknowledgementViewHolder holder, int position) {
        Acknowledgement a = acknowledgements.get(position);
        holder.acknowledgementTextView.setText(a.getTerm());
        //holder.acceptedTextView.setText(a.isAccepted());


    }

    @Override
    public int getItemCount() {
        return acknowledgements.size();
    }



}
