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
import com.striketec.mobile.dto.ChatDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentChatListAdapter extends ArrayAdapter<ChatDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<ChatDto> chatDtos;

    public RecentChatListAdapter(Context context, ArrayList<ChatDto> chatDtos){
        super(context, 0, chatDtos);

        mContext = context;
        this.chatDtos = chatDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<ChatDto> chatDtos){
        this.chatDtos = chatDtos;
    }

    @Override
    public int getCount() {
        return chatDtos.size();
    }

    @Nullable
    @Override
    public ChatDto getItem(int position) {
        return chatDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_recent_chat, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        ChatDto chatDto = getItem(position);

        viewHolder.usernameView.setText(chatDto.opponentUserName);
        viewHolder.timeView.setText("July 2");
        viewHolder.messageView.setText(chatDto.message);

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.profile) ImageView profileImageView;
        @BindView(R.id.name) TextView usernameView;
        @BindView(R.id.time) TextView timeView;
        @BindView(R.id.message) TextView messageView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

