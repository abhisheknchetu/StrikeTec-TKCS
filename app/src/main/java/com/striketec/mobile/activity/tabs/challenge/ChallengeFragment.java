package com.striketec.mobile.activity.tabs.challenge;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.DefaultFragment;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.activity.tabs.home.HomeFeedFragment;
import com.striketec.mobile.activity.tabs.home.HomeFragment;
import com.striketec.mobile.activity.tabs.home.HomeMyProgressFragment;
import com.striketec.mobile.customview.CustomViewPager;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChallengeFragment extends Fragment {

    public static String TAG = ChallengeFragment.class.getSimpleName();

    @BindView(R.id.back) ImageView backBtn;
    @BindView(R.id.second_image) ImageView secondImageView;
    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.viewpager) CustomViewPager viewPager;

    private MyPagerAdapter pagerAdapter;
    private MainActivity mainActivity;

    public static ChallengeFragment challengeFragment;
    private static Context mContext;

    TournamentsFragment tournamentsFragment;
    GamesFragment gamesFragment;
    BattlesFragment battlesFragment;

    public static ChallengeFragment newInstance(Context context) {
        mContext = context;
        challengeFragment = new ChallengeFragment();
        return challengeFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews(){
        backBtn.setVisibility(View.INVISIBLE);
        titleView.setText(getResources().getString(R.string.tip3_title));
        secondImageView.setImageResource(R.drawable.icon_plus);
        secondImageView.setVisibility(View.VISIBLE);

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

        public final int PAGE_COUNT = 3;

        private String mTabsTitle[] = {
                getResources().getString(R.string.battles),
                getResources().getString(R.string.tournaments),
                getResources().getString(R.string.games)
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
                    battlesFragment = BattlesFragment.newInstance(mainActivity);
                    return battlesFragment;

                case 1:
                    tournamentsFragment = TournamentsFragment.newInstance(mainActivity);
                    return tournamentsFragment;

                case 2:
                    gamesFragment = GamesFragment.newInstance(mainActivity);
                    return gamesFragment;
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
