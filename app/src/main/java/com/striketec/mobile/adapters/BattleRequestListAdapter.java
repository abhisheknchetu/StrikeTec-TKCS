package com.striketec.mobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.battle.BattleDetailActivity;
import com.striketec.mobile.dto.BattleUser_Temp;
import com.striketec.mobile.dto.NotificationDto_Temp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BattleRequestListAdapter extends ArrayAdapter<BattleUser_Temp> {

    private static final int TYPE_UNSELECTED = 0;
    private static final int TYPE_SELECTED = 1;

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<BattleUser_Temp> items;
    Activity activity;

    public BattleRequestListAdapter(Context context, ArrayList<BattleUser_Temp> items){
        super(context, 0, items);

        mContext = context;
        activity = (Activity)mContext;
        this.items = items;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<BattleUser_Temp> items){
        this.items = items;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public BattleUser_Temp getItem(int position) {
        return items.get(position);
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
            convertView = inflater.inflate(R.layout.item_battle_requests_list, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        BattleUser_Temp item = getItem(position);

        viewHolder.nameView.setText(item.oppoenentUserName);
        viewHolder.actionBtn.setText(mContext.getResources().getString(R.string.viewchallenges));
        viewHolder.diffTimeView.setText(item.timeDiff);

        viewHolder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(activity, BattleDetailActivity.class);
                activity.startActivity(detailIntent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        return convertView;
    }

    private SpannableStringBuilder getContent(NotificationDto_Temp item){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String username = item.oppoenentUserName;

        SpannableString userSpannable = new SpannableString(username);
        userSpannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.punches_text)), 0, username.length(), 0);
        builder.append(userSpannable);

        String content = " " + item.content;

        SpannableString contentSpannable = new SpannableString(content);
        contentSpannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)), 0, content.length(), 0);
        builder.append(contentSpannable);

        return builder;
    }


    public class ViewHolder {
        @BindView(R.id.profile) ImageView profileImageView;
        @BindView(R.id.username) TextView nameView;
        @BindView(R.id.difftime) TextView diffTimeView;
        @BindView(R.id.viewchallenge) Button actionBtn;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

