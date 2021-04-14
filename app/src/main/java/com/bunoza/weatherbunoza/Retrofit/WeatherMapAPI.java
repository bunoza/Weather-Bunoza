package com.bunoza.weatherbunoza.Retrofit;

import com.bunoza.weatherbunoza.Weather.WeatherResults;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherMapAPI {
    @GET("weather")
    Observable<WeatherResults> getWeather(
            @Query(value = "lat") String lat,
            @Query(value = "lon") String lon,
            @Query(value = "appid") String appid,
            @Query(value = "units") String units
    );


}
