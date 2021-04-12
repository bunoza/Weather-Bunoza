package com.bunoza.weatherbunoza.City;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityResults {

    @SerializedName("totalResultsCount")
    @Expose
    private Integer totalResultsCount;
    @SerializedName("geonames")
    @Expose
    private List<Geoname> geonames = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public CityResults() {
    }

    /**
     *
     * @param totalResultsCount
     * @param geonames
     */
    public CityResults(Integer totalResultsCount, List<Geoname> geonames) {
        super();
        this.totalResultsCount = totalResultsCount;
        this.geonames = geonames;
    }

    public Integer getTotalResultsCount() {
        return totalResultsCount;
    }

    public void setTotalResultsCount(Integer totalResultsCount) {
        this.totalResultsCount = totalResultsCount;
    }

    public List<Geoname> getGeonames() {
        return geonames;
    }

    public void setGeonames(List<Geoname> geonames) {
        this.geonames = geonames;
    }

}




