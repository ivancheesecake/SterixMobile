package com.sterix.sterixmobile;

/**
 * Created by Admin on 8/9/2017.
 */

public class Monitoring {

    private String id;
    private String location;
    private String[] monitoringTasks;
    private String[] status;

    public Monitoring(){
    }

    public Monitoring(String id, String location,String[] monitoringTasks,String[] status){

        this.id = id;
        this.location = location;
        this.monitoringTasks = monitoringTasks;
        this.status = status;

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

    public String[] getMonitoringTasks() {
        return this.monitoringTasks;
    }

    public void setMonitoringTasks(String[] monitoringTasks) {
        this.monitoringTasks = monitoringTasks;
    }

    public String[] getStatus() {
        return this.status;
    }

    public void setStatus(String[] status) {
        this.status = status;
    }
}

