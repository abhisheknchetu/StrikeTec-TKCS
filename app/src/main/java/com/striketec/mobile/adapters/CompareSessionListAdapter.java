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
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.PresetUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompareSessionListAdapter extends ArrayAdapter<TrainingSessionDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<TrainingSessionDto> sessionDtos;

    public CompareSessionListAdapter(Context context, ArrayList<TrainingSessionDto> sessionDtos){
        super(context, 0, sessionDtos);

        mContext = context;
        this.sessionDtos = sessionDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<TrainingSessionDto> sessionDtos){
        this.sessionDtos = sessionDtos;
    }

    @Override
    public int getCount() {
        return sessionDtos.size();
    }

    @Nullable
    @Override
    public TrainingSessionDto getItem(int position) {
        return sessionDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_compare_selectround_child, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        TrainingSessionDto sessionDto = getItem(position);
        viewHolder.yearmonthView.setText(DatesUtil.getYearMonth(sessionDto.mStartTime));
        viewHolder.dateView.setText(DatesUtil.getDate(sessionDto.mStartTime));
        viewHolder.roundDurationView.setText(PresetUtil.changeSecondsToMinutes((int)((Long.parseLong(sessionDto.mEndTime) - Long.parseLong(sessionDto.mStartTime)) / 1000)));
        viewHolder.avgtimeView.setText(String.valueOf(0.5f));
        viewHolder.avgforceView.setText(String.valueOf((int)sessionDto.mAvgForce));
        viewHolder.avgspeedView.setText(String.valueOf((int)sessionDto.mAvgSpeed));

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.yearmonth) TextView yearmonthView;
        @BindView(R.id.date) TextView dateView;
        @BindView(R.id.duration) TextView roundDurationView;
        @BindView(R.id.avgtime) TextView avgtimeView;
        @BindView(R.id.avgspeed) TextView avgspeedView;
        @BindView(R.id.avgpower) TextView avgforceView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

