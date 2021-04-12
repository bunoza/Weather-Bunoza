package com.bunoza.weatherbunoza.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientWeatherMap {
    private static final String BASE_API = "http://api.openweathermap.org/data/2.5/";
    private static Retrofit weatherAPI;

    public static WeatherMapAPI getApiInterface() {
        if (weatherAPI == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            return retrofit.create(WeatherMapAPI.class);
        }
        return weatherAPI.create(WeatherMapAPI.class);
    }

    public static WeatherMapDailyAPI getDailyApiInterface() {
        if (weatherAPI == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            return retrofit.create(WeatherMapDailyAPI.class);
        }
        return weatherAPI.create(WeatherMapDailyAPI.class);
    }

    private RetrofitClientWeatherMap(){

    }

}
