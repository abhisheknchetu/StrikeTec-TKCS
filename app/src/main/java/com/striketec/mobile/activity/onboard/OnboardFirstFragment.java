package com.striketec.mobile.activity.onboard;

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


public class OnboardFirstFragment extends Fragment {

    public static String TAG = OnboardFirstFragment.class.getSimpleName();

    public static OnboardFirstFragment tipFirstFragment;
    private static Context mContext;

    public static OnboardFirstFragment newInstance(Context context) {
        mContext = context;
        tipFirstFragment = new OnboardFirstFragment();
        return tipFirstFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_onboard_first, container, false);

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
