package com.striketec.mobile.activity.tabs.guidance;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.adapters.VideoChildListAdapter;
import com.striketec.mobile.customview.EndlessScrollListener;
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
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit2.Call;
import retrofit2.Response;


public class GuidanceFavoriteFragment extends Fragment {

    public static String TAG = GuidanceFavoriteFragment.class.getSimpleName();

    private final int INCREMENT = 25;

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.listview)ListView listView;
    @BindView(R.id.swipe) SwipeRefreshLayout swipeRefreshLayout;

    MainActivity mainActivity;

    private boolean moreload = true;
    private int startPoint = 0;

    ArrayList<VideoDto> videoDtos = new ArrayList<>();
    VideoChildListAdapter listAdapter;

    public static GuidanceFavoriteFragment guidanceSearchFragment;
    private static Context mContext;

    public static Fragment newInstance(Context context) {
        mContext = context;
        guidanceSearchFragment = new GuidanceFavoriteFragment();
        return guidanceSearchFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_guidance_favorite, container, false);

        ButterKnife.bind(this, view);

        initViews();
        loadMoreVideo(0);
        return view;
    }

    @OnClick ({R.id.back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                if (GuidanceFragment.guidanceFragment != null)
                    GuidanceFragment.guidanceFragment.removeChildFragment(this);
                break;
        }
    }

    private void initViews() {

        titleView.setText(getResources().getString(R.string.favorites));

        listAdapter = new VideoChildListAdapter(mainActivity, videoDtos, false);
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

    private void loadMoreVideo(final int startPoint1){

        Map<String, Object> queryMap = new HashedMap();
        queryMap.put("start", startPoint1);
        queryMap.put("limit", INCREMENT);

        if (CommonUtils.isOnline()){
            RetrofitSingleton.VIDEO_REST.getFavoriteVideos(SharedUtils.getHeader(), queryMap).enqueue(new IndicatorCallback<VideoResponseDto>(mainActivity, false) {
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

    private String changeQueryString(String queryString){
        return queryString.replace(" ", "+");
    }
}

