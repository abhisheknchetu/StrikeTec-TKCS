package com.striketec.mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.PresetUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionListAdapter extends ArrayAdapter<TrainingSessionDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<TrainingSessionDto> sessionLists;

    public SessionListAdapter(Context context, ArrayList<TrainingSessionDto> sessionLists){
        super(context, 0, sessionLists);

        mContext = context;
        this.sessionLists = sessionLists;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<TrainingSessionDto> sessionLists){
        this.sessionLists = sessionLists;
    }

    @Override
    public int getCount() {
        return sessionLists.size();
    }

    @Nullable
    @Override
    public TrainingSessionDto getItem(int position) {
        return sessionLists.get(position);
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
            convertView = inflater.inflate(R.layout.item_analysis_session_child, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        TrainingSessionDto sessionDto = getItem(position);

        viewHolder.yearmonthView.setText(DatesUtil.getYearMonth(sessionDto.mStartTime));
        viewHolder.dateView.setText(DatesUtil.getDate(sessionDto.mStartTime));
        viewHolder.timeValueView.setText(PresetUtil.changeSecondsToMinutes((int)((Long.parseLong(sessionDto.mEndTime) - Long.parseLong(sessionDto.mStartTime)) / 1000)));
        viewHolder.punchesView.setText(String.valueOf(sessionDto.mPunchCount));
        viewHolder.roundsView.setText(String.valueOf(sessionDto.mRoundIds.size()));

        if (sessionDto.mTrainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_COMBINATION) || sessionDto.mTrainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_SETS)){
            viewHolder.roundParentView.setVisibility(View.INVISIBLE);
            viewHolder.rounddivider.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.roundParentView.setVisibility(View.VISIBLE);
            viewHolder.rounddivider.setVisibility(View.VISIBLE);
        }

        if (sessionDto.mTrainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_QUICKSTART)){
            viewHolder.trainingTypeView.setText(AppConst.TRAINING_TYPE_QUICKSTART_STRING);
        }else if (sessionDto.mTrainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_ROUND)){
            viewHolder.trainingTypeView.setText(AppConst.TRAINING_TYPE_ROUND_STRING);
        }else if (sessionDto.mTrainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_COMBINATION)){
            viewHolder.trainingTypeView.setText(AppConst.TRAINING_TYPE_COMBINATION_STRING);
        }else if (sessionDto.mTrainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_SETS)){
            viewHolder.trainingTypeView.setText(AppConst.TRAINING_TYPE_SETS_STRING);
        }else{
            viewHolder.trainingTypeView.setText(AppConst.TRAINING_TYPE_WORKOUT_STRING);
        }

        int trainingStatus = CommonUtils.compareCount(sessionDto);

        if (trainingStatus == 0){
            viewHolder.dateView.setBackgroundResource(R.drawable.bg_rectangle_bad);
            viewHolder.sessionTimeView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.timeValueView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.punchesLabel.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.punchesView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.roundsLabel.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.roundsView.setTextColor(mContext.getResources().getColor(R.color.force_color));
        }else if (trainingStatus == 1){
            viewHolder.dateView.setBackgroundResource(R.drawable.bg_rectangle_average);
            viewHolder.sessionTimeView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.timeValueView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.punchesLabel.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.punchesView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.roundsLabel.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.roundsView.setTextColor(mContext.getResources().getColor(R.color.orange));
        }else {
            viewHolder.dateView.setBackgroundResource(R.drawable.bg_rectangle_above);
            viewHolder.sessionTimeView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.timeValueView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.punchesLabel.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.punchesView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.roundsLabel.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.roundsView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
        }

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.yearmonth) TextView yearmonthView;
        @BindView(R.id.date) TextView dateView;
        @BindView(R.id.training_type) TextView trainingTypeView;
        @BindView(R.id.label_time) TextView sessionTimeView;
        @BindView(R.id.time) TextView timeValueView;
        @BindView(R.id.label_punches) TextView punchesLabel;
        @BindView(R.id.punches) TextView punchesView;
        @BindView(R.id.label_round) TextView roundsLabel;
        @BindView(R.id.rounds) TextView roundsView;
        @BindView(R.id.rounddivider) View rounddivider;
        @BindView(R.id.roundparent)  LinearLayout roundParentView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

