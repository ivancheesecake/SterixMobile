package com.sterix.sterixmobile;

/**
 * Created by Ivan Escamos on 8/7/2017.
 */

public class ServiceOrder {

    private String date;
    private String location;
    private String order;

    public ServiceOrder(){
    }

    public ServiceOrder(String date, String location, String order){

        this.date = date;
        this.location = location;
        this.order = order;

    }

    public void setDate(String date){
        this.date = date;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setOrder(String order){
        this.order = order;
    }

    public String getDate(){
        return this.date;
    } // Still can't get a date. Kelan ka kaya magiging free?

    public String getLocation(){
        return this.location;
    }

    public String getOrder(){
        return this.order;
    }

}
