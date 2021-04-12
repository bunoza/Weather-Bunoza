package com.bunoza.weatherbunoza.City;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geoname {


    @SerializedName("toponymName")
    @Expose
    private String toponymName;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("adminName1")
    @Expose
    private String adminName1;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("lat")
    @Expose
    private Double lat;

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * No args constructor for use in serialization
     */
    public Geoname() {
    }

    /**
     * @param toponymName
     * @param countryName
     * @param name
     */
    public Geoname(String toponymName, String countryName, String name) {
        super();
        this.toponymName = toponymName;
        this.countryName = countryName;
        this.name = name;
    }

    public String getToponymName() {
        return toponymName;
    }

    public void setToponymName(String toponymName) {
        this.toponymName = toponymName;
    }


    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getAdminName1() {
        return adminName1;
    }

    public void setAdminName1(String adminName1) {
        this.adminName1 = adminName1;
    }

}