package com.striketec.mobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.GoalDto;
import com.striketec.mobile.dto.SensorOrderDto;
import com.striketec.mobile.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SensororderListAdapter extends RecyclerView.Adapter<SensororderListAdapter.ViewHolder> {

    private int itemWidth;
    private ArrayList<SensorOrderDto> sensorOrderDtos;

    public SensororderListAdapter (ArrayList<SensorOrderDto> dtos){
        this.sensorOrderDtos = dtos;
    }

    public void setData (ArrayList<SensorOrderDto> sensorOrderDtos){
        this.sensorOrderDtos = sensorOrderDtos;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        Activity context = (Activity)recyclerView.getContext();
        Point windowDimensions = new Point();
        context.getWindowManager().getDefaultDisplay().getSize(windowDimensions);
        itemWidth = Math.round(windowDimensions.x * 0.8f);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_sensororder, parent, false);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(itemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
//        ViewGroup.MarginLayoutParams params1 = new ViewGroup.LayoutParams();
        int margin = (int)(CommonUtils.dpTopx(parent.getContext().getResources().getInteger(R.integer.int_5)) * 1.5f);
        params.setMargins(margin, margin, margin, margin);
        view.setLayoutParams(params);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sensorView.setImageResource(sensorOrderDtos.get(position).mResId);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return sensorOrderDtos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.sensor_img) ImageView sensorView;
        @BindView(R.id.container)
        LinearLayout parentView;


        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setOverlay(float alpha){
            parentView.setAlpha(alpha);
        }
    }
}

