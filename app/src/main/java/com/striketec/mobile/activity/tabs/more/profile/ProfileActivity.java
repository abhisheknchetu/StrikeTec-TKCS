package com.striketec.mobile.activity.tabs.more.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.ChallengeHistoryAdapter;
import com.striketec.mobile.dto.AuthResponseDto;
import com.striketec.mobile.dto.ChallengeHistoryDto;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.listview) ListView listView;
    @BindView(R.id.fname) TextView firstnameView;
    @BindView(R.id.lname) TextView lastnameView;
    @BindView(R.id.avatar) ImageView profileView;
    @BindView(R.id.followercount) TextView followerCountView;
    @BindView(R.id.followingcount) TextView followingCountView;
    @BindView(R.id.challengecount) TextView challengeCountView;


    ChallengeHistoryAdapter adapter;
    ArrayList<ChallengeHistoryDto> historyDtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        adapter = new ChallengeHistoryAdapter(this, historyDtos);
        listView.setAdapter(adapter);

        getUserInfo();
    }

    private void updateView(){
        firstnameView.setText(StriketecApp.currentUser.mFirstName);
        lastnameView.setText(StriketecApp.currentUser.mlastName);
        followerCountView.setText(String.valueOf(StriketecApp.currentUser.mFollowersCount));
        followingCountView.setText(String.valueOf(StriketecApp.currentUser.mFollowingCount));

        if (!TextUtils.isEmpty(StriketecApp.currentUser.mPhoto)){
            Glide.with(this).load(StriketecApp.currentUser.mPhoto).into(profileView);
        }

        loadHistories();
    }

    private void getUserInfo(){
        if (CommonUtils.isOnline()){

            RetrofitSingleton.USER_REST.getUserInfo(SharedUtils.getHeader(), StriketecApp.currentUser.mId).enqueue(new IndicatorCallback<AuthResponseDto>(ProfileActivity.this) {
                @Override
                public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        AuthResponseDto responseDto = response.body();

                        if (!responseDto.mError){
                            StriketecApp.currentUser = responseDto.mUser;
                        }else {

                        }

                        updateView();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(ProfileActivity.this, t.getLocalizedMessage());
                    updateView();
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
            updateView();
        }
    }

    @OnClick({R.id.back, R.id.editprofile})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.editprofile:
                Intent editIntent = new Intent(this, EditProfileActivity.class);
                startActivity(editIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    private void loadHistories(){
        String currentName = "Conor Anthony McGregor";
        String otherName = "Melvin Reyes";

        for (int i = 0; i < 15; i++){
            ChallengeHistoryDto challengeHistoryDto = new ChallengeHistoryDto();
            int winner = CommonUtils.getRandomNum(10, 3);
            if (winner < 6)
                challengeHistoryDto.firstisCurrent = true;
            else
                challengeHistoryDto.firstisCurrent = false;

            if (challengeHistoryDto.firstisCurrent){
                challengeHistoryDto.name1 = currentName;
                challengeHistoryDto.name2 = otherName;
            }else {
                challengeHistoryDto.name2 = currentName;
                challengeHistoryDto.name1 = otherName;
            }

            historyDtos.add(challengeHistoryDto);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
