package com.striketec.mobile.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.RecentChatListAdapter;
import com.striketec.mobile.dto.ChatDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecentChatActivity extends BaseActivity {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.second_image) ImageView secondImageView;

    @BindView(R.id.swipe)  SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.listview) ListView listView;

    RecentChatListAdapter adapter;

    private ArrayList<ChatDto> recentDtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recentchat);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.chats));
        secondImageView.setVisibility(View.VISIBLE);
        secondImageView.setImageResource(R.drawable.icon_edit);

        adapter = new RecentChatListAdapter(this, recentDtos);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recentDtos.clear();
                loadRecents();
            }
        });

        loadRecents();
    }

    private void loadRecents(){
        for (int i = 0; i < 10; i++){
            ChatDto chatDto = new ChatDto();
            chatDto.opponentUserName = "Cornor McGregor";
            chatDto.messageTime = "December 4";
            chatDto.message = "I'm not sure about managing it through invision, hopefully they have a better method because not all features can be added in invision";

            recentDtos.add(chatDto);
        }

        adapter.notifyDataSetChanged();

        swipeRefreshLayout.setRefreshing(false);
    }

    @OnClick({R.id.back, R.id.second_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.second_image:
                Intent newchooseIntent = new Intent(RecentChatActivity.this, NewChatChooseActivity.class);
                startActivity(newchooseIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
