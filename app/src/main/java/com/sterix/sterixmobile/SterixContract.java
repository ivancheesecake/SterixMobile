package com.sterix.sterixmobile;

import android.provider.BaseColumns;

/**
 * Created by Admin on 8/17/2017.
 */

public final class SterixContract {

    private SterixContract(){}

    public static class ServiceOrder implements BaseColumns {

        public static final String TABLE_NAME = "service_order";
        public static final String COLUMN_SERVICE_TYPE = "service_type";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_START_DATE= "start_date";
        public static final String COLUMN_START_TIME= "start_time";
        public static final String COLUMN_END_DATE= "end_date";
        public static final String COLUMN_END_TIME= "end_time";
        public static final String COLUMN_STATUS= "status";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_SERVICE_TYPE + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_START_DATE + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_END_DATE + " TEXT, " +
                COLUMN_END_TIME + " TEXT, " +
                COLUMN_STATUS + " TEXT" + ")";

    }

    public static class ServiceOrderTask implements BaseColumns {

        public static final String TABLE_NAME = "service_order_task";
        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_TASK = "task";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_STATUS = "status";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_TASK + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_STATUS + " TEXT" + ")";

    }

    public static class ServiceOrderArea implements BaseColumns{

        public static final String TABLE_NAME = "service_order_area";
        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_CLIENT_LOCATION_AREA_ID = "client_location_area_id";
        public static final String COLUMN_CLIENT_LOCATION_AREA = "client_location_area";
        public static final String COLUMN_STATUS = "status";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_CLIENT_LOCATION_AREA_ID + " TEXT, " +
                COLUMN_CLIENT_LOCATION_AREA + " TEXT, " +
                COLUMN_STATUS + " TEXT" + ")";
    }

    public static class DeviceActivity implements BaseColumns{

        public static final String TABLE_NAME = "device_activity";
        public static final String COLUMN_DEVICE_ACTIVITY_ID = "device_activity_id";
        public static final String COLUMN_DEVICE_ACTIVITY_NAME = "device_activity_name";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DEVICE_ACTIVITY_ID + " TEXT, " +
                COLUMN_DEVICE_ACTIVITY_NAME + " TEXT " + ")";

    }

    public static class DeviceCondition implements BaseColumns{

        public static final String TABLE_NAME = "device_condition";
        public static final String COLUMN_CONDITION_ID = "condition_id";
        public static final String COLUMN_CONDITION_NAME = "condition_name";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONDITION_ID + " TEXT, " +
                COLUMN_CONDITION_NAME + " TEXT " + ")";

    }

    public static class DeviceMonitoring implements BaseColumns{

        public static final String TABLE_NAME = "device_monitoring";

        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_CLIENT_LOCATION_AREA_ID = "client_location_area_id";
        public static final String COLUMN_DEVICE_CODE = "device_code";
        public static final String COLUMN_DEVICE_CONDITION_ID = "device_condition_id";
        public static final String COLUMN_DEVICE_CONDITION = "device_condition";
        public static final String COLUMN_ACTIVITY_ID = "activity_id";
        public static final String COLUMN_ACTIVITY = "activity";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_CLIENT_LOCATION_AREA_ID + " TEXT, " +
                COLUMN_DEVICE_CODE + " TEXT, " +
                COLUMN_DEVICE_CONDITION_ID + " TEXT, " +
                COLUMN_DEVICE_CONDITION + " TEXT, " +
                COLUMN_ACTIVITY_ID + " TEXT, " +
                COLUMN_ACTIVITY + " TEXT, "+
                COLUMN_IMAGE + " TEXT, "+
                COLUMN_NOTES + " TEXT, "+
                COLUMN_TIMESTAMP + " TEXT " + ")";
    }

    public static class AreaFinding implements BaseColumns{

