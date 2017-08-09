package com.sterix.sterixmobile;

/**
 * Created by Admin on 8/9/2017.
 */

public class Monitoring {

    public String id;
    public String location;
    public String[] monitoringTasks;

    public Monitoring(){
    }

    public Monitoring(String id, String location,String[] monitoringTasks){

        this.id = id;
        this.location = location;
        this.monitoringTasks = monitoringTasks;

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
}
