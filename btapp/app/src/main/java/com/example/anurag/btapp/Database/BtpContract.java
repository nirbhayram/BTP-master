package com.example.anurag.btapp.Database;

import android.provider.BaseColumns;

/**
 * Created by Parth on 04-03-2017.
 * Project btapp.
 */

public final class BtpContract {

    public static final String CREATE_TABLE_BTP = "CREATE TABLE IF NOT EXISTS " + BtpEntry.TABLE_NAME
            + "("
            + BtpEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + BtpEntry.COLUMN_TIME + " VARCHAR (30) , "
            + BtpEntry.COLUMN_TEMPERATURE + " REAL , "
            + BtpEntry.COLUMN_PRESSURE + " REAL , "
            + BtpEntry.COLUMN_HUMIDITY + " REAL "
            + ")";

    public static final String DELETE_TABLE_BTP = "DROP TABLE IF EXISTS " + BtpEntry.TABLE_NAME;

    private BtpContract(){}

    public static final class BtpEntry implements BaseColumns {
        public static final String TABLE_NAME = "TempHumiPres";
        public static final String COLUMN_TIME = "Time";
        public static final String COLUMN_TEMPERATURE = "Temperature";
        public static final String COLUMN_HUMIDITY = "Humidity";
        public static final String COLUMN_PRESSURE = "Pressure";
    }
}
