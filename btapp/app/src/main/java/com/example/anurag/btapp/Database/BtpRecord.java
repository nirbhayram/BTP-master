package com.example.anurag.btapp.Database;

/**
 * Created by Parth on 04-03-2017.
 * Project btapp.
 */

public class BtpRecord {
    private long id;
    private String time;
    private String temperature;
    private String pressure;
    private String humidity;

    public BtpRecord(){
        this.setTime("");
        this.setTemperature("");
        this.setPressure("");
        this.setHumidity("");
    }

    public BtpRecord(String time, String temperature, String pressure, String humidity){
        this.time = time;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
