package com.sterix.sterixmobile;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 8/9/2017.
 * Thanks to http://www.parcelabler.com/ for easy parcelable implementation
 */

public class Monitoring implements Parcelable {

    private String id; //Create 2 ids, 1pk and 1fk
    private String location;
    private String monitoringTask;
    private String status;
    private String service_order_id;
    private String location_area_id;

    public Monitoring(){
    }

    public Monitoring(String id, String location,String monitoringTasks,String status){

        this.id = id;
        this.location = location;
        this.monitoringTask = monitoringTasks;
        this.status = status;

    }

    public Monitoring(String id, String location,String monitoringTasks,String status,String service_order_id, String location_area_id){

        this.id = id;
        this.location = location;
        this.monitoringTask = monitoringTasks;
        this.status = status;
        this.location_area_id = location_area_id;
        this.service_order_id = service_order_id;

    }

    public String getLocation_area_id() {
        return this.location_area_id;
    }

    public void setLocation_area_id(String location_area_id) {
        this.location_area_id = location_area_id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMonitoringTask() {
        return this.monitoringTask;
    }

    public void setMonitoringTask(String monitoringTasks) {
        this.monitoringTask = monitoringTasks;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService_order_id() {
        return this.service_order_id;
    }

    public void setService_order_id(String service_order_id) {
        this.service_order_id = service_order_id;
    }

    protected Monitoring(Parcel in) {
        id = in.readString();
        location = in.readString();
        monitoringTask = in.readString();
        status = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(location);
        dest.writeString(monitoringTask);
        dest.writeString(status);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Monitoring> CREATOR = new Parcelable.Creator<Monitoring>() {
        @Override
        public Monitoring createFromParcel(Parcel in) {
            return new Monitoring(in);
        }

        @Override
        public Monitoring[] newArray(int size) {
            return new Monitoring[size];
        }
    };
}



