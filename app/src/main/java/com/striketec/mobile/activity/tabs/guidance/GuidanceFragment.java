package com.striketec.mobile.activity.tabs.guidance;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.customview.CustomViewPager;
import com.striketec.mobile.dto.VideoDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GuidanceFragment extends Fragment {

    public static String TAG = GuidanceFragment.class.getSimpleName();

    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.viewpager) CustomViewPager viewPager;

    @BindView(R.id.main_container) LinearLayout mainView;
    @BindView(R.id.child_container) FrameLayout childView;

    private MyPagerAdapter pagerAdapter;
    private MainActivity mainActivity;

    GuidanceChildFragment workoutChildFragment, tutorialChildFragment, drillChildFragment, essentialChildFragment;

    enum Status{
        None,
        Search,
        Favorite
    }

    private Status status = Status.None;

    public static GuidanceFragment guidanceFragment;
    private static Context mContext;

    public static GuidanceFragment newInstance(Context context) {
        mContext = context;
        guidanceFragment = new GuidanceFragment();
        return guidanceFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivity = (MainActivity)getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_guidance, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    @OnClick({R.id.search_layout, R.id.favourite_layout})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.search_layout:
                addChildFragment(true);
                break;

            case R.id.favourite_layout:
                addChildFragment(false);
                break;
        }
    }

    private void initViews(){
        pagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

        if (mTabLayout != null) {
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab != null && tab.getCustomView() != null) {
                        ((TextView)tab.getCustomView().findViewById(R.id.tabtitle)).setTextColor(getResources().getColor(R.color.orange));
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    if (tab != null && tab.getCustomView() != null) {
                        ((TextView)tab.getCustomView().findViewById(R.id.tabtitle)).setTextColor(getResources().getColor(R.color.white));
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });

            mTabLayout.setupWithViewPager(viewPager);

            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(pagerAdapter.getTabView(i));
                }
            }

            ((TextView)mTabLayout.getTabAt(0).getCustomView().findViewById(R.id.tabtitle)).setTextColor(getResources().getColor(R.color.orange));

        }

        mainView.setVisibility(View.VISIBLE);
        childView.setVisibility(View.INVISIBLE);
    }

    public void updateViewCount(VideoDto videoDto){
        if (workoutChildFragment != null)
            workoutChildFragment.updateViewCount(videoDto);

        if (tutorialChildFragment != null)
            tutorialChildFragment.updateViewCount(videoDto);

        if (drillChildFragment != null)
            drillChildFragment.updateViewCount(videoDto);

        if (essentialChildFragment != null)
            essentialChildFragment.updateViewCount(videoDto);

        if (status == Status.Search){
            GuidanceSearchFragment searchFragment = (GuidanceSearchFragment) getChildFragmentManager().findFragmentByTag(GuidanceSearchFragment.TAG);
            searchFragment.updateViewCount(videoDto);
        }

        if (status == Status.Favorite){
            GuidanceFavoriteFragment favoriteFragment = (GuidanceFavoriteFragment)getChildFragmentManager().findFragmentByTag(GuidanceFavoriteFragment.TAG);
            favoriteFragment.updateViewCount(videoDto);
        }
    }

    public void updateFavoriteStatus(VideoDto videoDto){
        if (workoutChildFragment != null)
            workoutChildFragment.updateFavoriteStatus(videoDto);

        if (tutorialChildFragment != null)
            tutorialChildFragment.updateFavoriteStatus(videoDto);

        if (drillChildFragment != null)
            drillChildFragment.updateFavoriteStatus(videoDto);

        if (essentialChildFragment != null)
            essentialChildFragment.updateFavoriteStatus(videoDto);
    }

    private void addChildFragment(boolean isSearch){
        mainView.setVisibility(View.INVISIBLE);
        childView.setVisibility(View.VISIBLE);

        status = isSearch? Status.Search : Status.Favorite;

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = isSearch? new GuidanceSearchFragment() : new GuidanceFavoriteFragment();
        String tag = isSearch? GuidanceSearchFragment.TAG : GuidanceFavoriteFragment.TAG;
        fragmentTransaction.replace(R.id.child_container, fragment, tag);
        fragmentTransaction.commit();
    }

    public void removeChildFragment(Fragment childFragment){
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if (childFragment != null){
            fragmentTransaction.remove(childFragment);
        }

        fragmentTransaction.commit();

        mainView.setVisibility(View.VISIBLE);
        childView.setVisibility(View.INVISIBLE);

        status = Status.None;
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

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 4;

        private String mTabsTitle[] = {
                getResources().getString(R.string.guidance_workout),
                getResources().getString(R.string.guidance_tutorial),
                getResources().getString(R.string.guidance_drill),
                getResources().getString(R.string.guidance_essential)
        };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View view = LayoutInflater.from(mainActivity).inflate(R.layout.item_tab_text, null);
            TextView titleView = (TextView)view.findViewById(R.id.tabtitle);
            titleView.setText(mTabsTitle[position]);
            titleView.setTextColor(getResources().getColor(R.color.white));

            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    workoutChildFragment = GuidanceChildFragment.newInstance(mainActivity, AppConst.GUIDANCE_TYPE_WORKOUTS);
                    return workoutChildFragment;

                case 1:
                    tutorialChildFragment = GuidanceChildFragment.newInstance(mainActivity, AppConst.GUIDANCE_TYPE_TUTORIALS);
                    return tutorialChildFragment;

                case 2:
                    drillChildFragment = GuidanceChildFragment.newInstance(mainActivity, AppConst.GUIDANCE_TYPE_DRILLS);
                    return drillChildFragment;

                case 3:
                    essentialChildFragment = GuidanceChildFragment.newInstance(mainActivity, AppConst.GUIDANCE_TYPE_ESSENTIALS);
                    return essentialChildFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }
}
