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
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICE_ORDER_ID + " TEXT, " +
                COLUMN_TASK + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_STATUS + " TEXT" + ")";

    }

}
