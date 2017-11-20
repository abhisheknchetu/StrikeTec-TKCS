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
import com.striketec.mobile.dto.PunchwithDesiredDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.ComboSetUtil;
import com.striketec.mobile.util.CommonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CombinationPunchListAdapter extends ArrayAdapter<PunchwithDesiredDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<PunchwithDesiredDto> punchResultDtos;

    public CombinationPunchListAdapter(Context context,  ArrayList<PunchwithDesiredDto> punchResultDtos){
        super(context, 0, punchResultDtos);

        mContext = context;
        this.punchResultDtos = punchResultDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<PunchwithDesiredDto> punchResultDtos){
        this.punchResultDtos = punchResultDtos;
    }

    @Override
    public int getCount() {
        return punchResultDtos.size();
    }

    @Nullable
    @Override
    public PunchwithDesiredDto getItem(int position) {
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

        PunchwithDesiredDto punchResultDto = getItem(position);

        //set value
        viewHolder.desiredTypeView.setText(punchResultDto.mDesiredPunchKey);

        boolean success = false;

        if (punchResultDto.mDesiredPunchKey.length() > 1){
            //this is movement
            success = true;
            viewHolder.typeView.setText("-");
            viewHolder.handView.setText("-");
            viewHolder.durationView.setText("-");
            viewHolder.speedView.setText("-");
            viewHolder.powerView.setText("-");
        }else {
            if (punchResultDto.mPunch == null){
                //this is missing punch
                success = false;
                viewHolder.typeView.setText("-");
                viewHolder.handView.setText("-");
                viewHolder.durationView.setText("-");
                viewHolder.speedView.setText("-");
                viewHolder.powerView.setText("-");
            }else {
                viewHolder.typeView.setText(CommonUtils.getTypefromvalue(punchResultDto.mPunch.mPunchType));
                viewHolder.handView.setText(CommonUtils.getHandfromChar(punchResultDto.mPunch.mHand));
                viewHolder.durationView.setText(String.valueOf(punchResultDto.mPunch.mPunchDuration));
                viewHolder.speedView.setText(String.valueOf((int)punchResultDto.mPunch.mSpeed));
                viewHolder.powerView.setText(String.valueOf((int)punchResultDto.mPunch.mForce));

                String successString = ComboSetUtil.punchTypeMap.get(punchResultDto.mDesiredPunchKey);

                if (successString.equalsIgnoreCase(AppConst.JAB) || successString.equalsIgnoreCase(AppConst.STRAIGHT)){
                    if (CommonUtils.getTypefromvalue(punchResultDto.mPunch.mPunchType).equalsIgnoreCase(successString)){
                        success = true;
                    }else {
                        success = false;
                    }
                }else {
                    String desiredString = CommonUtils.getHandfromChar(punchResultDto.mPunch.mHand) + " " + CommonUtils.getTypefromvalue(punchResultDto.mPunch.mPunchType);

                    if (desiredString.equalsIgnoreCase(successString)){
                        success = true;
                    }else {
                        success = false;
                    }
                }
            }
        }

        //update view

        if (success){
            viewHolder.desiredTypeView.setBackgroundResource(R.drawable.punch_success_first);
            viewHolder.typeLabel.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.typeView.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.handView.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.durationLabel.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.durationView.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.durationUnitView.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.speedLabel.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.speedView.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.speedUnitView.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.powerLabel.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.powerView.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.powerUnitView.setTextColor(mContext.getResources().getColor(R.color.punches_text));
        }else {
            viewHolder.desiredTypeView.setBackgroundResource(R.drawable.punch_fail_first);
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

