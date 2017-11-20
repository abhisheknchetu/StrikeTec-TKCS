package com.striketec.mobile.activity.tabs.training.comboset;

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
import com.striketec.mobile.dto.ComboDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CombinationListAdapter extends ArrayAdapter<ComboDto> {

    Context mContext;
    LayoutInflater inflater;

    ArrayList<ComboDto> comboLists;
    private int currentPosition = 0;

    public CombinationListAdapter(Context context, ArrayList<ComboDto> comboLists){
        super(context, 0, comboLists);

        mContext = context;
        this.comboLists = comboLists;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<ComboDto> comboLists){
        this.comboLists = comboLists;
    }

    @Nullable
    @Override
    public ComboDto getItem(int position) {
        return comboLists.get(position);
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
            convertView = inflater.inflate(R.layout.item_combo_list, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final ComboDto comboDTO = getItem(position);

        if (currentPosition == position){
            viewHolder.parentView.setBackgroundColor(mContext.getResources().getColor(R.color.selectbg));
        }else {
            viewHolder.parentView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }

        viewHolder.nameView.setText(comboDTO.getName());
        viewHolder.combostringView.setText(comboDTO.getCombos());

        if (position == comboLists.size() - 1)
            viewHolder.divider.setVisibility(View.GONE);
        else
            viewHolder.divider.setVisibility(View.VISIBLE);

        return convertView;
    }

    public int getCurrentPosition(){
        return currentPosition;
    }

    public void setCurrentPosition(int position){
        this.currentPosition = position;
    }

    public static class ViewHolder {
        @BindView(R.id.parent) LinearLayout parentView;
        @BindView(R.id.name)  TextView nameView;
        @BindView(R.id.content)
        TextView combostringView;
        @BindView(R.id.divider) View divider;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

