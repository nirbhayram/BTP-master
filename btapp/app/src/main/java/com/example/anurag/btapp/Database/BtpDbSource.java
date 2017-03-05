package com.example.anurag.btapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

/**
 * Created by Parth on 04-03-2017.
 * Project btapp.
 */

public class BtpDbSource {
    private SQLiteDatabase database;
    private BtpDbHelper dbHelper;
    private DatabaseReference databaseReference;
    private Firebase mRef;
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

    private void putAllDataOnline(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Data");

        ArrayList<BtpRecord> records = getAllRecords();

        for (int i=0;i<records.size();i++){
            databaseReference.child(getDate()).child(records.get(i).getTime()).child(BtpContract.BtpEntry.COLUMN_TEMPERATURE).setValue(records.get(i).getTemperature());
            databaseReference.child(getDate()).child(records.get(i).getTime()).child(BtpContract.BtpEntry.COLUMN_HUMIDITY).setValue(records.get(i).getHumidity());
            databaseReference.child(getDate()).child(records.get(i).getTime()).child(BtpContract.BtpEntry.COLUMN_PRESSURE).setValue(records.get(i).getPressure());
        }

    }

    private ArrayList<BtpRecord> getDataOnline(String date){
        final ArrayList<BtpRecord> records = new ArrayList<BtpRecord>();
        mRef = new Firebase("https://btapp-8e7cc.firebaseio.com/Data/"+date);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot:dataSnapshot.getChildren()){
                    /*BtpRecord record = new BtpRecord();
                    record.setTime(postsnapshot.getKey());
                    record.setTemperature(postsnapshot.child(BtpContract.BtpEntry.COLUMN_TEMPERATURE).getValue(String.class));
                    record.setHumidity(postsnapshot.child(BtpContract.BtpEntry.COLUMN_HUMIDITY).getValue(String.class));
                    record.setPressure(postsnapshot.child(BtpContract.BtpEntry.COLUMN_PRESSURE).getValue(String.class));*/
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return records;
    }

    private String getDate(){
        return null;
    }
}
