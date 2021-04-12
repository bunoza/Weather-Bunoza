package com.bunoza.weatherbunoza.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Data {
    @PrimaryKey
    private int id;
    @ColumnInfo(name="city")
    public String city;
    @ColumnInfo(name="lat")
    public String lat;
    @ColumnInfo(name="lng")
    public String lon;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Data(String city, String lat, String lon) {
        this.id = 0;
        this.city = city;
        this.lat = lat;
        this.lon = lon;
    }
}
