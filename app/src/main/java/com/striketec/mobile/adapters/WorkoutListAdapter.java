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
import com.striketec.mobile.dto.PresetDto;
import com.striketec.mobile.dto.WorkoutDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkoutListAdapter extends ArrayAdapter<WorkoutDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<WorkoutDto> workoutDtos;
    private int currentworkoutPosition;
//
    public WorkoutListAdapter(Context context, ArrayList<WorkoutDto> workoutDtos, int currentworkoutPosition){
        super(context, 0, workoutDtos);

        mContext = context;
        this.currentworkoutPosition = currentworkoutPosition;
        this.workoutDtos = workoutDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<WorkoutDto> workoutDtos){
        this.workoutDtos = workoutDtos;
    }

    @Override
    public int getCount() {
        return workoutDtos.size();
    }

    @Nullable
    @Override
    public WorkoutDto getItem(int position) {
        return workoutDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_defaultstringlist, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        WorkoutDto presetDTO = getItem(position);
        viewHolder.nameView.setText(presetDTO.getName());

        if (position == currentworkoutPosition){
            viewHolder.nameView.setBackgroundColor(mContext.getResources().getColor(R.color.selectbg));
        }else {
            viewHolder.nameView.setBackgroundColor(mContext.getResources().getColor(R.color.popup_bg));
        }

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.name)
        TextView nameView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

