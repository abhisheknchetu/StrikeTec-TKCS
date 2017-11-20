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

public class PresetListAdapter extends ArrayAdapter<PresetDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<PresetDto> presetList;
    private int currentPresetPosition;
//
    public PresetListAdapter(Context context, ArrayList<PresetDto> presetList, int currentPresetPosition){
        super(context, 0, presetList);

        mContext = context;
        this.currentPresetPosition = currentPresetPosition;
        this.presetList = presetList;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<PresetDto> presetList){
        this.presetList = presetList;
    }

    @Override
    public int getCount() {
        return presetList.size() + 1;
    }

    @Nullable
    @Override
    public PresetDto getItem(int position) {
        return presetList.get(position);
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

        if (position == presetList.size()){
            viewHolder.nameView.setText(mContext.getResources().getString(R.string.savenewpreset));
        }else {
            PresetDto presetDTO = getItem(position);
            viewHolder.nameView.setText(presetDTO.getName());
        }

        if (position == currentPresetPosition){
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

