package com.bunoza.weatherbunoza;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bunoza.weatherbunoza.Database.AppDatabase;
import com.bunoza.weatherbunoza.Database.Data;
import com.bunoza.weatherbunoza.Retrofit.RetrofitClientWeatherMap;
import com.bunoza.weatherbunoza.Retrofit.WeatherMapAPI;
import com.bunoza.weatherbunoza.Retrofit.WeatherMapDailyAPI;
import com.bunoza.weatherbunoza.Weather.Daily;
import com.bunoza.weatherbunoza.Weather.WeatherResults;
import com.bunoza.weatherbunoza.Weather.WeatherResultsDaily;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";
    public final String APIKey = "95f7b6c15f8d7474dbef5f9b97033305";
    private static final int PERMISSIONS_FINE_LOCATION=99;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    WeatherMapAPI weatherMapAPI;
    TextView tvDescription;
    TextView tvTemperature;
    TextView tvTomorrow, tv2Days, tv3Days, tvTomorrowTemp, tv2DaysTemp, tv3DaysTemp, tvHumidity, tvPressure, tvWind;
    Data lastCity;
    AppDatabase db;
    LottieAnimationView mainAnimationView;
    LottieAnimationView animationView1;
    LottieAnimationView animationView2;
    LottieAnimationView animationView3;
    private WeatherMapDailyAPI weatherMapDailyAPI;
    private SimpleLocation location;
    SwipeRefreshLayout swipeRefreshLayout;
    int refreshCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setErrorHandler();
        initAPI();
        initUI();
        initDB();
    }

    private void setErrorHandler() {
        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof IOException) {
                compositeDisposable.clear();
                tvDescription.setText(String.format(getResources().getString(R.string.dataError), lastCity.getCity()));
                tvTemperature.setText("?°C");
                setNoDataAnimation(mainAnimationView);
                setNoDataAnimationDaily();
            }
        });
    }

    private void initAPI() {
        weatherMapAPI = RetrofitClientWeatherMap.getApiInterface();
        weatherMapDailyAPI = RetrofitClientWeatherMap.getDailyApiInterface();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(isInternetAvailable(this)){
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please enable internet connection for location search", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDB() {
        if(db == null){
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "data-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
            Log.d(TAG, "onCreate: nema baze, stvaram");
            Log.d(TAG, "initDB: " + db.dataDao().getFirst());

        }
        if(db.dataDao().getFirst() != null){
            lastCity = db.dataDao().getFirst();
            Log.d(TAG, "initDB: " + lastCity.getLat() + lastCity.getLon());
//            fetchWeatherData(lastCity);
//            fetchWeatherDataDaily(lastCity);
        }
        checkForLocationPermission();

    }

    private void checkForLocationPermission() {

        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            location = new SimpleLocation(MainActivity.this);
            Log.d(TAG, "initDB: USPJEH " + location.getLatitude() + " " + location.getLongitude());

            gainLocation();


        }else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
//                Log.d(TAG, "initDB: " + location.getLatitude() + " " + location.getLongitude());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gainLocation();
            } else {
                Toast.makeText(this, "You can still use the app, but you have to search for your location manually", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void gainLocation() {
        location = new SimpleLocation(this);
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Log.d("Addresses","-->"+addresses);
            String result = addresses.get(0).getLocality().trim();
            Log.d(TAG, "initDB: " + result);
            insertIntoDB(new Data(result, Double.toString(location.getLatitude()), Double.toString(location.getLongitude())));
            fetchWeatherDataDaily(db.dataDao().getFirst());
            fetchWeatherData(db.dataDao().getFirst());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void insertIntoDB(Data data){
        if (db.dataDao().getFirst() != null) {
            db.dataDao().delete(db.dataDao().getFirst());
        }
        db.dataDao().insert(data);
    }

    private void fetchWeatherData(Data s){
            compositeDisposable.add(weatherMapAPI.getWeather(s.getLat(), s.getLon(), APIKey, "metric")
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(weatherResults -> {
                        Log.d(TAG, "accept: " + weatherResults.getWeather().get(0).getDescription());
                        updateWeather(weatherResults);
                        refreshCounter++;
                        terminateRefreshing();
                    }, throwable -> {
//                                       compositeDisposable.clear();
//                                       Log.e(TAG, "accept: " + throwable);
//                                       tvDescription.setText(String.format(getResources().getString(R.string.dataError), s));
//                                       tvTemperature.setText("?°C");
//                                       setNoDataAnimation(mainAnimationView);
                    }
                    ));
    }

    private void fetchWeatherDataDaily(Data s){
        Log.d(TAG, "fetchWeatherDataDaily: " + s.getLat()+ " "  + s.getLon());
        compositeDisposable.add(weatherMapDailyAPI.getWeatherDaily(s.getLat(), s.getLon(), "hourly,minutely,alerts,current", "metric", APIKey)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherResultsDaily -> {
                    Log.d(TAG, "accept: daily" + weatherResultsDaily.toString());
                    updateWeatherDaily(weatherResultsDaily.getDaily());
                    updateDates(weatherResultsDaily);
                    updateTemps(weatherResultsDaily);
                    refreshCounter++;
                    terminateRefreshing();
                }, throwable -> {
//                                   Log.e(TAG, "accept: " + throwable);
//                                   setNoDataAnimationDaily();
                           }
                ));
    }

    @SuppressLint("StringFormatMatches")
    private void updateTemps(WeatherResultsDaily weatherResultsDaily) {
        tvTomorrowTemp.setText(String.format(getResources().getString(R.string.temp_no_c), weatherResultsDaily.getDaily().get(1).getTemp().getMax().intValue(), getResources().getString(R.string.c)));
        tv2DaysTemp.setText(String.format(getResources().getString(R.string.temp_no_c), weatherResultsDaily.getDaily().get(2).getTemp().getMax().intValue(), getResources().getString(R.string.c)));
        tv3DaysTemp.setText(String.format(getResources().getString(R.string.temp_no_c), weatherResultsDaily.getDaily().get(3).getTemp().getMax().intValue(), getResources().getString(R.string.c)));
    }

    private void updateDates(WeatherResultsDaily weatherResultsDaily) {
        int i;
        for(i = 1; i < 4; i++){
            Date date = new Date((weatherResultsDaily.getDaily().get(i).getDt() + weatherResultsDaily.getTimezoneOffset())*1000L);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.");
            String formatted = sdf.format(date);
            if (i == 1) {
                tvTomorrow.setText(formatted);
            } else if (i == 2) {
                tv2Days.setText(formatted);
            } else {
                tv3Days.setText(formatted);
            }
        }

    }

    private void setNoDataAnimationDaily() {
        animationView1.setAnimation(getResources().getIdentifier("nodataanimation", "raw", getPackageName()));
        animationView2.setAnimation(getResources().getIdentifier("nodataanimation", "raw", getPackageName()));
        animationView3.setAnimation(getResources().getIdentifier("nodataanimation", "raw", getPackageName()));

        animationView1.playAnimation();
        animationView2.playAnimation();
        animationView3.playAnimation();
    }

    private void updateWeather(WeatherResults weatherResults) {
        tvDescription.setText(weatherResults.getWeather().get(0).getDescription());
        Log.d(TAG, "updateWeather: " + weatherResults.getWeather().get(0).getIcon());
        pickAnimation(weatherResults.getWeather().get(0).getIcon(), mainAnimationView);
        tvTemperature.setText(String.format(getResources().getString(R.string.currentTemperature), db.dataDao().getFirst().getCity(), weatherResults.getMain().getTemp().toString()));
        tvHumidity.setText(String.format(getResources().getString(R.string.humid), weatherResults.getMain().getHumidity().toString(), getResources().getString(R.string.percent)));
        tvWind.setText(String.format(getResources().getString(R.string.wind), weatherResults.getWind().getSpeed().toString()));
        tvPressure.setText(String.format(getResources().getString(R.string.pressure), weatherResults.getMain().getPressure().toString()));

    }

    private void updateWeatherDaily(List<Daily> daily) {
        Log.d(TAG, "updateWeather: " + daily.get(1).getDt());
        pickAnimation(daily.get(1).getWeather().get(0).getIcon(), animationView1);
        pickAnimation(daily.get(2).getWeather().get(0).getIcon(), animationView2);
        pickAnimation(daily.get(3).getWeather().get(0).getIcon(), animationView3);
    }


    private void pickAnimation(String s, LottieAnimationView anim) {
        switch (s){
            case "01d": anim.setAnimationFromUrl(getResources().getString(R.string.clearskyday));  break;
            case "01n": anim.setAnimationFromUrl(getResources().getString(R.string.clearskynight)); break;
            case "02d": anim.setAnimationFromUrl(getResources().getString(R.string.fewcloudsday)); break;
            case "02n": anim.setAnimationFromUrl(getResources().getString(R.string.fewcloudsnight)); break;
            case "03d":
            case "03n":
                        anim.setAnimationFromUrl(getResources().getString(R.string.scatteredclouds)); break;
            case "04d":
            case "04n":
                        anim.setAnimationFromUrl(getResources().getString(R.string.brokenclouds)); break;
            case "09d": anim.setAnimationFromUrl(getResources().getString(R.string.showerrainday)); break;
            case "09n": anim.setAnimationFromUrl(getResources().getString(R.string.showerrainnight)); break;
            case "10d": anim.setAnimationFromUrl(getResources().getString(R.string.rainday)); break;
            case "10n": anim.setAnimationFromUrl(getResources().getString(R.string.rainnight)); break;
            case "11d":
            case "11n":
                        anim.setAnimationFromUrl(getResources().getString(R.string.thunderstorm)); break;
            case "13d":
            case "13n":
                        anim.setAnimationFromUrl(getResources().getString(R.string.snow)); break;
            case "50d":
            case "50n":
                        anim.setAnimationFromUrl(getResources().getString(R.string.mist)); break;
            default: setNoDataAnimation(anim); break;
        }
        anim.playAnimation();

    }

    private void setNoDataAnimation(LottieAnimationView anim){
        anim.setAnimation(getResources().getIdentifier("nodataanimation", "raw", getPackageName()));
        anim.playAnimation();
    }

    public boolean isInternetAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void initUI() {
        tvDescription = findViewById(R.id.tvHelloWorld);
        mainAnimationView = findViewById(R.id.mainAnimationView);
        tvTemperature = findViewById(R.id.tvTemperature);
        animationView1 = findViewById(R.id.animationView1);
        animationView2 = findViewById(R.id.animationView2);
        animationView3 = findViewById(R.id.animationView3);
        tvTomorrow = findViewById(R.id.tvTomorrow);
        tv2Days = findViewById(R.id.tv2Days);
        tv3Days = findViewById(R.id.tv3Days);
        tvTomorrowTemp = findViewById(R.id.tvTomorrowTemp);
        tv2DaysTemp = findViewById(R.id.tv2DaysTemp);
        tv3DaysTemp = findViewById(R.id.tv3DaysTemp);
        tvPressure = findViewById(R.id.tvPressure);
        tvWind = findViewById(R.id.tvWind);
        tvHumidity = findViewById(R.id.tvHumidity);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(isInternetAvailable(this)){
                refreshCounter = 0;
                checkForLocationPermission();
                fetchWeatherDataDaily(db.dataDao().getFirst());
                fetchWeatherData(db.dataDao().getFirst());
            }else{
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Internet connection is needed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void terminateRefreshing(){
        if(refreshCounter == 2){
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    protected void onStop() {
        compositeDisposable.clear();
        db.close();
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(db != null){
            if (db.dataDao().getFirst() != null) {
                fetchWeatherData(db.dataDao().getFirst());
                Log.d(TAG, "onPostResume:" + db.dataDao().getFirst().getLon());
                Log.d(TAG, "onPostResume:" + db.dataDao().getFirst().getLon());
                fetchWeatherDataDaily(db.dataDao().getFirst());
            }
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(db != null){
//            if (db.dataDao().getFirst() != null) {
//                fetchWeatherData(db.dataDao().getFirst().getCity());
//            }
//        }
//    }

}

