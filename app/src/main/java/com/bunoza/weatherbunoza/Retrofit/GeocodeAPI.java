package com.bunoza.weatherbunoza.Retrofit;

import com.bunoza.weatherbunoza.City.CityResults;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodeAPI {
    @GET("searchJSON")
    Observable<CityResults> getCities(
            @Query(value = "name_startsWith") String q,
            @Query(value = "username") String username,
            @Query(value = "maxRows") Integer maxRows,
            @Query(value = "fcodeName") String fcodeName
            );
}
