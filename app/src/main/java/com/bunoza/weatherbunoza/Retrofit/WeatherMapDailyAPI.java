package com.bunoza.weatherbunoza.Retrofit;

import com.bunoza.weatherbunoza.Weather.Daily;
import com.bunoza.weatherbunoza.Weather.WeatherResults;
import com.bunoza.weatherbunoza.Weather.WeatherResultsDaily;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherMapDailyAPI {

    @GET("onecall")
    Observable<WeatherResultsDaily> getWeatherDaily(
            @Query(value = "lat") String lat,
            @Query(value = "lon") String lon,
            @Query(value = "exclude") String exclude,
            @Query(value = "units") String metric,
            @Query(value = "appid") String appid
    );
}
