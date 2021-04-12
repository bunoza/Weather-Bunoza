package com.bunoza.weatherbunoza.Recycler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunoza.weatherbunoza.City.CityResults;
import com.bunoza.weatherbunoza.City.Geoname;
import com.bunoza.weatherbunoza.R;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ItemClickListener clickListener;
    List<Geoname> data = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(data.get(position).getCountryName() != null){
            if(data.get(position).getAdminName1() != null || !data.get(position).getAdminName1().trim().equals("")){
                holder.setText(data.get(position).getToponymName() + ", "+ data.get(position).getAdminName1() + ", "
                        + data.get(position).getCountryName());
            }else{
                holder.setText(data.get(position).getToponymName() + ", "+ data.get(position).getCountryName());
            }

        }
    }

    public RecyclerAdapter(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public Geoname getItemOn(int index){
        try{
            Geoname temp =  data.get(index);
        }catch (Exception e){
            Log.e("Recycler", "getItemOn: Unable to get Geoname at" + index );
        }
        return  data.get(index);
    }

    public void addData(CityResults data){
        this.data.clear();
        this.data.addAll(data.getGeonames());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
       return this.data.size();
    }

}
