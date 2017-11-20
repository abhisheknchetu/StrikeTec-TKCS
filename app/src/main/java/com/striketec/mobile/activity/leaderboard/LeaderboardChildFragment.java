package com.striketec.mobile.activity.leaderboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.credential.CountrySelectActivity;
import com.striketec.mobile.activity.credential.ForgotpasswordActivity;
import com.striketec.mobile.adapters.CompareLeaderboardListAdapter;
import com.striketec.mobile.adapters.ExploreListAdapter;
import com.striketec.mobile.adapters.LeaderboardListAdapter;
import com.striketec.mobile.customview.EndlessScrollListener;
import com.striketec.mobile.dto.CountryStateCityDto;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.LeaderboardDto;
import com.striketec.mobile.dto.ServerLeaderboardResponseDto;
import com.striketec.mobile.interfaces.FollowCallback;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class LeaderboardChildFragment extends Fragment {

    public static String TAG = LeaderboardChildFragment.class.getSimpleName();

    @BindView(R.id.listview) ListView listView;
    @BindView(R.id.country) Button countryBtn;

    private String type;
    Activity activity;

    ArrayList<LeaderboardDto> leaderboardDtos = new ArrayList<>();
    CountryStateCityDto countryDto = null;
    FollowCallback followCallback;

    ArrayAdapter adapter;

    public static LeaderboardChildFragment leaderboardChildFragment;
    private static Context mContext;

    public static LeaderboardChildFragment newInstance(Context context, String type) {
        mContext = context;
        leaderboardChildFragment = new LeaderboardChildFragment();
        Bundle arg = new Bundle();
        arg.putString(AppConst.FEED_TYPE, type);
        leaderboardChildFragment.setArguments(arg);
        return leaderboardChildFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        type = getArguments().getString(AppConst.FEED_TYPE);
        countryDto = StriketecApp.currentUser.mCountry;

        ButterKnife.bind(this, view);

        followCallback = (FollowCallback)getActivity();
        initViews();

        return view;
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(int position){
        if (type.equalsIgnoreCase(AppConst.FEED_COMPARE_LEADERBOARD)){
            if (((CompareLeaderboardListAdapter)adapter).getSelectedPosition() == position)
                ((CompareLeaderboardListAdapter)adapter).setSelectedPosition(-1);
            else
                ((CompareLeaderboardListAdapter)adapter).setSelectedPosition(position);
            adapter.notifyDataSetChanged();
        }else if (type.equalsIgnoreCase(AppConst.FEED_LEADERBOARD)){
            if (((LeaderboardListAdapter)adapter).getSelectedPosition() == position)
                ((LeaderboardListAdapter)adapter).setSelectedPosition(-1);
            else
                ((LeaderboardListAdapter)adapter).setSelectedPosition(position);
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.country})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.country:
                gotoCountryActivity();
                break;
        }
    }

    private void initViews(){

        if (countryDto != null){
            countryBtn.setText(countryDto.mName);
        }

        if (type.equalsIgnoreCase(AppConst.FEED_LEADERBOARD)){
            adapter = new LeaderboardListAdapter(activity, leaderboardDtos, followCallback);
        }else {
            adapter = new CompareLeaderboardListAdapter(activity, leaderboardDtos, followCallback);
        }

        listView.setAdapter(adapter);

        if (countryDto == null)
            loadLeaderboards(0);
        else
            loadLeaderboards(countryDto.mId);
    }

    private void gotoCountryActivity(){
        Intent countryIntent = new Intent(activity, CountrySelectActivity.class);
        countryIntent.putExtra(AppConst.TYPE, AppConst.COUNTRY);
        countryIntent.putExtra(AppConst.FILTER, true);
        countryIntent.putExtra(AppConst.COUNTRY, countryDto == null ? -1 : countryDto.mId);
        startActivityForResult(countryIntent, AppConst.COUNTRY_REQUEST_CODE);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void updateUserFollow(int userId){
        for (int i = 0; i < leaderboardDtos.size(); i++){
            LeaderboardDto leaderboardDto = leaderboardDtos.get(i);

            if (leaderboardDto.mUserId == userId){
                leaderboardDto.mUser.mUserFollowing = !leaderboardDto.mUser.mUserFollowing;

                break;
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void loadLeaderboards(int countryId){

        Map<String, Object> params = new HashMap();

        if (countryId >0)
            params.put("country_id", countryId);

        Log.e("Super", "Header = " + SharedUtils.getHeader());

        if (CommonUtils.isOnline()){
            RetrofitSingleton.DATA_REST.getLeaderboard(SharedUtils.getHeader(), params).enqueue(new IndicatorCallback<ServerLeaderboardResponseDto>(activity, leaderboardDtos.isEmpty()) {
                @Override
                public void onResponse(Call<ServerLeaderboardResponseDto> call, Response<ServerLeaderboardResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        ServerLeaderboardResponseDto responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage);

                            leaderboardDtos.clear();
                            leaderboardDtos.addAll(responseDto.mData);

                            adapter.notifyDataSetChanged();
                        }else {
                            if (!TextUtils.isEmpty(responseDto.mMessage))
                                CommonUtils.showAlert(activity, responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerLeaderboardResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(activity, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == AppConst.COUNTRY_REQUEST_CODE){
                CountryStateCityDto stateCityDto = (CountryStateCityDto)data.getSerializableExtra(AppConst.COUNTRY);

                if (countryDto == null || countryDto.mId != stateCityDto.mId){
                    countryDto = stateCityDto;
                    countryBtn.setText(stateCityDto.mName);

                    loadLeaderboards(countryDto.mId);
                }
            }
        }
    }
}
