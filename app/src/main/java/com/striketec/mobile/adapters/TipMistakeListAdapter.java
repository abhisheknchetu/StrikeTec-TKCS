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

public class TipMistakeListAdapter extends ArrayAdapter<ArrayList<MistakeDto>> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<ArrayList<MistakeDto>> mistakeDtos;

    public TipMistakeListAdapter(Context context, ArrayList<ArrayList<MistakeDto>> mistakeDtos){
        super(context, 0, mistakeDtos);

        mContext = context;
        this.mistakeDtos = mistakeDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<ArrayList<MistakeDto>> mistakeDtos){
        this.mistakeDtos = mistakeDtos;
    }

    @Override
    public int getCount() {
        return mistakeDtos.size();
    }

    @Nullable
    @Override
    public ArrayList<MistakeDto> getItem(int position) {
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
            convertView = inflater.inflate(R.layout.item_analysis_tip_mistakes, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        ArrayList<MistakeDto> mistakePunchList = getItem(position);

//        String type = mistakePunchList.get(0).trainingPunchDto.mHand + " " + mistakePunchList.get(0).trainingPunchDto.mPunchType;
//        viewHolder.mistakeInfoView.setText(type + ", " + mistakePunchList.size() + " " +  mContext.getResources().getString(R.string.mistakes));

        TipMistakePunchListAdapter adapter = new TipMistakePunchListAdapter(mContext, mistakePunchList);
        viewHolder.mistakeListview.setAdapter(adapter);

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.mistake_info) TextView mistakeInfoView;
        @BindView(R.id.mistake_listview) ListView mistakeListview;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

