package com.striketec.mobile.activity.leaderboard;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.customview.CustomViewPager;
import com.striketec.mobile.interfaces.FollowCallback;
import com.striketec.mobile.util.AppConst;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class ExploreLeaderboardActivity extends BaseActivity implements FollowCallback{

    private String TAG = ExploreLeaderboardActivity.class.getSimpleName();

    @BindView(R.id.searchview)  EditText searchView;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.viewpager) CustomViewPager viewPager;

    private MyPagerAdapter pagerAdapter;
    ExploreChildFragment exploreFragment;
    LeaderboardChildFragment leaderboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exploreleaderboard);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    @OnClick({R.id.cancelview})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.cancelview:

                hideSoftKeyboard();

                searchView.setText("");
                searchView.clearFocus();

                break;
        }
    }

    @OnEditorAction(R.id.searchview)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event){

        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchView.clearFocus();
            hideSoftKeyboard();
            performSearch();
            return true;
        }

        return false;
    }

    private void performSearch(){

    }

    private void initViews(){
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void followChanged(int userId) {
        if (exploreFragment != null)
            exploreFragment.updateUserFollow(userId);

        if (leaderboardFragment != null)
            leaderboardFragment.updateUserFollow(userId);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 2;

        private String mTabsTitle[] = {
                getResources().getString(R.string.explore),
                getResources().getString(R.string.leaderboard)
        };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View view = LayoutInflater.from(ExploreLeaderboardActivity.this).inflate(R.layout.item_tab_text, null);
            TextView titleView = (TextView)view.findViewById(R.id.tabtitle);
            titleView.setText(mTabsTitle[position]);
            titleView.setTextColor(getResources().getColor(R.color.white));

            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    exploreFragment = ExploreChildFragment.newInstance(ExploreLeaderboardActivity.this);
                    return exploreFragment;

                case 1:
                    leaderboardFragment = LeaderboardChildFragment.newInstance(ExploreLeaderboardActivity.this, AppConst.FEED_LEADERBOARD);
                    return leaderboardFragment;
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
