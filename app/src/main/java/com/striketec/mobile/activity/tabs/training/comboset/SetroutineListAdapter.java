package com.striketec.mobile.activity.tabs.training.comboset;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.ComboDto;
import com.striketec.mobile.dto.SetsDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.ComboSetUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetroutineListAdapter extends ArrayAdapter<SetsDto> {

    Context mContext;
    LayoutInflater inflater;

    ArrayList<SetsDto> setsDtos;
    Activity activity;

    public SetroutineListAdapter(Context context, ArrayList<SetsDto> setsDtos){
        super(context, 0, setsDtos);

        mContext = context;
        activity = (Activity)mContext;
        this.setsDtos = setsDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<SetsDto> setsDtos){
        this.setsDtos = setsDtos;
    }

    @Nullable
    @Override
    public SetsDto getItem(int position) {
        return setsDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_sets_list, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final SetsDto setsDto = getItem(position);

        viewHolder.nameView.setText(setsDto.getName());

        ArrayList<ComboDto> comboDtos = new ArrayList<>();
        for (int i = 0; i < setsDto.getComboIDLists().size(); i++){
            comboDtos.add(ComboSetUtil.getComboDtowithID(setsDto.getComboIDLists().get(i)));
        }

        CombinationListAdapter listAdapter = new CombinationListAdapter(mContext, comboDtos);
        listAdapter.setCurrentPosition(-1);
        viewHolder.comboListView.setAdapter(listAdapter);

        viewHolder.startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trainingIntent = new Intent(activity, PlansTrainingActivity.class);
                trainingIntent.putExtra(AppConst.TRAININGTYPE, AppConst.SETS);
                trainingIntent.putExtra(AppConst.SET_ID, setsDto.getId());
                activity.startActivity(trainingIntent);
                activity.finish();
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.name)  TextView nameView;
        @BindView(R.id.starttraining) TextView startView;
        @BindView(R.id.divider) View divider;
        @BindView(R.id.combolist)
        ListView comboListView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

