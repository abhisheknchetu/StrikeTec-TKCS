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
import com.striketec.mobile.dto.ComboDto;
import com.striketec.mobile.dto.GeneratedTrainingResultsComboDto;
import com.striketec.mobile.util.ComboSetUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnalysisComboListAdapter extends ArrayAdapter<GeneratedTrainingResultsComboDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<GeneratedTrainingResultsComboDto> generatedTrainingResultsComboDtos;

    public AnalysisComboListAdapter(Context context, ArrayList<GeneratedTrainingResultsComboDto> generatedTrainingResultsComboDtos){
        super(context, 0, generatedTrainingResultsComboDtos);

        mContext = context;
        this.generatedTrainingResultsComboDtos = generatedTrainingResultsComboDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<GeneratedTrainingResultsComboDto> generatedTrainingResultsComboDtos){
        this.generatedTrainingResultsComboDtos = generatedTrainingResultsComboDtos;
    }

    @Override
    public int getCount() {
        return generatedTrainingResultsComboDtos.size();
    }

    @Nullable
    @Override
    public GeneratedTrainingResultsComboDto getItem(int position) {
        return generatedTrainingResultsComboDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_analysis_set_child, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        GeneratedTrainingResultsComboDto generatedTrainingResultsComboDto = getItem(position);
        ComboDto comboDto = ComboSetUtil.getComboDtowithID(generatedTrainingResultsComboDto.mComboId);

        viewHolder.comboindexView.setText(String.valueOf(position + 1));
        viewHolder.combonameView.setText(comboDto.getName());
        viewHolder.combostringView.setText(comboDto.getCombos());

        int status = 1;//CommonUtils.compareDuration(comboDto);

        if (status == 0){
            viewHolder.comboindexView.setBackgroundResource(R.drawable.bg_rectangle_bad);
            viewHolder.combonameView.setTextColor(mContext.getResources().getColor(R.color.force_color));
            viewHolder.arrowImage.setImageResource(R.drawable.force_arrow_right);
        }else if (status == 1){
            viewHolder.comboindexView.setBackgroundResource(R.drawable.bg_rectangle_average);
            viewHolder.combonameView.setTextColor(mContext.getResources().getColor(R.color.orange));
            viewHolder.arrowImage.setImageResource(R.drawable.orange_arrow_right);
        }else {
            viewHolder.comboindexView.setBackgroundResource(R.drawable.bg_rectangle_above);
            viewHolder.combonameView.setTextColor(mContext.getResources().getColor(R.color.speed_color));
            viewHolder.arrowImage.setImageResource(R.drawable.speed_arrow_right);
        }


        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.comboindex) TextView comboindexView;
        @BindView(R.id.comboname) TextView combonameView;
        @BindView(R.id.combo_string) TextView combostringView;
        @BindView(R.id.arrow) ImageView arrowImage;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

