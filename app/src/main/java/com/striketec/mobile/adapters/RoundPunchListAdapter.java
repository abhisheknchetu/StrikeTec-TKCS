package com.striketec.mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.TrainingPunchDto;
import com.striketec.mobile.util.CommonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoundPunchListAdapter extends ArrayAdapter<TrainingPunchDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<TrainingPunchDto> punchResultDtos;

    public RoundPunchListAdapter(Context context,  ArrayList<TrainingPunchDto> punchResultDtos){
        super(context, 0, punchResultDtos);

        mContext = context;
        this.punchResultDtos = punchResultDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<TrainingPunchDto> punchResultDtos){
        this.punchResultDtos = punchResultDtos;
    }

    @Override
    public int getCount() {
        return punchResultDtos.size();
    }

    @Nullable
    @Override
    public TrainingPunchDto getItem(int position) {
        return punchResultDtos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_analysis_combopunch_child, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        TrainingPunchDto punchResultDto = getItem(position);

        //set value
        viewHolder.desiredTypeView.setText(String.valueOf(position + 1));
        viewHolder.desiredTypeView.setTextColor(mContext.getResources().getColor(R.color.white));
        viewHolder.typeView.setText(CommonUtils.getTypefromvalue(punchResultDto.mPunchType));
        viewHolder.handView.setText(CommonUtils.getHandfromChar(punchResultDto.mHand));
        viewHolder.durationView.setText(String.valueOf(punchResultDto.mPunchDuration));
        viewHolder.speedView.setText(String.valueOf((int)punchResultDto.mSpeed));
        viewHolder.powerView.setText(String.valueOf((int)punchResultDto.mForce));

        int trainingStatus = CommonUtils.compareForce(punchResultDto);

        if (trainingStatus == 0){
            viewHolder.desiredTypeView.setBackgroundResource(R.drawable.bg_rectangle_bad);
            viewHolder.typeLabel.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.typeView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.handView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.durationLabel.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.durationView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.durationUnitView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.speedLabel.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.speedView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.speedUnitView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.powerLabel.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.powerView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.powerUnitView.setTextColor(mContext.getResources().getColor(R.color.force_color));
        }else if (trainingStatus == 1){
            viewHolder.desiredTypeView.setBackgroundResource(R.drawable.bg_rectangle_average);
            viewHolder.typeLabel.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.typeView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.handView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.durationLabel.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.durationView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.durationUnitView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.speedLabel.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.speedView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.speedUnitView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.powerLabel.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.powerView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.powerUnitView.setTextColor(mContext.getResources().getColor(R.color.orange));
        }else {
            viewHolder.desiredTypeView.setBackgroundResource(R.drawable.bg_rectangle_above);
            viewHolder.typeLabel.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.typeView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.handView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.durationLabel.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.durationView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.durationUnitView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.speedLabel.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.speedView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.speedUnitView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.powerLabel.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.powerView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.powerUnitView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
        }

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.desiredtype) TextView desiredTypeView;

        @BindView(R.id.label_type) TextView typeLabel;
        @BindView(R.id.type) TextView typeView;
        @BindView(R.id.hand) TextView handView;

        @BindView(R.id.label_time) TextView durationLabel;
        @BindView(R.id.duration) TextView durationView;
        @BindView(R.id.duration_unit) TextView durationUnitView;

        @BindView(R.id.label_speed) TextView speedLabel;
        @BindView(R.id.speed) TextView speedView;
        @BindView(R.id.speed_unit) TextView speedUnitView;

        @BindView(R.id.label_power) TextView powerLabel;
        @BindView(R.id.power) TextView powerView;
        @BindView(R.id.power_unit) TextView powerUnitView;


        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

