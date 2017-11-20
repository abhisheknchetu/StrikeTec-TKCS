package com.striketec.mobile.activity.onboard;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Response;

public class OnboardActivity extends BaseActivity {

    public static final String TAG = OnboardActivity.class.getSimpleName();

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.indicator) CircleIndicator indicatorView;
    @BindView(R.id.next) Button nextView;

    @BindView(R.id.nevershow_tip) TextView nevershowTip;

    private OnboardPagerAdapter pagerAdapter;

    boolean fromMoreScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        fromMoreScreen = getIntent().getBooleanExtra(AppConst.ONBOARD_FROMMORE, false);
        ButterKnife.bind(this);

        initView();
    }

    private void initView(){
        pagerAdapter = new OnboardPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        indicatorView.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3){
                    nextView.setText(getResources().getString(R.string.continues));
                }else {
                    nextView.setText(getResources().getString(R.string.skip));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (fromMoreScreen){
            nevershowTip.setVisibility(View.INVISIBLE);
        }else
            nevershowTip.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.next, R.id.nevershow_tip})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.next:
                if (!fromMoreScreen)
                    gotoMainScreen();
                else
                    finish();
                break;
            case R.id.nevershow_tip:
                setTip();
                break;
        }
    }

    private void setTip(){
        if (CommonUtils.isOnline()){

            Map<String, Object> params = new HashedMap();
            params.put("show_tip", false);

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

                            gotoMainScreen();

                        }else {
                            if (!TextUtils.isEmpty(responseDto.mMessage))
                                CommonUtils.showAlert(OnboardActivity.this, responseDto.mMessage);

                            gotoMainScreen();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(OnboardActivity.this, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));

            gotoMainScreen();
        }
    }

    private void gotoMainScreen(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
