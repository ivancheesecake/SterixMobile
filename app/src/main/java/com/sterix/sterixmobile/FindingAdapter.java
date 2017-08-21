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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FindingAdapter extends RecyclerView
        .Adapter<FindingAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Finding> findings;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView finding;
        TextView recommendation;
        ImageView cardImage;

        public DataObjectHolder(View itemView) {
            super(itemView);
            finding = (TextView) itemView.findViewById(R.id.card_finding);
            recommendation = (TextView) itemView.findViewById(R.id.card_recommendation);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            Log.i(LOG_TAG, "Adding Listener");
//            itemView.setOnClickListener(this);
        }


    }


    public FindingAdapter(ArrayList<Finding> myDataset) {
        findings = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
//        holder.finding.setText(findings.get(position).getFinding());
        holder.finding.setText(findings.get(position).getFinding());
        holder.recommendation.setText(findings.get(position).getRecommendation());

//        File sd = Environment.getExternalStorageDirectory();
        try{
            File image = new File(findings.get(position).getImage());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.25),(int)(bitmap.getHeight()*0.25),true);
            holder.cardImage.setImageBitmap(bitmap);
        }
        catch (Exception e){

            holder.cardImage.setVisibility(View.GONE);
        }

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