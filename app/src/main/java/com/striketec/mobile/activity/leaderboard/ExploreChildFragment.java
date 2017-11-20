package com.striketec.mobile.activity.leaderboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.adapters.CompareLeaderboardListAdapter;
import com.striketec.mobile.adapters.ExploreListAdapter;
import com.striketec.mobile.adapters.LeaderboardListAdapter;
import com.striketec.mobile.adapters.PresetListAdapter;
import com.striketec.mobile.adapters.SortListAdapter;
import com.striketec.mobile.customview.EndlessScrollListener;
import com.striketec.mobile.dto.CountryStateCityDto;
import com.striketec.mobile.dto.LeaderboardDto;
import com.striketec.mobile.dto.PresetDto;
import com.striketec.mobile.dto.ServerLeaderboardResponseDto;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.interfaces.FollowCallback;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

import org.apache.commons.collections.map.HashedMap;

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

public class ExploreChildFragment extends Fragment {

    public static String TAG = ExploreChildFragment.class.getSimpleName();
    private final int INCREMENT = 50;

    @BindView(R.id.swipe) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.listview) ListView listView;

    private boolean moreload = true;
    private int startPoint = 0;

    CountryStateCityDto countryDto, stateDto;
    String gender;
    int minweight, maxweight;
    int minage, maxage;

    Activity activity;
    FollowCallback followCallback;

    ArrayList<LeaderboardDto> leaderboardDtos = new ArrayList<>();

    private enum SORT {
        Rank,
        Name,
        Speed,
        Power,
        Punchcount,
        Challenges,
        Hours
    }

    SORT currentSort = SORT.Rank;
    int currentsortposition = 0;

    ExploreListAdapter adapter;

    public static ExploreChildFragment exploreChildFragment;
    private static Context mContext;

    public static ExploreChildFragment newInstance(Context context) {
        mContext = context;
        exploreChildFragment = new ExploreChildFragment();
        return exploreChildFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        ButterKnife.bind(this, view);

        countryDto = StriketecApp.currentUser.mCountry;
        stateDto = StriketecApp.currentUser.mState;
        gender = StriketecApp.currentUser.mGender;

        minweight = StriketecApp.currentUser.mWeight - 20;
        maxweight = StriketecApp.currentUser.mWeight + 20;

        int age = DatesUtil.getAge(StriketecApp.currentUser.mBirthday);

        minage = age - 6;
        maxage = age + 6;

        if (minweight <= AppConst.WEIGHT_MIN)
            minweight = AppConst.WEIGHT_MIN;

        if (maxweight >= AppConst.WEIGHT_MAX)
            maxweight = AppConst.WEIGHT_MAX;

        if (minage <= AppConst.AGE_MIN || minage >= AppConst.AGE_MAX)
            minage = AppConst.AGE_MIN;

        if (maxage >= AppConst.AGE_MAX || maxage <= AppConst.AGE_MIN)
            maxage = AppConst.AGE_MAX;


        followCallback = (FollowCallback)getActivity();

        initViews();

        return view;
    }

    @OnClick({R.id.sortview, R.id.filterview})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.sortview:
                showSortDialog();
                break;

            case R.id.filterview:
                Intent filterIntent = new Intent(activity, ExploreFilterActivity.class);
                if (countryDto != null)
                    filterIntent.putExtra(AppConst.COUNTRY, countryDto);

                if (stateDto != null)
                    filterIntent.putExtra(AppConst.STATE, stateDto);

                filterIntent.putExtra(AppConst.MAX_WEIGHT, maxweight);
                filterIntent.putExtra(AppConst.MIN_WEIGHT, minweight);
                filterIntent.putExtra(AppConst.MAX_AGE, maxage);
                filterIntent.putExtra(AppConst.MIN_AGE, minage);
                filterIntent.putExtra(AppConst.GENDER, gender);

                startActivityForResult(filterIntent, AppConst.FILTER_REQUEST_CODE);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    private void initViews(){
        adapter = new ExploreListAdapter(activity, leaderboardDtos, followCallback);
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                return loadMore();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                leaderboardDtos.clear();
                startPoint = 0;

                loadMoreLeaderboard(0);

//                if (countryDto == null)
//                    loadLeaderboards(0);
//                else
//                    loadLeaderboards(countryDto.mId);
                //loadMoreUsers(0);
            }
        });