        public static final String TABLE_NAME = "area_findings";

        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_CLIENT_LOCATION_AREA_ID = "client_location_area_id";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_FINDINGS = "findings";
        public static final String COLUMN_RECOMMENDATIONS = "recommendations";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        // Maglagay din ng timestamp


        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_CLIENT_LOCATION_AREA_ID + " TEXT, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_FINDINGS + " TEXT, " +
                COLUMN_RECOMMENDATIONS + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT " + ")";

    }

    public static class AreaFindingQueue implements BaseColumns{

        public static final String TABLE_NAME = "area_findings_queue";

        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_CLIENT_LOCATION_AREA_ID = "client_location_area_id";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_FINDINGS = "findings";
        public static final String COLUMN_RECOMMENDATIONS = "recommendations";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        // Maglagay din ng timestamp


        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_CLIENT_LOCATION_AREA_ID + " TEXT, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_FINDINGS + " TEXT, " +
                COLUMN_RECOMMENDATIONS + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT " + ")";

    }

    public static class AreaMonitoringPest implements BaseColumns{

        public static final String TABLE_NAME = "area_monitoring_pest";

        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_SERVICE_ORDER_AREA_ID = "service_order_area_id";
        public static final String COLUMN_PEST_ID = "pest_id";
        public static final String COLUMN_NUMBER = "number";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_SERVICE_ORDER_AREA_ID + " TEXT, " +
                COLUMN_PEST_ID + " TEXT, " +
                COLUMN_NUMBER + " INT " + ")";
    }

    public static class DeviceMonitoringPest implements BaseColumns{

        public static final String TABLE_NAME = "device_monitoring_pest";

        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_DEVICE_MONITORING_ID = "device_monitoring_id";
        public static final String COLUMN_DEVICE_CODE = "device_code";
        public static final String COLUMN_PEST_ID = "pest_id";
        public static final String COLUMN_NUMBER = "number";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_DEVICE_MONITORING_ID + " TEXT, " +
                COLUMN_DEVICE_CODE + " TEXT, " +
                COLUMN_PEST_ID + " TEXT, " +
                COLUMN_NUMBER + " INT " + ")";
    }

    public static class DeviceMonitoringPestQueue implements BaseColumns{

        public static final String TABLE_NAME = "device_monitoring_pest_queue";

        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_DEVICE_MONITORING_ID = "device_monitoring_id";
        public static final String COLUMN_DEVICE_CODE = "device_code";
        public static final String COLUMN_PEST_ID = "pest_id";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_QUEUE_NUMBER = "queue_number";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_DEVICE_MONITORING_ID + " TEXT, " +
                COLUMN_DEVICE_CODE + " TEXT, " +
                COLUMN_PEST_ID + " TEXT, " +
                COLUMN_NUMBER + " INT, " +
                COLUMN_QUEUE_NUMBER + " INT " + ")";
    }

    public static class DeviceMonitoringQueue implements BaseColumns{

        public static final String TABLE_NAME = "device_monitoring_queue";

        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_CLIENT_LOCATION_AREA_ID = "client_location_area_id";
        public static final String COLUMN_DEVICE_CODE = "device_code";
        public static final String COLUMN_DEVICE_CONDITION_ID = "device_condition_id";
        public static final String COLUMN_ACTIVITY_ID = "activity_id";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_QUEUE_NUMBER = "queue_number";


        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_DEVICE_CODE + " TEXT, " +
                COLUMN_CLIENT_LOCATION_AREA_ID + " TEXT, " +
                COLUMN_DEVICE_CONDITION_ID + " TEXT, " +
                COLUMN_ACTIVITY_ID + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT, "+
                COLUMN_IMAGE + " TEXT, "+
                COLUMN_NOTES + " TEXT, " +
                COLUMN_QUEUE_NUMBER + " INT " + ")";
    }

    public static class AreaMonitoringPestQueue implements BaseColumns{

        public static final String TABLE_NAME = "area_monitoring_pest_queue";

        public static final String COLUMN_SERVICE_ORDER_ID = "service_order_id";
        public static final String COLUMN_SERVICE_ORDER_AREA_ID = "service_order_area_id";
        public static final String COLUMN_PEST_ID = "pest_id";
        public static final String COLUMN_NUMBER = "number";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_SERVICE_ORDER_AREA_ID + " TEXT, " +
                COLUMN_PEST_ID + " TEXT, " +
                COLUMN_NUMBER + " INT " + ")";
    }



}
