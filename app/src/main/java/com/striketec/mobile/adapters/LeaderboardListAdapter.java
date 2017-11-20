package com.striketec.mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.LeaderboardDto;
import com.striketec.mobile.dto.VideoDto;
import com.striketec.mobile.interfaces.FollowCallback;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class LeaderboardListAdapter extends ArrayAdapter<LeaderboardDto> {

    private static final int TYPE_UNSELECTED = 0;
    private static final int TYPE_SELECTED = 1;

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<LeaderboardDto> leaderboardDtos;
    FollowCallback followCallback;

    private int selectedPosition = -1;

    public LeaderboardListAdapter(Context context, ArrayList<LeaderboardDto> leaderboardDtos, FollowCallback followCallback){
        super(context, 0, leaderboardDtos);

        mContext = context;
        this.leaderboardDtos = leaderboardDtos;
        this.followCallback = followCallback;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData (ArrayList<LeaderboardDto> leaderboardDtos){
        this.leaderboardDtos = leaderboardDtos;
        selectedPosition = -1;
    }

    public void setSelectedPosition(int selectedPosition){
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == selectedPosition){
            return TYPE_SELECTED;
        }else
            return TYPE_UNSELECTED;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return leaderboardDtos.size();
    }

    @Nullable
    @Override
    public LeaderboardDto getItem(int position) {
        return leaderboardDtos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        switch (type){
            case TYPE_UNSELECTED:
                return getUnselectedView(position, convertView, parent);

            case TYPE_SELECTED:
                return getSelectedView(position, convertView, parent);
        }

        return null;
    }

    private View getUnselectedView(final int position, View convertView, ViewGroup parent) {

        final UnselectedViewHolder viewHolder;

        if (convertView == null || !(convertView.getTag() instanceof UnselectedViewHolder)){
            convertView = inflater.inflate(R.layout.item_leaderboard_list, null);
            viewHolder = new UnselectedViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (UnselectedViewHolder)convertView.getTag();
        }

        final LeaderboardDto leaderboardDto = getItem(position);

        if (leaderboardDto.mUserId == StriketecApp.currentUser.mId) {
            viewHolder.usernameView.setText("You");
            viewHolder.connectstatusView.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.usernameView.setText(leaderboardDto.mUser.mFirstName + " " + leaderboardDto.mUser.mlastName);
            viewHolder.connectstatusView.setVisibility(View.VISIBLE);
        }

        viewHolder.rankView.setText(String.valueOf(leaderboardDto.mRank));
        viewHolder.rankBelowView.setText(String.valueOf(leaderboardDto.mRank));
        viewHolder.pointsView.setText(String.format(mContext.getResources().getString(R.string.points), leaderboardDto.mPunchCount));

        if (leaderboardDto.mUser.mUserFollowing){
            viewHolder.followstatusView.setText(mContext.getResources().getString(R.string.unfollow));
            viewHolder.followstatusView.setTextColor(mContext.getResources().getColor(R.color.black));
        }else {
            viewHolder.followstatusView.setText(mContext.getResources().getString(R.string.follow));
            viewHolder.followstatusView.setTextColor(mContext.getResources().getColor(R.color.orange));
        }

        if (leaderboardDto.mUser.mUserFollower && leaderboardDto.mUser.mUserFollowing){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.connection));
        }else if (leaderboardDto.mUser.mUserFollowing){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.following));
        }else if (leaderboardDto.mUser.mUserFollower){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.followingyou));
        }else
            viewHolder.connectstatusView.setText("");

        if (leaderboardDto.mUserId == StriketecApp.currentUser.mId){
            viewHolder.followstatusView.setVisibility(View.INVISIBLE);
        }else
            viewHolder.followstatusView.setVisibility(View.VISIBLE);

        viewHolder.followstatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUnfollowUser(leaderboardDto);
            }
        });

        return convertView;
    }

    private View getSelectedView (final int position, View convertView, ViewGroup parent) {

        final SelectedViewHolder viewHolder;

        if (convertView == null || !(convertView.getTag() instanceof SelectedViewHolder)){
            convertView = inflater.inflate(R.layout.item_leaderboard_list_selected, null);
            viewHolder = new SelectedViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (SelectedViewHolder)convertView.getTag();
        }

        final LeaderboardDto leaderboardDto = getItem(position);

        if (leaderboardDto.mUserId == StriketecApp.currentUser.mId) {
            viewHolder.usernameView.setText("You");
            viewHolder.connectstatusView.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.usernameView.setText(leaderboardDto.mUser.mFirstName + " " + leaderboardDto.mUser.mlastName);
            viewHolder.connectstatusView.setVisibility(View.VISIBLE);
        }


        viewHolder.rankView.setText(String.valueOf(leaderboardDto.mRank));
        viewHolder.rankBelowView.setText(String.valueOf(leaderboardDto.mRank));

        viewHolder.pointsView.setText(String.format(mContext.getResources().getString(R.string.points), leaderboardDto.mPunchCount));

        if (leaderboardDto.mUser.mUserFollowing){
            viewHolder.followstatusView.setText(mContext.getResources().getString(R.string.unfollow));
            viewHolder.followstatusView.setTextColor(mContext.getResources().getColor(R.color.black));
        }else {
            viewHolder.followstatusView.setText(mContext.getResources().getString(R.string.follow));
            viewHolder.followstatusView.setTextColor(mContext.getResources().getColor(R.color.orange));
        }

        if (leaderboardDto.mUser.mUserFollower && leaderboardDto.mUser.mUserFollowing){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.connection));
        }else if (leaderboardDto.mUser.mUserFollowing){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.following));
        }else if (leaderboardDto.mUser.mUserFollower){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.followingyou));
        }else
            viewHolder.connectstatusView.setText("");

        if (leaderboardDto.mUserId == StriketecApp.currentUser.mId){
            viewHolder.followstatusView.setVisibility(View.INVISIBLE);
        }else
            viewHolder.followstatusView.setVisibility(View.VISIBLE);


        viewHolder.followstatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUnfollowUser(leaderboardDto);
            }
        });

        return convertView;
    }

    public class SelectedViewHolder {
        @BindView(R.id.profile) ImageView profileImageView;
        @BindView(R.id.rank) TextView rankView;
        @BindView(R.id.name) TextView usernameView;
        @BindView(R.id.pro_layout) LinearLayout proLayout;
        @BindView(R.id.points) TextView pointsView;
        @BindView(R.id.followstatus)  Button followstatusView;
        @BindView(R.id.rank_below) TextView rankBelowView;
        @BindView(R.id.followingstatus) TextView connectstatusView;

        SelectedViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    public class UnselectedViewHolder {
        @BindView(R.id.profile) ImageView profileImageView;
        @BindView(R.id.name) TextView usernameView;
        @BindView(R.id.rank) TextView rankView;
        @BindView(R.id.pro_layout) LinearLayout proLayout;
        @BindView(R.id.points) TextView pointsView;
        @BindView(R.id.followstatus)  Button followstatusView;
        @BindView(R.id.rank_below) TextView rankBelowView;
        @BindView(R.id.followingstatus) TextView connectstatusView;

        UnselectedViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    public void followUnfollowUser(final LeaderboardDto leaderboardDto){

        if (CommonUtils.isOnline()){

            Call call = null;

            if (leaderboardDto.mUser.mUserFollowing){
                call = RetrofitSingleton.USER_REST.unfollowUser(SharedUtils.getHeader(), leaderboardDto.mUserId);
            }else {
                call = RetrofitSingleton.USER_REST.followUser(SharedUtils.getHeader(), leaderboardDto.mUserId);
            }

            if (call != null){
                call.enqueue(new IndicatorCallback<DefaultResponseDto>(mContext, false) {
                    @Override
                    public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                        super.onResponse(call, response);

                        if (response.body() != null){
                            DefaultResponseDto responseDto = response.body();

                            if (!responseDto.mError){
                                followCallback.followChanged(leaderboardDto.mUserId);
                            }else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                        super.onFailure(call, t);
                        CommonUtils.showAlert(mContext, t.getLocalizedMessage());
                    }
                });
            }

        }else {
            CommonUtils.showToastMessage(mContext.getResources().getString(R.string.nointernet));
        }
    }
}

