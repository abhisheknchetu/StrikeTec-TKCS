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
import com.striketec.mobile.dto.TrainingRoundDto;
import com.striketec.mobile.util.PresetUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoundListAdapter extends ArrayAdapter<TrainingRoundDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<TrainingRoundDto> roundLists;

    public RoundListAdapter(Context context, ArrayList<TrainingRoundDto> roundLists){
        super(context, 0, roundLists);

        mContext = context;
        this.roundLists = roundLists;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<TrainingRoundDto> roundLists){
        this.roundLists = roundLists;
    }

    @Override
    public int getCount() {
        return roundLists.size();
    }

    @Nullable
    @Override
    public TrainingRoundDto getItem(int position) {
        return roundLists.get(position);
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
            convertView = inflater.inflate(R.layout.item_analysis_round_child, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        TrainingRoundDto roundDto = getItem(position);
        viewHolder.roundIndexView.setText(String.valueOf(position + 1));
        viewHolder.roundDurationView.setText(PresetUtil.changeSecondsToMinutes((int)((Long.parseLong(roundDto.mEndTime) - Long.parseLong(roundDto.mStartTime)) / 1000)));
        viewHolder.besttimeView.setText(String.valueOf(roundDto.mBestTime));
        viewHolder.maxpowerView.setText(String.valueOf((int)roundDto.mMaxForce));
        viewHolder.maxspeedView.setText(String.valueOf((int)roundDto.mMaxSpeed));

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.roundindex) TextView roundIndexView;
        @BindView(R.id.duration) TextView roundDurationView;
        @BindView(R.id.besttime) TextView besttimeView;
        @BindView(R.id.maxspeed) TextView maxspeedView;
        @BindView(R.id.maxpower) TextView maxpowerView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

