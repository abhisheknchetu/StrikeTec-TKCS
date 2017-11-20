package com.striketec.mobile.activity.tabs.more.myplan;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.credential.SignupSensorActivity;
import com.striketec.mobile.activity.tabs.home.HomeFeedFragment;
import com.striketec.mobile.activity.tabs.home.HomeFragment;
import com.striketec.mobile.activity.tabs.home.HomeMyProgressFragment;
import com.striketec.mobile.customview.CustomViewPager;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.SharedUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class MyTrainingplanActivity extends BaseActivity {

    private static final String TAG = MyTrainingplanActivity.class.getSimpleName();

    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    CustomViewPager viewPager;

    private MyPagerAdapter pagerAdapter;

    ProfileQuestionsFragment profileQuestionsFragment;
    TrainingPlanFragment trainingPlanFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplans);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.mytrainingplans));

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

    @OnClick({R.id.back, R.id.next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.next:
                submit();
                break;
        }
    }

    private void submit(){
        if (profileQuestionsFragment == null || trainingPlanFragment == null)
            return;

        final HashMap<String, Object> params = new HashMap<>();

        if (profileQuestionsFragment.checkContinue()){
            if (profileQuestionsFragment.checkUpdateAble() || trainingPlanFragment.checkUpdateable()){
                profileQuestionsFragment.updateParam(params);
                trainingPlanFragment.updateParams(params);
            }else {
                finish();
                return;
            }
        }else
            return;

        if (CommonUtils.isOnline()){
            RetrofitSingleton.USER_REST.updateUser(SharedUtils.getHeader(), params).enqueue(new IndicatorCallback<DefaultResponseDto>(this) {
                @Override
                public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        DefaultResponseDto responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage);

                            if (!TextUtils.isEmpty(responseDto.mMessage))
                                CommonUtils.showToastMessage(responseDto.mMessage);

                            StriketecApp.currentUser.mGender = (String)params.get("gender");
                            StriketecApp.currentUser.mWeight = (int)params.get("weight");
                            StriketecApp.currentUser.mHeight = (int)params.get("height");
                            StriketecApp.currentUser.mStance = (String)params.get("stance");
                            StriketecApp.currentUser.mSkillLevel = (String)params.get("skill_level");
                            StriketecApp.currentUser.mBirthday = DatesUtil.changeDateFormat((String)params.get("birthday"));
                            StriketecApp.currentUser.mCountry = profileQuestionsFragment.countryDto;
                            StriketecApp.currentUser.mState = profileQuestionsFragment.stateDto;
                            StriketecApp.currentUser.mCity = profileQuestionsFragment.cityDto;

                            finish();

                        }else {
                            if (!TextUtils.isEmpty(responseDto.mMessage))
                                CommonUtils.showAlert(MyTrainingplanActivity.this, responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(MyTrainingplanActivity.this, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 2;

        private String mTabsTitle[] = {
                getResources().getString(R.string.profilequestiontitle),
                getResources().getString(R.string.trainingplantitle)
        };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View view = LayoutInflater.from(MyTrainingplanActivity.this).inflate(R.layout.item_tab_text, null);
            TextView titleView = (TextView)view.findViewById(R.id.tabtitle);
            titleView.setText(mTabsTitle[position]);
            titleView.setTextColor(getResources().getColor(R.color.white));

            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    profileQuestionsFragment = ProfileQuestionsFragment.newInstance(MyTrainingplanActivity.this);
                    return profileQuestionsFragment;

                case 1:
                    trainingPlanFragment = TrainingPlanFragment.newInstance(MyTrainingplanActivity.this);
                    return trainingPlanFragment;
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
