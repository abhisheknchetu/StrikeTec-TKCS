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
import com.striketec.mobile.dto.ChallengeHistoryDto;
import com.striketec.mobile.dto.TrainingRoundDto;
import com.striketec.mobile.util.PresetUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChallengeHistoryAdapter extends ArrayAdapter<ChallengeHistoryDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<ChallengeHistoryDto> challengeHistoryDtos;

    public ChallengeHistoryAdapter(Context context, ArrayList<ChallengeHistoryDto> challengeHistoryDtos){
        super(context, 0, challengeHistoryDtos);

        mContext = context;
        this.challengeHistoryDtos = challengeHistoryDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<ChallengeHistoryDto> challengeHistoryDtos){
        this.challengeHistoryDtos = challengeHistoryDtos;
    }

    @Override
    public int getCount() {
        return challengeHistoryDtos.size();
    }

    @Nullable
    @Override
    public ChallengeHistoryDto getItem(int position) {
        return challengeHistoryDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_challengehistory, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        ChallengeHistoryDto challengeHistoryDto = getItem(position);

        if (challengeHistoryDto.firstisCurrent){
            viewHolder.winnerProfile.setBorderColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.losserProfile.setBorderColor(mContext.getResources().getColor(R.color.transparent));
            viewHolder.winnerProfile.setImageResource(R.drawable.default_profil);
            viewHolder.losserProfile.setImageResource(R.drawable.tmp_avatar);
            viewHolder.winnerName.setTextColor(mContext.getResources().getColor(R.color.white));
            viewHolder.losserName.setTextColor(mContext.getResources().getColor(R.color.punches_text));
        }else {
            viewHolder.losserProfile.setBorderColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.winnerProfile.setBorderColor(mContext.getResources().getColor(R.color.transparent));
            viewHolder.losserProfile.setImageResource(R.drawable.default_profil);
            viewHolder.winnerProfile.setImageResource(R.drawable.tmp_avatar);
            viewHolder.winnerName.setTextColor(mContext.getResources().getColor(R.color.punches_text));
            viewHolder.losserName.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        viewHolder.winnerName.setText(challengeHistoryDto.name1);
        viewHolder.losserName.setText(challengeHistoryDto.name2);

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.winner_name) TextView winnerName;
        @BindView(R.id.winner_profile) CircleImageView winnerProfile;
        @BindView(R.id.losser_name) TextView losserName;
        @BindView(R.id.losser_profile) CircleImageView losserProfile;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

