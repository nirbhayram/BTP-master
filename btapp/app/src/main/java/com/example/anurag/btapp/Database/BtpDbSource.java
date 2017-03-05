package com.example.anurag.btapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Parth on 04-03-2017.
 * Project btapp.
 */

public class BtpDbSource {
    private SQLiteDatabase database;
    private BtpDbHelper dbHelper;
    private String columns[] = {BtpContract.BtpEntry._ID,
            BtpContract.BtpEntry.COLUMN_TIME,
            BtpContract.BtpEntry.COLUMN_TEMPERATURE,
            BtpContract.BtpEntry.COLUMN_PRESSURE,
            BtpContract.BtpEntry.COLUMN_HUMIDITY};

    public BtpDbSource(Context context){
        dbHelper = new BtpDbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public BtpRecord addRecord(BtpRecord record){
        ContentValues values = new ContentValues();

        values.put(BtpContract.BtpEntry.COLUMN_TIME, record.getTime());
        values.put(BtpContract.BtpEntry.COLUMN_TEMPERATURE, record.getTemperature());
        values.put(BtpContract.BtpEntry.COLUMN_HUMIDITY, record.getHumidity());
        values.put(BtpContract.BtpEntry.COLUMN_PRESSURE, record.getPressure());

        record.setId(database.insert(BtpContract.BtpEntry.TABLE_NAME, null, values));
        return record;
    }

    public ArrayList<BtpRecord> getAllRecords(){
        ArrayList<BtpRecord> records = new ArrayList<>();

        Cursor cursor = database.query(BtpContract.BtpEntry.TABLE_NAME, columns, null, null, null, null, null);

        if(cursor.moveToFirst()){
            do{
                records.add(cursorToRecord(cursor));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    private BtpRecord cursorToRecord(Cursor cursor){
        BtpRecord record = new BtpRecord();
        record.setId(cursor.getLong(cursor.getColumnIndex(columns[0])));
        record.setTime(cursor.getString(cursor.getColumnIndex(columns[1])));
        record.setTemperature(cursor.getString(cursor.getColumnIndex(columns[2])));
        record.setPressure(cursor.getString(cursor.getColumnIndex(columns[3])));
        record.setHumidity(cursor.getString(cursor.getColumnIndex(columns[4])));
        return record;
    }
}
