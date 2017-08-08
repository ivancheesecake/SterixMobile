package com.sterix.sterixmobile;

/**
 * Created by Ivan Escamos on 8/7/2017.
 */

public class Task {

    private String id;
    private String time;
    private String task;
    private String status;


    public Task(){
    }

    public Task(String id, String time, String task, String status){

        this.id = id;
        this.time = time;
        this.task = task;
        this.status = status;

    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTask() {
        return this.task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
