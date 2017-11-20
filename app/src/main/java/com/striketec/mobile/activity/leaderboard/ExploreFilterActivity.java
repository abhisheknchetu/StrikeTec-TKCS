package com.striketec.mobile.activity.leaderboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.credential.CountrySelectActivity;
import com.striketec.mobile.dto.CountryStateCityDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.PresetUtil;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExploreFilterActivity extends BaseActivity {

    private static final String TAG = ExploreFilterActivity.class.getSimpleName();

    @BindView(R.id.title)  TextView titleView;
    @BindView(R.id.second_title) TextView secondtitleView;
    @BindView(R.id.country)  Button countryBtn;
    @BindView(R.id.state) Button stateBtn;
    @BindView(R.id.radio_group) RadioGroup genderGroup;
    @BindView(R.id.tab_any)  RadioButton anyRadioBtn;
    @BindView(R.id.tab_male) RadioButton maleRadioBtn;
    @BindView(R.id.tab_female) RadioButton femaleRadioBtn;

    @BindView(R.id.weight_rangeseekbar) RangeSeekBar weightSeekbar;
    @BindView(R.id.age_rangeseekbar) RangeSeekBar ageSeekbar;

    CountryStateCityDto countryDto, stateDto;
    String gender;
    int minweight, maxweight, minage, maxage;

    boolean filterisupdated = false;

    CountryStateCityDto oldcountryDto, oldstateDto;
    String oldgender;
    int oldminweight, oldmaxweight, oldminage, oldmaxage;

    boolean countryischagned = false;
    boolean stateischanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorefilter);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        countryDto = (CountryStateCityDto)getIntent().getSerializableExtra(AppConst.COUNTRY);
        stateDto = (CountryStateCityDto)getIntent().getSerializableExtra(AppConst.STATE);
        gender = getIntent().getStringExtra(AppConst.GENDER);
        minweight = getIntent().getIntExtra(AppConst.MIN_WEIGHT, AppConst.WEIGHT_MIN);
        maxweight = getIntent().getIntExtra(AppConst.MAX_WEIGHT, AppConst.WEIGHT_MAX);
        minage = getIntent().getIntExtra(AppConst.MIN_AGE, AppConst.AGE_MIN);
        maxage = getIntent().getIntExtra(AppConst.MAX_AGE, AppConst.AGE_MAX);

        oldcountryDto = countryDto;
        oldstateDto = stateDto;
        oldgender = gender;
        oldminweight = minweight;
        oldmaxweight = maxweight;
        oldmaxage = maxage;
        oldminage = minage;

        if (minweight <= AppConst.WEIGHT_MIN)
            minweight = AppConst.WEIGHT_MIN;

        if (maxweight >= AppConst.WEIGHT_MAX)
            maxweight = AppConst.WEIGHT_MAX;

        if (minage <= AppConst.AGE_MIN || minage >= AppConst.AGE_MAX)
            minage = AppConst.AGE_MIN;

        if (maxage >= AppConst.AGE_MAX || maxage <= AppConst.AGE_MIN)
            maxage = AppConst.AGE_MAX;

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.listfilter));
        secondtitleView.setText(getResources().getString(R.string.apply));
        secondtitleView.setVisibility(View.VISIBLE);

        weightSeekbar.setSelectedMaxValue(maxweight);
        weightSeekbar.setSelectedMinValue(minweight);
        ageSeekbar.setSelectedMinValue(minage);
        ageSeekbar.setSelectedMaxValue(maxage);

        if (countryDto != null){
            countryBtn.setText(countryDto.mName);
        }

        if (stateDto != null)
            stateBtn.setText(stateDto.mName);

        if (gender.equalsIgnoreCase(getResources().getString(R.string.male))){
            maleRadioBtn.setChecked(true);
        }else if (gender.equalsIgnoreCase(getResources().getString(R.string.female))){
            femaleRadioBtn.setChecked(true);
        }else {
            anyRadioBtn.setChecked(true);
        }

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.tab_any:
                        gender = anyRadioBtn.getText().toString();
                        break;

                    case R.id.tab_male:
                        gender = maleRadioBtn.getText().toString();
                        break;

                    case R.id.tab_female:
                        gender = femaleRadioBtn.getText().toString();
                        break;
                }
            }
        });
    }

    @OnClick({R.id.back, R.id.country, R.id.state, R.id.second_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.country:
                gotoCountryActivity();
                break;

            case R.id.state:
                gotoStateActivity();
                break;

            case R.id.second_title:
                apply();
                break;
        }
    }

    private void gotoCountryActivity(){
        Intent countryIntent = new Intent(this, CountrySelectActivity.class);
        countryIntent.putExtra(AppConst.TYPE, AppConst.COUNTRY);
        countryIntent.putExtra(AppConst.FILTER, true);
        countryIntent.putExtra(AppConst.COUNTRY, countryDto == null ? -1 : countryDto.mId);
        startActivityForResult(countryIntent, AppConst.COUNTRY_REQUEST_CODE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void gotoStateActivity(){
        if (countryDto == null) {
            CommonUtils.showAlert(this, getResources().getString(R.string.emptycountry));
            return;
        }

        if (countryDto.mName.equalsIgnoreCase(getResources().getString(R.string.world))){
            CommonUtils.showAlert(this, getResources().getString(R.string.wrongcountry));
            return;
        }

        Intent countryIntent = new Intent(this, CountrySelectActivity.class);
        countryIntent.putExtra(AppConst.TYPE, AppConst.STATE);
        countryIntent.putExtra(AppConst.COUNTRY, countryDto.mId);
        countryIntent.putExtra(AppConst.STATE, stateDto == null ? -1 : stateDto.mId);
        startActivityForResult(countryIntent, AppConst.STATE_REQUEST_CODE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void apply(){
        if (countryDto == null) {
            CommonUtils.showAlert(this, getResources().getString(R.string.emptycountry));
            return;
        }

        maxage = (int)ageSeekbar.getSelectedMaxValue();
        minage = (int)ageSeekbar.getSelectedMinValue();

        maxweight = (int)weightSeekbar.getSelectedMaxValue();
        minweight = (int)weightSeekbar.getSelectedMinValue();

        if (checkFilterisUpdated()){
            Intent intent = new Intent();

            if (countryDto != null)
                intent.putExtra(AppConst.COUNTRY, countryDto);

            if (stateDto != null)
                intent.putExtra(AppConst.STATE, stateDto);

            intent.putExtra(AppConst.MAX_AGE, maxage);
            intent.putExtra(AppConst.MIN_AGE, minage);
            intent.putExtra(AppConst.MAX_WEIGHT, maxweight);
            intent.putExtra(AppConst.MIN_WEIGHT, minweight);
            intent.putExtra(AppConst.GENDER, gender);

            setResult(Activity.RESULT_OK, intent);

        }else {
        }

        finish();
    }

    private boolean checkFilterisUpdated(){
        return countryischagned || stateischanged || (!oldgender.equalsIgnoreCase(gender) || (maxweight != oldmaxweight) ||
                (minweight != oldminweight)  || (oldminage != minage) || oldmaxage != maxage);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == AppConst.COUNTRY_REQUEST_CODE){
                CountryStateCityDto stateCityDto = (CountryStateCityDto)data.getSerializableExtra(AppConst.COUNTRY);

                if (countryDto == null || countryDto.mId != stateCityDto.mId){
                    countryDto = stateCityDto;
                    countryBtn.setText(stateCityDto.mName);

                    stateDto = null;
                    stateBtn.setText(getResources().getString(R.string.state));

                    if (oldstateDto == null){
                        stateischanged = false;
                    }else
                        stateischanged = true;

                    if (oldcountryDto == null || oldcountryDto.mId != countryDto.mId)
                        countryischagned = true;
                }
            }else if (requestCode == AppConst.STATE_REQUEST_CODE){
                CountryStateCityDto stateCityDto = (CountryStateCityDto)data.getSerializableExtra(AppConst.STATE);

                if (stateDto == null || stateDto.mId != stateCityDto.mId){
                    stateDto = stateCityDto;
                    stateBtn.setText(stateCityDto.mName);

                    if (oldstateDto == null || oldstateDto .mId != stateDto.mId){
                        stateischanged = true;
                    }
                }
            }
        }
    }
}
