package com.striketec.mobile.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.LeaderboardDto;
import com.striketec.mobile.interfaces.FollowCallback;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class CompareLeaderboardListAdapter extends ArrayAdapter<LeaderboardDto> {

    private static final int TYPE_UNSELECTED = 0;
    private static final int TYPE_SELECTED = 1;

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<LeaderboardDto> leaderboardDtos;
    Activity activity;
    FollowCallback followCallback;
    private int selectedPosition = -1;

    public CompareLeaderboardListAdapter(Context context, ArrayList<LeaderboardDto> leaderboardDtos, FollowCallback followCallback){
        super(context, 0, leaderboardDtos);

        mContext = context;
        activity = (Activity)mContext;
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
            convertView = inflater.inflate(R.layout.item_compareleaderboard_list, null);
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

        if (leaderboardDto.mUser.mUserFollower && leaderboardDto.mUser.mUserFollowing){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.connection));
        }else if (leaderboardDto.mUser.mUserFollowing){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.following));
        }else if (leaderboardDto.mUser.mUserFollower){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.followingyou));
        }else
            viewHolder.connectstatusView.setText("");

        viewHolder.settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDialog(leaderboardDto);
            }
        });

        if (leaderboardDto.mUserId == StriketecApp.currentUser.mId)
            viewHolder.settingsView.setVisibility(View.INVISIBLE);
        else
            viewHolder.settingsView.setVisibility(View.VISIBLE);

        return convertView;
    }

    private View getSelectedView (final int position, View convertView, ViewGroup parent) {

        final SelectedViewHolder viewHolder;

        if (convertView == null || !(convertView.getTag() instanceof SelectedViewHolder)){
            convertView = inflater.inflate(R.layout.item_compareleaderboard_list_selected, null);
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
        viewHolder.avgspeedView.setText(String.valueOf((int)leaderboardDto.mAvgSpeed));
        viewHolder.avgforceView.setText(String.valueOf((int)leaderboardDto.mAvgForce));
        viewHolder.avgcountView.setText(String.valueOf(leaderboardDto.mPunchCount / leaderboardDto.mSessionCount));

        if (leaderboardDto.mUser.mUserFollower && leaderboardDto.mUser.mUserFollowing){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.connection));
        }else if (leaderboardDto.mUser.mUserFollowing){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.following));
        }else if (leaderboardDto.mUser.mUserFollower){
            viewHolder.connectstatusView.setText(mContext.getResources().getString(R.string.followingyou));
        }else
            viewHolder.connectstatusView.setText("");

        if (leaderboardDto.mUserId == StriketecApp.currentUser.mId)
            viewHolder.settingsView.setVisibility(View.INVISIBLE);
        else
            viewHolder.settingsView.setVisibility(View.VISIBLE);

        viewHolder.settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDialog(leaderboardDto);
            }
        });

        return convertView;
    }

    public static class UnselectedViewHolder {
        @BindView(R.id.profile) ImageView profileImageView;
        @BindView(R.id.rank) TextView rankView;
        @BindView(R.id.name) TextView usernameView;
        @BindView(R.id.pro_layout) LinearLayout proLayout;
        @BindView(R.id.settings) ImageView settingsView;
        @BindView(R.id.points) TextView pointsView;
        @BindView(R.id.rank_below) TextView rankBelowView;
        @BindView(R.id.followingstatus) TextView connectstatusView;

        UnselectedViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    public static class SelectedViewHolder {
        @BindView(R.id.profile) ImageView profileImageView;
        @BindView(R.id.name) TextView usernameView;
        @BindView(R.id.rank) TextView rankView;
        @BindView(R.id.pro_layout) LinearLayout proLayout;
        @BindView(R.id.settings) ImageView settingsView;
        @BindView(R.id.avgspeed_value) TextView avgspeedView;
        @BindView(R.id.avgcount_value) TextView avgcountView;
        @BindView(R.id.avgforce_value) TextView avgforceView;
        @BindView(R.id.rank_below) TextView rankBelowView;
        @BindView(R.id.followingstatus) TextView connectstatusView;

        SelectedViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    private void showSettingsDialog(final LeaderboardDto leaderboardDto){
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_presetpopup);

        final ArrayList<String> actionList = new ArrayList<>();

        if (leaderboardDto.mUser.mUserFollowing && leaderboardDto.mUser.mUserFollower){
            //this is connection, will be unfollow, message
            actionList.add(mContext.getResources().getString(R.string.unfollow));
            actionList.add(mContext.getResources().getString(R.string.message));
        }else if (leaderboardDto.mUser.mUserFollowing){
            //following status, will be unfollow
            actionList.add(mContext.getResources().getString(R.string.unfollow));
        }else {
            //will be follow
            actionList.add(mContext.getResources().getString(R.string.follow));
        }

        ListView actionlistview = (ListView)dialog.findViewById(R.id.listview);
        final SortListAdapter sortListAdapter = new SortListAdapter(activity, actionList, 1);
        actionlistview.setAdapter(sortListAdapter);

        actionlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                if (sortListAdapter.getItem(position).equalsIgnoreCase(mContext.getResources().getString(R.string.follow)) ||
                        sortListAdapter.getItem(position).equalsIgnoreCase(mContext.getResources().getString(R.string.unfollow)))
                    followUnfollowUser(leaderboardDto);
                else if (sortListAdapter.getItem(position).equalsIgnoreCase(mContext.getResources().getString(R.string.message))){
                    //go to chat screen
                    CommonUtils.showToastMessage("Go to Chat");
                }
            }
        });

        dialog.show();
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

