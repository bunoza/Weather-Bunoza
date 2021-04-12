package com.bunoza.weatherbunoza.Recycler;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunoza.weatherbunoza.R;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView tvItem;
    private ItemClickListener clickListener;


    public ViewHolder(@NonNull View itemView, ItemClickListener listener ) {
        super(itemView);
        this.clickListener = listener;
        tvItem =  itemView.findViewById(R.id.tvItem);
        itemView.setOnClickListener(this);
    }

    public void setText(String text){
        tvItem.setText(text);
    }

    @Override
    public void onClick(View v) {
        clickListener.onItemClick(getAbsoluteAdapterPosition());
    }
}
