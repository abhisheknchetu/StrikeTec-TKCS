package com.striketec.mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.MistakeDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TipMistakePunchListAdapter extends ArrayAdapter<MistakeDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<MistakeDto> mistakeDtos;

    public TipMistakePunchListAdapter(Context context, ArrayList<MistakeDto> mistakeDtos){
        super(context, 0, mistakeDtos);

        mContext = context;
        this.mistakeDtos = mistakeDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<MistakeDto> mistakeDtos){
        this.mistakeDtos = mistakeDtos;
    }

    @Override
    public int getCount() {
        return mistakeDtos.size();
    }

    @Nullable
    @Override
    public MistakeDto getItem(int position) {
        return mistakeDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_analysis_tip_mistake_punch, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        MistakeDto mistakepunch = getItem(position);

//        if (mistakepunch.speedisslow){
//            viewHolder.statusView.setText(mContext.getResources().getString(R.string.veryslow));
//            viewHolder.valueView.setText((int)mistakepunch.trainingPunchDto.mSpeed + mContext.getResources().getString(R.string.speed_unit));
//        }else {
//            viewHolder.statusView.setText(mContext.getResources().getString(R.string.weakpunch));
//            viewHolder.valueView.setText((int)mistakepunch.trainingPunchDto.mForce + mContext.getResources().getString(R.string.force_unit));
//        }


        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.status) TextView statusView;
        @BindView(R.id.value) TextView valueView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

