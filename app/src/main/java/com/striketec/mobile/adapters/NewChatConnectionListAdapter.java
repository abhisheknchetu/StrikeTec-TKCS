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
import com.striketec.mobile.dto.UserDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewChatConnectionListAdapter extends ArrayAdapter<UserDto> {

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<UserDto> userDtos;

    public NewChatConnectionListAdapter(Context context, ArrayList<UserDto> userDtos){
        super(context, 0, userDtos);

        mContext = context;
        this.userDtos = userDtos;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<UserDto> userDtos){
        this.userDtos = userDtos;
    }

    @Override
    public int getCount() {
        return userDtos.size();
    }

    @Nullable
    @Override
    public UserDto getItem(int position) {
        return userDtos.get(position);
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
            convertView = inflater.inflate(R.layout.item_new_chat, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        UserDto userDto = getItem(position);
        viewHolder.usernameView.setText(userDto.mFirstName + " " + userDto.mlastName);

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.profile) ImageView profileImageView;
        @BindView(R.id.name) TextView usernameView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

