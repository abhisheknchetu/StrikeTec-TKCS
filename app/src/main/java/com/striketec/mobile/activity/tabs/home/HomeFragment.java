package com.striketec.mobile.activity.tabs.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.chat.RecentChatActivity;
import com.striketec.mobile.activity.leaderboard.ExploreLeaderboardActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.activity.tabs.home.notification.NotificationsActivity;
import com.striketec.mobile.activity.tabs.training.quickstart.QuickstartSettingsActivity;
import com.striketec.mobile.activity.tabs.training.quickstart.TrainingNoroundSettingsFragment;
import com.striketec.mobile.activity.tabs.training.quickstart.TrainingRoundSettingsFragment;
import com.striketec.mobile.customview.CustomViewPager;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeFragment extends Fragment {

    public static String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.viewpager) CustomViewPager viewPager;

    private MyPagerAdapter pagerAdapter;
    private MainActivity mainActivity;

    HomeFeedFragment homeFeedFragment;
    HomeMyProgressFragment homeMyProgressFragment;

    public static HomeFragment homeFragment;
    private static Context mContext;

    public static HomeFragment newInstance(Context context) {
        mContext = context;
        homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews(){
        pagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setPagingEnabled(false);
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
    }

    @OnClick({R.id.search, R.id.chat, R.id.notification})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.search:
                Intent exploreleaderboardIntent = new Intent(mainActivity, ExploreLeaderboardActivity.class);
                startActivity(exploreleaderboardIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.chat:
                Intent recentIntent = new Intent(mainActivity, RecentChatActivity.class);
                startActivity(recentIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.notification:
                Intent notificationIntent = new Intent(mainActivity, NotificationsActivity.class);
                startActivity(notificationIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    public void update(){
        if (homeMyProgressFragment != null)
            homeMyProgressFragment.update();
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

        public final int PAGE_COUNT = 2;

        private String mTabsTitle[] = {
                getResources().getString(R.string.feed),
                getResources().getString(R.string.myprogress)
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
                    homeFeedFragment = HomeFeedFragment.newInstance(mainActivity);
                    return homeFeedFragment;

                case 1:
                    homeMyProgressFragment = HomeMyProgressFragment.newInstance(mainActivity);
                    return homeMyProgressFragment;
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
