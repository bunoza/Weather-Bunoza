package com.bunoza.weatherbunoza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.bunoza.weatherbunoza.Database.AppDatabase;
import com.bunoza.weatherbunoza.Database.Data;
import com.bunoza.weatherbunoza.City.CityResults;
import com.bunoza.weatherbunoza.Recycler.ItemClickListener;
import com.bunoza.weatherbunoza.Recycler.RecyclerAdapter;
import com.bunoza.weatherbunoza.Retrofit.GeocodeAPI;
import com.bunoza.weatherbunoza.Retrofit.RetrofitClientGeo;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity implements ItemClickListener {

    public final String TAG = "SearchActivity";
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final String GeoUsername = "domagojbu";


    GeocodeAPI geocodeAPI;
    SearchView searchView;
    private RecyclerView recycler;
    private RecyclerAdapter adapter;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initUI();
        setupRecycler();
        initGeoApi();
        initDB();

    }

    private void setupRecycler(){
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(this);
        recycler.setAdapter(adapter);
    }

    private void initDB() {
        if(db == null){
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "data-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
            Log.d(TAG, "SearchActivity: nema baze, stvaram: " + db.dataDao().getElementCount());
        }
    }

    private void setupRecyclerData(CityResults cityResults){
        Log.d(TAG, "setupRecyclerData: dodavanje podataka u adapter");
        adapter.addData(cityResults);
        Log.d(TAG, "setupRecyclerData: podaci dodani u adapter");
    }

    private void initUI() {
        recycler = findViewById(R.id.rvRecycler);
        searchView = findViewById(R.id.svSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit: KREĆE API CALL");
                fetchCitiesData(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.trim().length() >2)
                fetchCitiesData(s);
                return false;
            }
        });
    }

    private void initGeoApi(){
        geocodeAPI = RetrofitClientGeo.getApiInterface();
    }

    private void fetchCitiesData(String s){
        compositeDisposable.add(geocodeAPI.getCities(s, GeoUsername, 5, "PPL")
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Consumer<CityResults>() {
                                      @Override
                                      public void accept(CityResults cityResults) {
                                          Log.d(TAG, "accept: pred postavljanje podataka");
                                          setupRecyclerData(cityResults);
                                          Log.d(TAG, "accept: uspješno postavljeni podaci");

                                      }
                                  }, new Consumer<Throwable>() {
                                      @Override
                                      public void accept(Throwable throwable) throws Exception {
                                          Log.e(TAG, throwable.getMessage(), throwable);
                                      }
                                  }
                        ));
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        db.close();
        super.onStop();
    }

    @Override
    public void onItemClick(int position) {
        if(db.dataDao().getFirst() == null){
            db.dataDao().insert(new Data(adapter.getItemOn(position).getToponymName(), adapter.getItemOn(position).getLat().toString(), adapter.getItemOn(position).getLng().toString()));
        }else{
            db.dataDao().delete(db.dataDao().getFirst());

            db.dataDao().insert(new Data(adapter.getItemOn(position).getToponymName(), adapter.getItemOn(position).getLat().toString(), adapter.getItemOn(position).getLng().toString()));
            Log.d(TAG, "onItemClick: " + db.dataDao().getAll().get(0).getCity());
            db.close();
        }
        compositeDisposable.clear();
        finish();
    }


}