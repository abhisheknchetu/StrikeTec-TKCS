package com.striketec.mobile.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.GoalDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalListAdapter extends ArrayAdapter<GoalDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<GoalDto> goalDtos;

    public GoalListAdapter(Context context, ArrayList<GoalDto> goalDtos){
        super(context, 0, goalDtos);

        mContext = context;
        this.goalDtos = goalDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<GoalDto> goalDtos){
        this.goalDtos = goalDtos;
    }

    @Override
    public int getCount() {
        return goalDtos.size();
    }

    @Nullable
    @Override
    public GoalDto getItem(int position) {
        return goalDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_goal_list, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        GoalDto goalDto = getItem(position);

        viewHolder.goalView.setText(goalDto.mActivity + " " + mContext.getResources().getString(R.string.goal));
        String description = String.format(mContext.getString(R.string.goal_description), goalDto.mGoal, goalDto.mActivity, goalDto.mType);
        viewHolder.goalDescription.setText(description);

        int percent =(int)( goalDto.mFinished / (float)goalDto.mGoal * 100);
        viewHolder.progressBar.setMax(100);
        viewHolder.progressBar.setProgress(percent);
        viewHolder.progressValue.setText(percent + "%");

        if (percent < 30){
            viewHolder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.customprogress_belowgoalbar));
            viewHolder.progressValue.setTextColor(mContext.getResources().getColor(R.color.force_color));
        }else if (percent < 60){
            viewHolder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.customprogress_averagegoalbar));
            viewHolder.progressValue.setTextColor(mContext.getResources().getColor(R.color.orange));
        }else {
            viewHolder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.customprogress_abovegoalbar));
            viewHolder.progressValue.setTextColor(mContext.getResources().getColor(R.color.speed_color));
        }

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.goal) TextView goalView;
        @BindView(R.id.settings) ImageView settingsView;
        @BindView(R.id.description) TextView goalDescription;
        @BindView(R.id.progress_bar) ProgressBar progressBar;
        @BindView(R.id.progress_value) TextView progressValue;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

