package com.striketec.mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.CountryStateCityDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryListAdapter extends ArrayAdapter<CountryStateCityDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<CountryStateCityDto> itemLists;
    private int currentPosition = -1;

    public CountryListAdapter(Context context, ArrayList<CountryStateCityDto> itemLists){
        super(context, 0, itemLists);

        mContext = context;
        this.itemLists = itemLists;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCurrentPosition(int currentPosition){
        this.currentPosition = currentPosition;
    }

    public void setData(ArrayList<CountryStateCityDto> itemLists) {
        this.itemLists = itemLists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemLists.size();
    }

    @Nullable
    @Override
    public CountryStateCityDto getItem(int position) {
        return itemLists.get(position);
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
            convertView = inflater.inflate(R.layout.item_country_row, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.itemView.setText(getItem(position).mName);

        if (currentPosition == getItem(position).mId){
            viewHolder.checkView.setVisibility(View.VISIBLE);
        }else {
            viewHolder.checkView.setVisibility(View.GONE);
        }

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.checkview) ImageView checkView;
        @BindView(R.id.item) TextView itemView;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

