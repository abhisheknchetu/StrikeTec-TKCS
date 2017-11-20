package com.striketec.mobile.activity.tabs.guidance;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.credential.SigninActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.adapters.VideoMainListAdapter;
import com.striketec.mobile.customview.EndlessScrollListener;
import com.striketec.mobile.dto.AuthResponseDto;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.dto.VideoDto;
import com.striketec.mobile.dto.VideoResponseDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import org.apache.commons.collections.map.HashedMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;


public class GuidanceChildFragment extends Fragment {

    public static String TAG = GuidanceChildFragment.class.getSimpleName();

    private final int INCREMENT = 25;

    @BindView(R.id.listview) ListView listView;
    @BindView(R.id.swipe) SwipeRefreshLayout swipeRefreshLayout;

    public static GuidanceChildFragment guidanceChildFragment;
    private static Context mContext;

    String type = "";

    private MainActivity mainActivity;

    private boolean moreload = true;
    private int startPoint = 0;
    private ArrayList<VideoDto> videoDtos = new ArrayList<>();

    VideoMainListAdapter listAdapter;

    public static GuidanceChildFragment newInstance(Context context, String type) {
        mContext = context;
        guidanceChildFragment = new GuidanceChildFragment();

        Bundle bundle = new Bundle();
        bundle.putString(AppConst.FEED_TYPE, type);
        guidanceChildFragment.setArguments(bundle);

        return guidanceChildFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_guidance_child, container, false);
        type = getArguments().getString(AppConst.FEED_TYPE);

        ButterKnife.bind(this, view);

        initViews();

        loadMoreVideo(0);

        return view;
    }

    private void initViews(){

        listAdapter = new VideoMainListAdapter(mainActivity, videoDtos);
        listView.setAdapter(listAdapter);

        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                return loadMore();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                videoDtos.clear();
                startPoint = 0;
                loadMoreVideo(0);
            }
        });
    }

    private boolean loadMore(){
        if (!moreload)
            return false;

        loadMoreVideo(startPoint);
        return true;
    }

    public void updateViewCount(VideoDto videoDto){
        if (videoDtos.size() > 0){
            for (int i = 0; i < videoDtos.size(); i++){
                VideoDto temp = videoDtos.get(i);
                if (temp.mId == videoDto.mId){
                    temp.mViewCounts = videoDto.mViewCounts;
                    break;
                }
            }

            listAdapter.notifyDataSetChanged();
        }
    }

    public void updateFavoriteStatus(VideoDto videoDto){
        if (videoDtos.size() > 0){
            for (int i = 0; i < videoDtos.size(); i++){
                VideoDto temp = videoDtos.get(i);
                if (temp.mId == videoDto.mId){
                    temp.mFavorited = videoDto.mFavorited;
                    break;
                }
            }

            listAdapter.notifyDataSetChanged();
        }
    }

    private void loadMoreVideo(final int startPoint1){
        Map<String, Object> queryMap = new HashedMap();
        queryMap.put("category_id", getCategoryId());
        queryMap.put("start", startPoint1);
        queryMap.put("limit", INCREMENT);

        if (CommonUtils.isOnline()){
            RetrofitSingleton.VIDEO_REST.getVideos(SharedUtils.getHeader(), queryMap).enqueue(new IndicatorCallback<VideoResponseDto>(mainActivity, false) {
                @Override
                public void onResponse(Call<VideoResponseDto> call, Response<VideoResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        VideoResponseDto videoResponseDto = response.body();

                        if (!videoResponseDto.mError){

                            if (videoResponseDto.mVideos.size() == 0)
                                moreload = false;

                            if (videoResponseDto.mVideos.size() > 0){
                                videoDtos.addAll(videoResponseDto.mVideos);
                            }

                            startPoint = videoDtos.size();
                            listAdapter.notifyDataSetChanged();

                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " + videoResponseDto.mMessage + "      " + videoDtos.size());

                        }else {
                            if (!TextUtils.isEmpty(videoResponseDto.mMessage))
                                CommonUtils.showAlert(mainActivity, videoResponseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<VideoResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(mainActivity, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    private int getCategoryId(){
        if (type.equalsIgnoreCase(AppConst.GUIDANCE_TYPE_WORKOUTS)){
            return 1;
        }else if (type.equalsIgnoreCase(AppConst.GUIDANCE_TYPE_TUTORIALS)){
            return 2;
        }else if (type.equalsIgnoreCase(AppConst.GUIDANCE_TYPE_DRILLS)){
            return 3;
        }else if (type.equalsIgnoreCase(AppConst.GUIDANCE_TYPE_ESSENTIALS)){
            return 4;
        }

        return 0;
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
}
