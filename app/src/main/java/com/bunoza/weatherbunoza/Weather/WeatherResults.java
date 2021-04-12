
package com.bunoza.weatherbunoza.Weather;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherResults {

    @SerializedName("coord")
    @Expose
    private Coord coord;
    @SerializedName("weather")
    @Expose
    private List<Weather> weather = null;
    @SerializedName("visibility")
    @Expose
    private Integer visibility;
    @SerializedName("wind")
    @Expose
    private Wind wind;
    @SerializedName("clouds")
    @Expose
    private Clouds clouds;
    @SerializedName("main")
    @Expose
    private Main main;


    /**
     * No args constructor for use in serialization
     * 
     */
    public WeatherResults() {
    }

    /**
     * 
     * @param visibility
     * @param clouds
     * @param weather
     * @param wind
     */
    public WeatherResults(List<Weather> weather, Integer visibility, Wind wind, Clouds clouds, Main main) {
        super();
        this.weather = weather;
        this.visibility = visibility;
        this.wind = wind;
        this.clouds = clouds;
        this.main = main;
    }


    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }


    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

}
