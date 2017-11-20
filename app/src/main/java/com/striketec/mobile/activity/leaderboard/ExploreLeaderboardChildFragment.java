package com.striketec.mobile.activity.leaderboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.adapters.CompareLeaderboardListAdapter;
import com.striketec.mobile.adapters.ExploreListAdapter;
import com.striketec.mobile.adapters.LeaderboardListAdapter;
import com.striketec.mobile.customview.EndlessScrollListener;
import com.striketec.mobile.dto.LeaderboardDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class ExploreLeaderboardChildFragment extends Fragment {

    public static String TAG = ExploreLeaderboardChildFragment.class.getSimpleName();
    private final int INCREMENT = 1000;

    enum SORT {
        RANK,
        WEIGHT,
        AGE,
        COUNTRY
    }

    @BindView(R.id.swipe) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.listview) ListView listView;
    @BindView(R.id.sort_rank) TextView sortrankView;
    @BindView(R.id.sort_rank_img) ImageView sortrankImgView;
    @BindView(R.id.sort_weight) TextView sortweightView;
    @BindView(R.id.sort_weight_img) ImageView sortweightImgView;
    @BindView(R.id.sort_age) TextView sortageView;
    @BindView(R.id.sort_age_img) ImageView sortageImgView;
    @BindView(R.id.sort_country) TextView sortcountryView;
    @BindView(R.id.sort_country_img) ImageView sortcountryImgView;


    private boolean moreload = true;
    private int startPoint = 0;
    private String type;

    Activity activity;

    ArrayList<LeaderboardDto> leaderboardDtos = new ArrayList<>();
    ArrayList<LeaderboardDto> totalleaderboardDtos = new ArrayList<>();
    LeaderboardDto currentUserDto;

    private SORT currentSort = SORT.RANK;

    private boolean sortrankDesc = true, sortweightDesc = true, sortagedesc = true, sortcountrydesc = true;

    ArrayAdapter adapter;

    public static ExploreLeaderboardChildFragment exploreLeaderboardChildFragment;
    private static Context mContext;

    public static ExploreLeaderboardChildFragment newInstance(Context context, String type) {
        mContext = context;
        exploreLeaderboardChildFragment = new ExploreLeaderboardChildFragment();
        Bundle arg = new Bundle();
        arg.putString(AppConst.FEED_TYPE, type);
        exploreLeaderboardChildFragment.setArguments(arg);
        return exploreLeaderboardChildFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exploreleaderboard, container, false);

        type = getArguments().getString(AppConst.FEED_TYPE);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    @OnClick({R.id.sort_rank_parent, R.id.sort_weight_parent, R.id.sort_age_parent, R.id.sort_country_parent})
    public void onClick(View view){
//        switch (view.getId()){
//            case R.id.sort_rank_parent:
//                if (currentSort == SORT.RANK){
//                    sortrankDesc= !sortrankDesc;
//                }else {
//                    currentSort = SORT.RANK;
//                }
//
//                updateSortView();
//
//                break;
//
//            case R.id.sort_weight_parent:
//
//                if (currentSort == SORT.WEIGHT){
//                    sortweightDesc = !sortweightDesc;
//                }else {
//                    currentSort = SORT.WEIGHT;
//                }
//
//                updateSortView();
//                break;
//
//            case R.id.sort_age_parent:
//
//                if (currentSort == SORT.AGE){
//                    sortagedesc = !sortagedesc;
//                }else {
//                    currentSort = SORT.AGE;
//                }
//
//                updateSortView();
//                break;
//            case R.id.sort_country_parent:
//
//                if (currentSort == SORT.COUNTRY){
//                    sortcountrydesc = !sortcountrydesc;
//                }else {
//                    currentSort = SORT.COUNTRY;
//                }
//
//                updateSortView();
//                break;
//        }
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(int position){
        if (type.equalsIgnoreCase(AppConst.FEED_COMPARE_LEADERBOARD)){
            ((CompareLeaderboardListAdapter)adapter).setSelectedPosition(position);
            adapter.notifyDataSetChanged();
        }else if (type.equalsIgnoreCase(AppConst.FEED_LEADERBOARD)){
            ((LeaderboardListAdapter)adapter).setSelectedPosition(position);
            adapter.notifyDataSetChanged();
        }
    }

    private void initViews(){
//
//        if (type.equalsIgnoreCase(AppConst.FEED_EXPLORE)){
//            adapter = new ExploreListAdapter(activity, leaderboardDtos);
//        }else if (type.equalsIgnoreCase(AppConst.FEED_LEADERBOARD)){
//            adapter = new LeaderboardListAdapter(activity, leaderboardDtos);
//            listView.setPadding(0, 0, 0, 0);
//        }else {
//            adapter = new CompareLeaderboardListAdapter(activity, leaderboardDtos);
//            listView.setPadding(0, 0, 0, 0);
//        }
//
//        listView.setAdapter(adapter);
//
//        listView.setOnScrollListener(new EndlessScrollListener() {
//            @Override
//            public boolean onLoadMore(int page, int totalItemsCount) {
//                return loadMore();
//            }
//        });
//
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                leaderboardDtos.clear();
//                startPoint = 0;
//
//                loadLeaderboards();
//                //loadMoreUsers(0);
//            }
//        });
//
//        loadLeaderboards();
    }

//    private void updateSortView(){
//        if (currentSort == SORT.RANK){
//
//            if (sortrankDesc){
//                sortrankImgView.setImageResource(R.drawable.sort_active_down);
//                Collections.sort(leaderboardDtos, RANK_COMPAREATOR);
//            }else {
//                sortrankImgView.setImageResource(R.drawable.sort_active_up);
//                Collections.sort(leaderboardDtos, RANK_COMPAREATOR_DOWN);
//            }
//
//            sortrankView.setTextColor(getResources().getColor(R.color.punches_text));
//            sortweightView.setTextColor(getResources().getColor(R.color.first_punch));
//            sortageView.setTextColor(getResources().getColor(R.color.first_punch));
//            sortcountryView.setTextColor(getResources().getColor(R.color.first_punch));
//
//            sortweightImgView.setImageResource(R.drawable.sort_deactive);
//            sortageImgView.setImageResource(R.drawable.sort_deactive);
//            sortcountryImgView.setImageResource(R.drawable.sort_deactive);
//        }else if(currentSort == SORT.WEIGHT){
//            if (sortweightDesc){
//                sortweightImgView.setImageResource(R.drawable.sort_active_down);
//                Collections.sort(leaderboardDtos, WEIGHT_COMPAREATOR);
//            }else {
//                sortweightImgView.setImageResource(R.drawable.sort_active_up);
//                Collections.sort(leaderboardDtos, WEIGHT_COMPAREATOR_DOWN);
//            }
//
//            sortweightView.setTextColor(getResources().getColor(R.color.punches_text));
//            sortrankView.setTextColor(getResources().getColor(R.color.first_punch));
//            sortageView.setTextColor(getResources().getColor(R.color.first_punch));
//            sortcountryView.setTextColor(getResources().getColor(R.color.first_punch));
//
//            sortrankImgView.setImageResource(R.drawable.sort_deactive);
//            sortageImgView.setImageResource(R.drawable.sort_deactive);
//            sortcountryImgView.setImageResource(R.drawable.sort_deactive);
//        }else if (currentSort == SORT.AGE){
//            if (sortagedesc){
//                sortageImgView.setImageResource(R.drawable.sort_active_down);
//                Collections.sort(leaderboardDtos,AGE_COMPAREATOR);
//            }else {
//                sortageImgView.setImageResource(R.drawable.sort_active_up);
//                Collections.sort(leaderboardDtos,AGE_COMPAREATOR_DOWN);
//            }
//
//            sortageView.setTextColor(getResources().getColor(R.color.punches_text));
//            sortweightView.setTextColor(getResources().getColor(R.color.first_punch));
//            sortrankView.setTextColor(getResources().getColor(R.color.first_punch));
//            sortcountryView.setTextColor(getResources().getColor(R.color.first_punch));
//
//            sortweightImgView.setImageResource(R.drawable.sort_deactive);
//            sortrankImgView.setImageResource(R.drawable.sort_deactive);
//            sortcountryImgView.setImageResource(R.drawable.sort_deactive);
//        }else {
//            if (sortcountrydesc){
//                sortcountryImgView.setImageResource(R.drawable.sort_active_down);
//                Collections.sort(leaderboardDtos,COUNTRY_COMPAREATOR);
//            }else {
//                sortcountryImgView.setImageResource(R.drawable.sort_active_up);
//                Collections.sort(leaderboardDtos,COUNTRY_COMPAREATOR_DOWN);
//            }
//
//            sortcountryView.setTextColor(getResources().getColor(R.color.punches_text));
//            sortweightView.setTextColor(getResources().getColor(R.color.first_punch));
//            sortageView.setTextColor(getResources().getColor(R.color.first_punch));
//            sortrankView.setTextColor(getResources().getColor(R.color.first_punch));
//
//            sortweightImgView.setImageResource(R.drawable.sort_deactive);
//            sortageImgView.setImageResource(R.drawable.sort_deactive);
//            sortrankImgView.setImageResource(R.drawable.sort_deactive);
//        }
//
//        if (type.equalsIgnoreCase(AppConst.FEED_EXPLORE)){
//            ((ExploreListAdapter)adapter).setData(leaderboardDtos);
//             adapter.notifyDataSetChanged();
//        }else if (type.equalsIgnoreCase(AppConst.FEED_LEADERBOARD)){
//            ((LeaderboardListAdapter)adapter).setData(leaderboardDtos);
//            adapter.notifyDataSetChanged();
//        }else {
//            ((CompareLeaderboardListAdapter)adapter).setData(leaderboardDtos);
//            adapter.notifyDataSetChanged();
//        }
//    }

    private boolean loadMore(){
        if (!moreload)
            return false;

//        loadMOreUsers(startPoint);
        return true;
    }

    private void loadMOreUsers(final int startPoint1){

//        if (CommonUtils.isOnline()){
//            RetrofitSingleton.VIDEO_REST.getVideos(SharedUtils.getHeader(), queryMap).enqueue(new IndicatorCallback<VideoResponseDto>(exploreLeaderboardActivity, false) {
//                @Override
//                public void onResponse(Call<VideoResponseDto> call, Response<VideoResponseDto> response) {
//                    super.onResponse(call, response);
//
//                    if (response.body() != null){
//                        VideoResponseDto videoResponseDto = response.body();
//
//                        if (!videoResponseDto.mError){
//
//                            if (videoResponseDto.mVideos.size() == 0)
//                                moreload = false;
//
//                            if (videoResponseDto.mVideos.size() > 0){
//                                videoDtos.addAll(videoResponseDto.mVideos);
//                            }
//
//                            startPoint = videoDtos.size();
//                            listAdapter.notifyDataSetChanged();
//                        }else {
//                            if (!TextUtils.isEmpty(videoResponseDto.mMessage))
//                                CommonUtils.showAlert(exploreLeaderboardActivity, videoResponseDto.mMessage);
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<VideoResponseDto> call, Throwable t) {
//                    super.onFailure(call, t);
//                    CommonUtils.showAlert(exploreLeaderboardActivity, t.getLocalizedMessage());
//                }
//            });
//        }else {
//            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
//        }

        swipeRefreshLayout.setRefreshing(false);
    }


//    private void loadLeaderboards(){
//
//        leaderboardDtos.clear();
//        totalleaderboardDtos.clear();
//
//        //first user is current user
//        currentUserDto = new LeaderboardDto();
//        currentUserDto.mAge = CommonUtils.getRandomNum(30, 27);
//        currentUserDto.mAvgForce = CommonUtils.getRandomNum(600, 200);
//        currentUserDto.mAvgSpeed = CommonUtils.getRandomNum(35, 10);
//        currentUserDto.mAvgTime = 0.5f;
//        currentUserDto.mWeight = CommonUtils.getRandomNum(150, 100);
//        currentUserDto.mPunchCount = CommonUtils.getRandomNum(90000, 30000);
//        currentUserDto.mSessionCount = 100;
//
//        int mPro = CommonUtils.getRandomNum(20, 10);
//        if (mPro < 15)
//            currentUserDto.mPro = true;
//        else
//            currentUserDto.mPro = false;
//
//
//
//        int mCountry = CommonUtils.getRandomNum(20, 1);
//
//        if (mCountry < 5)
//            currentUserDto.mCountry = "United State";
//        else if (mCountry < 10)
//            currentUserDto.mCountry = "China";
//        else if (mCountry < 15)
//            currentUserDto.mCountry = "India";
//        else
//            currentUserDto.mCountry = "Russia";
//
//        for (int i = 0; i < INCREMENT; i++){
//            LeaderboardDto leaderboardDto = new LeaderboardDto();
//            leaderboardDto.mAge = CommonUtils.getRandomNum(30, 27);
//            leaderboardDto.mAvgForce = CommonUtils.getRandomNum(600, 200);
//            leaderboardDto.mAvgSpeed = CommonUtils.getRandomNum(35, 10);
//            leaderboardDto.mAvgTime = 0.5f;
//            leaderboardDto.mWeight = CommonUtils.getRandomNum(150, 100);
//            leaderboardDto.mPunchCount = CommonUtils.getRandomNum(90000, 30000);
//            leaderboardDto.mSessionCount = 100;
//
//            int mPro1 = CommonUtils.getRandomNum(20, 10);
//            if (mPro1 < 15)
//                leaderboardDto.mPro = true;
//            else
//                leaderboardDto.mPro = false;
//
//            int mFollowing = CommonUtils.getRandomNum(20, 10);
//            if (mFollowing < 15)
//                leaderboardDto.mUserFollowing = true;
//            else
//                leaderboardDto.mUserFollowing = false;
//
//
//            int mCountry1 = CommonUtils.getRandomNum(20, 1);
//
//            if (mCountry1 < 5)
//                leaderboardDto.mCountry = "United State";
//            else if (mCountry1 < 10)
//                leaderboardDto.mCountry = "China";
//            else if (mCountry1 < 15)
//                leaderboardDto.mCountry = "India";
//            else
//                leaderboardDto.mCountry = "Russia";
//
//            leaderboardDtos.add(leaderboardDto);
//        }
//
//        totalleaderboardDtos.addAll(leaderboardDtos);
//        totalleaderboardDtos.add(0, currentUserDto);
//
//        calculateRank();
//    }

//    private void calculateRank(){
//        Collections.sort(leaderboardDtos, RANK_GENERATOR);
//        for (int i = 0; i < leaderboardDtos.size(); i++){
//            LeaderboardDto leaderboardDto = leaderboardDtos.get(i);
//            leaderboardDto.mRank = i + 1;
//            if (i == 0)
//                leaderboardDto.mUserName = "Cornor McGregor";
//            else
//                leaderboardDto.mUserName = "User " + (i + 1);
//        }
//
//        swipeRefreshLayout.setRefreshing(false);
//
//        updateSortView();
//    }

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

//    public Comparator<LeaderboardDto> RANK_GENERATOR = new Comparator<LeaderboardDto>() {
//        @Override
//        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
//            return rhs.mPunchCount - lhs.mPunchCount;
//        }
//    };
//
//    public Comparator<LeaderboardDto> RANK_COMPAREATOR = new Comparator<LeaderboardDto>() {
//        @Override
//        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
//            return lhs.mRank - rhs.mRank;
//        }
//    };
//
//    public Comparator<LeaderboardDto> RANK_COMPAREATOR_DOWN = new Comparator<LeaderboardDto>() {
//        @Override
//        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
//            return rhs.mRank - lhs.mRank;
//        }
//    };
//
//    public Comparator<LeaderboardDto> WEIGHT_COMPAREATOR = new Comparator<LeaderboardDto>() {
//        @Override
//        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
//            return rhs.mWeight - lhs.mWeight;
//        }
//    };
//
//    public Comparator<LeaderboardDto> WEIGHT_COMPAREATOR_DOWN = new Comparator<LeaderboardDto>() {
//        @Override
//        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
//            return lhs.mWeight - rhs.mWeight;
//        }
//    };
//
//    public Comparator<LeaderboardDto> AGE_COMPAREATOR = new Comparator<LeaderboardDto>() {
//        @Override
//        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
//            return rhs.mAge - lhs.mAge;
//        }
//    };
//
//    public Comparator<LeaderboardDto> AGE_COMPAREATOR_DOWN = new Comparator<LeaderboardDto>() {
//        @Override
//        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
//            return lhs.mAge - rhs.mAge;
//        }
//    };
//
//    public Comparator<LeaderboardDto> COUNTRY_COMPAREATOR = new Comparator<LeaderboardDto>() {
//        @Override
//        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
//            return rhs.mCountry.compareToIgnoreCase(lhs.mCountry);
//        }
//    };
//
//    public Comparator<LeaderboardDto> COUNTRY_COMPAREATOR_DOWN = new Comparator<LeaderboardDto>() {
//        @Override
//        public int compare(LeaderboardDto lhs, LeaderboardDto rhs) {
//            return lhs.mCountry.compareToIgnoreCase(rhs.mCountry);
//        }
//    };
}
