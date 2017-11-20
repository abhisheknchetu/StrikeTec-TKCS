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

public class SortListAdapter extends ArrayAdapter<String> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<String> itemList;
    private int currentPresetPosition;
//
    public SortListAdapter(Context context, ArrayList<String> itemList, int currentPresetPosition){
        super(context, 0, itemList);

        mContext = context;
        this.currentPresetPosition = currentPresetPosition;
        this.itemList = itemList;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<String> itemList){
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return itemList.get(position);
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

        viewHolder.nameView.setText(itemList.get(position));

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

