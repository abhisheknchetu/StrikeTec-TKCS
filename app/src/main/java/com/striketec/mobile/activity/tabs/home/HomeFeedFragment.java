package com.striketec.mobile.activity.tabs.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.striketec.mobile.R;

import java.lang.reflect.Field;

import butterknife.ButterKnife;


public class HomeFeedFragment extends Fragment {

    public static String TAG = HomeFeedFragment.class.getSimpleName();

    public static HomeFeedFragment homeFeedFragment;
    private static Context mContext;

    public static HomeFeedFragment newInstance(Context context) {
        mContext = context;
        homeFeedFragment = new HomeFeedFragment();
        return homeFeedFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_feed, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews(){
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
