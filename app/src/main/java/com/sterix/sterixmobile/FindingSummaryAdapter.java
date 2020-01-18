package com.sterix.sterixmobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FindingSummaryAdapter extends RecyclerView
        .Adapter<FindingSummaryAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Finding> findings;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView location_area;
        TextView proposedAction;
        TextView finding;
        TextView actionsTaken;
        TextView viewPhotosFindings;
        TextView viewPhotosActionsTaken;
        TextView riskAssessment;
        TextView personInCharge;
        TextView status;
        Button edit;
        Button delete;
        ImageView cardImage;

        public DataObjectHolder(View itemView) {
            super(itemView);
//            location_area = (TextView) itemView.findViewById(R.id.location_area);
            finding = (TextView) itemView.findViewById(R.id.card_finding);
            proposedAction = (TextView) itemView.findViewById(R.id.card_proposed_action);
            actionsTaken = (TextView) itemView.findViewById(R.id.card_action_taken);
            viewPhotosFindings = (TextView) itemView.findViewById(R.id.card_finding_view_photos);
            viewPhotosActionsTaken = (TextView) itemView.findViewById(R.id.card_action_taken_view_photos);
            riskAssessment = (TextView) itemView.findViewById(R.id.card_risk_assessment);
            personInCharge = (TextView) itemView.findViewById(R.id.card_person_in_charge);
            status = (TextView) itemView.findViewById(R.id.card_status);
            edit =  (Button) itemView.findViewById(R.id.edit_finding);
            delete =  (Button) itemView.findViewById(R.id.delete_finding);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);

            Log.i(LOG_TAG, "Adding Listener");
//            itemView.setOnClickListener(this);
        }


    }


    public FindingSummaryAdapter(ArrayList<Finding> myDataset) {
        findings = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.finding_summary_card, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
//        holder.finding.setText(findings.get(position).getFinding());
//        holder.location_area.setText(findings.get(position).get());
        holder.finding.setText(findings.get(position).getFinding());
        holder.proposedAction.setText(findings.get(position).getProposedAction());
        holder.actionsTaken.setText(findings.get(position).getActionTaken());
        holder.riskAssessment.setText(findings.get(position).getRiskAssessment());

        Log.d("RED",findings.get(position).getRiskAssessment());

        if(findings.get(position).getRiskAssessment().equals("Critical"))
            holder.riskAssessment.setTextColor(Color.parseColor("#e74c3c"));
        holder.personInCharge.setText(findings.get(position).getPersonInCharge());
        holder.status.setText(findings.get(position).getStatus());
//        holder.viewPhotosFindings.setTag(R.id.finding_id,findings.get(position).getId());
//        holder.viewPhotosActionsTaken.setTag(R.id.finding_id,findings.get(position).getId());
//        holder.edit.setTag(position);
//        holder.edit.setTag(R.id.finding_id,findings.get(position).getId());
//        holder.delete.setTag(position);
//        holder.delete.setTag(R.id.finding_id,findings.get(position).getId());


//        File sd = Environment.getExternalStorageDirectory();
//        try{
//            File image = new File(findings.get(position).getImage());
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
//            bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.25),(int)(bitmap.getHeight()*0.25),true);
//            holder.cardImage.setImageBitmap(bitmap);
//        }
//        catch (Exception e){
//
//            holder.cardImage.setVisibility(View.GONE);
//        }

        //Bitmap img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), findings.get(position).getImage());
//        holder.cardImage.
    }

    public void addItem(Finding dataObj, int index) {
        findings.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        findings.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return findings.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}