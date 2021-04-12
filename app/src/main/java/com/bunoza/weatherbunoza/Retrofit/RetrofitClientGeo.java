package com.bunoza.weatherbunoza.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientGeo {
    private static final String BASE_API = "http://api.geonames.org/";
    private static Retrofit geocodeAPI;

    public static GeocodeAPI getApiInterface() {
        if (geocodeAPI == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            return retrofit.create(GeocodeAPI.class);
        }
        return geocodeAPI.create(GeocodeAPI.class);
    }

    private RetrofitClientGeo(){

    }
}
