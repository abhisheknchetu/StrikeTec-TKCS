package com.striketec.mobile.activity.tabs.training.quickstart;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.tabs.training.TrainingFragment;
import com.striketec.mobile.dto.PresetDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuickstartSettingsActivity extends BaseActivity {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;

    private MyPagerAdapter pagerAdapter;

    private TrainingRoundSettingsFragment roundSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quickstart_settings);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.quickstart_settings));
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
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

    @OnClick({R.id.starttraining, R.id.back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.starttraining:
                startTraining();
                break;

            case R.id.back:
                finish();
                break;
        }
    }

    private void startTraining(){
        Intent trainingIntent = new Intent(this, QuickstartTrainingActivity.class);

        if (mTabLayout.getSelectedTabPosition() == 0){
            if (roundSettingsFragment != null) {
                PresetDto presetDto = roundSettingsFragment.getCurrentPreset();
                trainingIntent.putExtra(AppConst.PRESET, presetDto);
                trainingIntent.putExtra(AppConst.ROUNDTRAINING, true);
            }
        }else {
            trainingIntent.putExtra(AppConst.ROUNDTRAINING, false);
        }

        startActivity(trainingIntent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 2;

        private String mTabsTitle[] = {
                getResources().getString(R.string.rounds),
                getResources().getString(R.string.norounds)
        };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View view = LayoutInflater.from(QuickstartSettingsActivity.this).inflate(R.layout.item_tab_text, null);
            TextView titleView = (TextView)view.findViewById(R.id.tabtitle);
            titleView.setText(mTabsTitle[position]);
            titleView.setTextColor(getResources().getColor(R.color.white));

            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    roundSettingsFragment = TrainingRoundSettingsFragment.newInstance(QuickstartSettingsActivity.this);
                    return roundSettingsFragment;
                case 1:
                    return TrainingNoroundSettingsFragment.newInstance(QuickstartSettingsActivity.this);
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
