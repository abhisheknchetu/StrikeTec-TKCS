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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalActivityTypeListAdapter extends ArrayAdapter<String> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<String> lists;
    private int currentposition;
    private boolean isgoalactivity;
//
    public GoalActivityTypeListAdapter(Context context, ArrayList<String> lists, boolean isgoalactivity){
        super(context, 0, lists);

        mContext = context;
        this.isgoalactivity = isgoalactivity;
        this.lists = lists;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<String> lists){
        this.lists = lists;
    }

    public void setCurrentposition(int currentposition){
        this.currentposition = currentposition;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        if (isgoalactivity){
            return lists.get(position);
        }else {
            return String.format(mContext.getResources().getString(R.string.goal_type), lists.get(position));
        }

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

        String data = getItem(position);

        viewHolder.nameView.setText(data);

        if (position == currentposition){
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