//        if (countryDto == null)
//            loadLeaderboards(0);
//        else
//            loadLeaderboards(countryDto.mId);
        loadMoreLeaderboard(0);
    }

    private void updateSortView(){
        adapter.setData(leaderboardDtos);
        adapter.notifyDataSetChanged();
    }

    private boolean loadMore(){
        if (!moreload)
            return false;

        loadMoreLeaderboard(startPoint);
        return true;
    }

    private void loadMoreLeaderboard(final int startPoint1){

        if (CommonUtils.isOnline()){
            Map<String, Object> queryMap = new HashedMap();
            queryMap.put("start", startPoint1);
            queryMap.put("limit", INCREMENT);
            if (countryDto != null){
                if (countryDto.mId > 0)
                    queryMap.put("country_id", countryDto.mId);
            }

            if (stateDto != null)
                queryMap.put("state_id", stateDto.mId);

            queryMap.put("age", minage + "-" + maxage);
            queryMap.put("weight", minweight + "-" + maxweight);

            if (!gender.equalsIgnoreCase(getResources().getString(R.string.any)))
                queryMap.put("gender", gender.toLowerCase());

            Log.e("Super", "explore  = " + SharedUtils.getHeader() + "   " +  minage + "   " + maxage + "    " + minweight + "   " + maxweight + "   " + gender);

            RetrofitSingleton.DATA_REST.getExplore(SharedUtils.getHeader(), queryMap).enqueue(new IndicatorCallback<ServerLeaderboardResponseDto>(activity, leaderboardDtos.isEmpty()) {
                @Override
                public void onResponse(Call<ServerLeaderboardResponseDto> call, Response<ServerLeaderboardResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        ServerLeaderboardResponseDto leaderboardResponseDto = response.body();

                        if (!leaderboardResponseDto.mError){

                            if (leaderboardResponseDto.mData.size() == 0)
                                moreload = false;
                            else
                                moreload = true;

                            if (leaderboardResponseDto.mData.size() > 0){
                                leaderboardDtos.addAll(leaderboardResponseDto.mData);
                            }

                            startPoint = leaderboardDtos.size();
                            adapter.notifyDataSetChanged();
                        }else {
                            if (!TextUtils.isEmpty(leaderboardResponseDto.mMessage))
                                CommonUtils.showAlert(activity, leaderboardResponseDto.mMessage);
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

        swipeRefreshLayout.setRefreshing(false);
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

        swipeRefreshLayout.setRefreshing(false);
    }

    private void showSortDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sortpopup);

        ListView sortListview = (ListView)dialog.findViewById(R.id.listview);
        final SortListAdapter sortListAdapter = new SortListAdapter(getActivity(), PresetUtil.sortList, currentsortposition);
        sortListview.setAdapter(sortListAdapter);

        sortListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != currentsortposition){
                    getSortFromPosition(position);
                    currentsortposition = position;

                    dialog.dismiss();

                    CommonUtils.showToastMessage("Sort By : " + sortListAdapter.getItem(position));
                }
            }
        });

        dialog.show();
    }

    private void getSortFromPosition(int position){
        switch (position){
            case 0:
                currentSort = SORT.Rank;
                break;

            case 1:
                currentSort = SORT.Name;
                break;

            case 2:
                currentSort = SORT.Speed;
                break;

            case 3:
                currentSort = SORT.Power;
                break;

            case 4:
                currentSort = SORT.Punchcount;
                break;

            case 5:
                currentSort = SORT.Challenges;
                break;

            case 6:
                currentSort = SORT.Hours;
                break;
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

    public Comparator<LeaderboardDto> RANK_GENERATOR = new Comparator<LeaderboardDto>() {
        @Override
        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
            return rhs.mPunchCount - lhs.mPunchCount;
        }
    };

    public Comparator<LeaderboardDto> RANK_COMPAREATOR = new Comparator<LeaderboardDto>() {
        @Override
        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
            return lhs.mRank - rhs.mRank;
        }
    };

    public Comparator<LeaderboardDto> RANK_COMPAREATOR_DOWN = new Comparator<LeaderboardDto>() {
        @Override
        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
            return rhs.mRank - lhs.mRank;
        }
    };

    public Comparator<LeaderboardDto> WEIGHT_COMPAREATOR = new Comparator<LeaderboardDto>() {
        @Override
        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
            return rhs.mUser.mWeight - lhs.mUser.mWeight;
        }
    };

    public Comparator<LeaderboardDto> WEIGHT_COMPAREATOR_DOWN = new Comparator<LeaderboardDto>() {
        @Override
        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
            return lhs.mUser.mWeight - rhs.mUser.mWeight;
        }
    };

    public Comparator<LeaderboardDto> AGE_COMPAREATOR = new Comparator<LeaderboardDto>() {
        @Override
        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
            return rhs.mUser.mAge - lhs.mUser.mAge;
        }
    };

    public Comparator<LeaderboardDto> AGE_COMPAREATOR_DOWN = new Comparator<LeaderboardDto>() {
        @Override
        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
            return lhs.mUser.mAge - rhs.mUser.mAge;
        }
    };

    public Comparator<LeaderboardDto> COUNTRY_COMPAREATOR = new Comparator<LeaderboardDto>() {
        @Override
        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
            return rhs.mUser.mCountry.mName.toLowerCase().compareToIgnoreCase(lhs.mUser.mCountry.mName.toLowerCase());
        }
    };

    public Comparator<LeaderboardDto> COUNTRY_COMPAREATOR_DOWN = new Comparator<LeaderboardDto>() {
        @Override
        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
            return lhs.mUser.mCountry.mName.toLowerCase().compareToIgnoreCase(rhs.mUser.mCountry.mName.toLowerCase());
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == AppConst.FILTER_REQUEST_CODE){
                countryDto = (CountryStateCityDto)data.getSerializableExtra(AppConst.COUNTRY);
                stateDto = (CountryStateCityDto)data.getSerializableExtra(AppConst.STATE);
                gender = data.getStringExtra(AppConst.GENDER);
                minweight = data.getIntExtra(AppConst.MIN_WEIGHT, AppConst.WEIGHT_MIN);
                maxweight = data.getIntExtra(AppConst.MAX_WEIGHT, AppConst.WEIGHT_MAX);
                minage = data.getIntExtra(AppConst.MIN_AGE, AppConst.AGE_MIN);
                maxage = data.getIntExtra(AppConst.MAX_AGE, AppConst.AGE_MAX);

                startPoint = 0;
                leaderboardDtos.clear();
                loadMoreLeaderboard(0);

//                loadLeaderboards(countryDto.mId);

                if (AppConst.DEBUG){
                    if (countryDto != null)
                        Log.e(TAG, "country = " + countryDto.mName);

                    if(stateDto != null)
                        Log.e(TAG, "state = " + stateDto.mName);

                    Log.e(TAG, "min, max .. = " + maxweight + "    " + minweight + "    " + maxage + "     " + minage);
                }
            }
        }
    }
}
