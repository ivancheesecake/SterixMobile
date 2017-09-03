package com.sterix.sterixmobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 8/17/2017.
 */

public class SterixDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 26;
    public static final String DATABASE_NAME = "sterix_database";

    public SterixDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SterixContract.ServiceOrder.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.ServiceOrderTask.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.ServiceOrderArea.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.DeviceActivity.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.DeviceCondition.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.DeviceMonitoring.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.DeviceMonitoringPest.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.AreaFinding.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.AreaMonitoringPest.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.DeviceMonitoringQueue.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.DeviceMonitoringPestQueue.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.AreaFindingQueue.CREATE_TABLE);
        sqLiteDatabase.execSQL(SterixContract.AreaMonitoringPestQueue.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.ServiceOrder.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.ServiceOrderTask.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.ServiceOrderArea.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.DeviceActivity.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.DeviceCondition.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.DeviceMonitoring.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.DeviceMonitoringPest.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.AreaFinding.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.AreaMonitoringPest.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.DeviceMonitoringQueue.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.DeviceMonitoringPestQueue.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.AreaFindingQueue.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SterixContract.AreaMonitoringPestQueue.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}