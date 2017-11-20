package com.striketec.mobile.activity.credential;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.facebook.common.Common;
import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.CustomSpinnerAdapter;
import com.striketec.mobile.dto.AuthResponseDto;
import com.striketec.mobile.dto.CountryStateCityDto;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.PresetUtil;

import org.apache.commons.collections.map.HashedMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Response;

public class SignupProfileActivity extends BaseActivity {

    public static final String TAG = SignupProfileActivity.class.getSimpleName();
    final int DATE_DIALOG_ID = 999;

    @BindView(R.id.genderbtn) Button genderBtn;
    @BindView(R.id.genderspinner) Spinner genderSpinner;
    @BindView(R.id.dateofbirthbtn) Button birthdayBtn;
    @BindView(R.id.weightbtn) Button weightBtn;
    @BindView(R.id.weightspinner) Spinner weightSpinner;
    @BindView(R.id.heightbtn) Button heightBtn;
    @BindView(R.id.heightspinner) Spinner heightSpinner;
    @BindView(R.id.stancebtn) Button stanceBtn;
    @BindView(R.id.stancespinner) Spinner stanceSpinner;
    @BindView(R.id.country) Button countryBtn;
    @BindView(R.id.state) Button stateBtn;
    @BindView(R.id.city) Button cityBtn;

    private CustomSpinnerAdapter genderSpinnerAdapter, weightSpinnerAdapter, heightSpinnerAdapter, stanceSpinnerAdpater;

    private boolean genderFirstTime = true, weightFirsttime = true, heightFirsttime = true, stanceFirsttime = true;
    private int month, year, day;
    private String gender, birthday, weight, height, stance;

    private CountryStateCityDto countryDto = null, stateDto = null, cityDto = null;

    UserDto userDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_profile);

        userDto = (UserDto)getIntent().getSerializableExtra(AppConst.USER_INTENT);

        if (userDto == null)
            userDto = new UserDto();

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        genderSpinnerAdapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_text_with_img, PresetUtil.genderList, AppConst.SPINNER_TEXT_ORANGE);
        genderSpinner.setAdapter(genderSpinnerAdapter);

        weightSpinnerAdapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_digit_with_img, PresetUtil.weightList, AppConst.SPINNER_DIGIT_ORANGE);
        weightSpinner.setAdapter(weightSpinnerAdapter);

        heightSpinnerAdapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_digit_with_img, PresetUtil.heightList, AppConst.SPINNER_DIGIT_ORANGE);
        heightSpinner.setAdapter(heightSpinnerAdapter);

        stanceSpinnerAdpater = new CustomSpinnerAdapter(this, R.layout.custom_spinner_text_with_img, PresetUtil.stanceList, AppConst.SPINNER_TEXT_ORANGE);
        stanceSpinner.setAdapter(stanceSpinnerAdpater);

        if (!TextUtils.isEmpty(userDto.mStance)){
            stanceSpinner.setSelection(PresetUtil.getStanceposition(userDto.mStance));
        }

        if (!TextUtils.isEmpty(userDto.mGender)){
            genderSpinner.setSelection(PresetUtil.getGenderPosition(userDto.mGender));
        }

        if (userDto.mWeight > 0){
            weightSpinner.setSelection(PresetUtil.getWeightPosition(String.valueOf(userDto.mWeight)));
        }

        if (userDto.mHeight > 0){
            heightSpinner.setSelection(PresetUtil.getHeightPosition(String.valueOf(userDto.mHeight)));
        }

        if(!TextUtils.isEmpty(userDto.mBirthday)){
            birthdayBtn.setText(userDto.mBirthday);
        }

        if (userDto.mCountry != null){
            countryDto = userDto.mCountry;
            countryBtn.setText(countryDto.mName);
        }

        if (userDto.mState != null){
            stateDto = userDto.mState;
            stateBtn.setText(stateDto.mName);
        }

        if (userDto.mCity != null){
            cityDto = userDto.mCity;
            cityBtn.setText(cityDto.mName);
        }
    }

    @OnClick({R.id.genderbtn, R.id.dateofbirthbtn, R.id.weightbtn, R.id.heightbtn, R.id.stancebtn, R.id.next, R.id.country, R.id.state, R.id.city})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.genderbtn:
                genderSpinner.performClick();
                break;

            case R.id.dateofbirthbtn:
                selectBirthdate();
                break;

            case R.id.weightbtn:
                weightSpinner.performClick();
                break;

            case R.id.heightbtn:
                heightSpinner.performClick();
                break;

            case R.id.stancebtn:
                stanceSpinner.performClick();
                break;

            case R.id.next:
                gotoNext();
                break;

            case R.id.country:
                gotoCountryActivity();
                break;

            case R.id.state:
                gotoStateActivity();
                break;

            case R.id.city:
                gotoCityActivity();
                break;
        }
    }

    @OnItemSelected({R.id.genderspinner, R.id.weightspinner, R.id.heightspinner, R.id.stancespinner})
    public void onItemSelected(Spinner spinner, int position){

        switch (spinner.getId()){
            case R.id.genderspinner:
                if (TextUtils.isEmpty(userDto.mGender)) {
                    if (!genderFirstTime) {
                        genderBtn.setText(PresetUtil.genderList.get(position));
                    } else {
                        genderFirstTime = false;
                    }
                }else {
                    genderFirstTime = false;
                    genderBtn.setText(PresetUtil.genderList.get(position));
                }

                break;

            case R.id.weightspinner:
                if (userDto.mWeight == 0) {
                    if (!weightFirsttime) {
                        weightBtn.setText(PresetUtil.weightList.get(position));
                    } else {
                        weightFirsttime = false;
                    }
                }else {
                    weightFirsttime = false;
                    weightBtn.setText(PresetUtil.weightList.get(position));
                }

                break;

            case R.id.heightspinner:
                if (userDto.mHeight == 0) {
                    if (!heightFirsttime) {
                        heightBtn.setText(PresetUtil.heightList.get(position));
                    } else {
                        heightFirsttime = false;
                    }
                }else {
                    heightFirsttime = false;
                    heightBtn.setText(PresetUtil.heightList.get(position));
                }

                break;

            case R.id.stancespinner:
                if (TextUtils.isEmpty(userDto.mStance)) {
                    if (!stanceFirsttime) {
                        stanceBtn.setText(PresetUtil.stanceList.get(position));
                    } else {
                        stanceFirsttime = false;
                    }
                }else {
                    stanceFirsttime = false;
                    stanceBtn.setText(PresetUtil.stanceList.get(position));
                }

                break;
        }
    }

    private void gotoNext(){
        if (checkContinue()){
            gotoTrainingPlanActivity();
        }
    }

    private void gotoTrainingPlanActivity(){
        userDto.mGender = gender;
        userDto.mStance = stance;
        userDto.mBirthday = birthday;
        userDto.mWeight = Integer.parseInt(weight);
        userDto.mHeight = Integer.parseInt(height);
        userDto.mCountry = countryDto;
        userDto.mState = stateDto;
        userDto.mCity = cityDto;

        Intent planIntent = new Intent(this, SignupTrainingPlanActivity.class);
        planIntent.putExtra(AppConst.USER_INTENT, userDto);
        startActivity(planIntent);

        finish();
    }

    /*
        check user's profile values are empty or not
     */
    private boolean checkContinue(){
        gender = genderBtn.getText().toString().trim();
        birthday = birthdayBtn.getText().toString().trim();
        weight = weightBtn.getText().toString().trim();
        height = heightBtn.getText().toString().trim();
        stance = stanceBtn.getText().toString().trim();

        if (gender.equalsIgnoreCase(getResources().getString(R.string.gender))){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_gender_invalid));
            return false;
        }

        if (birthday.equalsIgnoreCase(getResources().getString(R.string.dateofbirth))){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_birthday_invalid));
            return false;
        }

        if (weight.equalsIgnoreCase(getResources().getString(R.string.weight))){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_weight_invalid));
            return false;
        }

        if (height.equalsIgnoreCase(getResources().getString(R.string.height))){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_height_invalid));
            return false;
        }

        if (stance.equalsIgnoreCase(getResources().getString(R.string.stance))){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_stance_invalid));
            return false;
        }

        if (countryDto == null) {
            CommonUtils.showToastMessage(getResources().getString(R.string.emptycountry));
            return false;
        }

        if (stateDto == null){
            CommonUtils.showToastMessage(getResources().getString(R.string.emptystate));
            return false;
        }

        if (cityDto == null){
            CommonUtils.showToastMessage(getResources().getString(R.string.emptycity));
            return false;
        }

        return true;
    }

    private void gotoCountryActivity(){
        Intent countryIntent = new Intent(this, CountrySelectActivity.class);
        countryIntent.putExtra(AppConst.TYPE, AppConst.COUNTRY);
        countryIntent.putExtra(AppConst.COUNTRY, countryDto == null ? -1 : countryDto.mId);
        startActivityForResult(countryIntent, AppConst.COUNTRY_REQUEST_CODE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void gotoStateActivity(){
        if (countryDto == null) {
            CommonUtils.showAlert(this, getResources().getString(R.string.emptycountry));
            return;
        }

        Intent countryIntent = new Intent(this, CountrySelectActivity.class);
        countryIntent.putExtra(AppConst.TYPE, AppConst.STATE);
        countryIntent.putExtra(AppConst.COUNTRY, countryDto.mId);
        countryIntent.putExtra(AppConst.STATE, stateDto == null ? -1 : stateDto.mId);
        startActivityForResult(countryIntent, AppConst.STATE_REQUEST_CODE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void gotoCityActivity(){
        if (stateDto == null) {
            CommonUtils.showAlert(this, getResources().getString(R.string.emptystate));
            return;
        }

        Intent countryIntent = new Intent(this, CountrySelectActivity.class);
        countryIntent.putExtra(AppConst.TYPE, AppConst.CITY);
        countryIntent.putExtra(AppConst.STATE, stateDto.mId);
        countryIntent.putExtra(AppConst.CITY, cityDto == null ? -1 : cityDto.mId);
        startActivityForResult(countryIntent, AppConst.CITY_REQUEST_CODE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /*
    choose birthday
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public void selectBirthdate() {
        try {
            birthday = birthdayBtn.getText().toString();

            if (AppConst.DEBUG)
                Log.e(TAG, "user birthday = " + birthday);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date;

            if (birthday.equalsIgnoreCase(getResources().getString(R.string.dateofbirth)))
                birthday = "";

            if (TextUtils.isEmpty(birthday)) {
                date = new Date();
            } else {
                date = formatter.parse(birthday);
            }

            year = Integer.parseInt((String) android.text.format.DateFormat.format("yyyy", date));
            month = Integer.parseInt((String) android.text.format.DateFormat.format("MM", date));
            day = Integer.parseInt((String) android.text.format.DateFormat.format("dd", date));
            showDialog(DATE_DIALOG_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected Dialog onCreateDialog(int id) {
        DatePickerDialog pDialog = null;

        switch (id) {
            case DATE_DIALOG_ID:
                pDialog = new DatePickerDialog(this, datePickerListener, year, month - 1, day);
                pDialog.setCancelable(false);
                if (android.os.Build.VERSION.SDK_INT >= 11)
                    pDialog.getDatePicker().setMaxDate(new Date().getTime());
                pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((DatePickerDialog) dialog).updateDate(year, month - 1, day);
                        dialog.dismiss();
                    }
                });
                return pDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @SuppressLint("SimpleDateFormat")
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            GregorianCalendar gregorianCalendar = new GregorianCalendar(year, (month), day);
            Date selectedDate = gregorianCalendar.getTime();
            String selectedDateStr = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
            birthdayBtn.setText(selectedDateStr);
        }
    };

    private void gotoSigninActivity(){
        Intent signinIntent = new Intent(this, SigninActivity.class);
        signinIntent.putExtra(AppConst.USER_INTENT, userDto);
        startActivity(signinIntent);
        finish();
    }


    @Override
    public void onBackPressed() {
        gotoSigninActivity();
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

                    cityDto = null;
                    cityBtn.setText(getResources().getString(R.string.city));
                }
            }else if (requestCode == AppConst.STATE_REQUEST_CODE){
                CountryStateCityDto stateCityDto = (CountryStateCityDto)data.getSerializableExtra(AppConst.STATE);

                if (stateDto == null || stateDto.mId != stateCityDto.mId){
                    stateDto = stateCityDto;
                    stateBtn.setText(stateCityDto.mName);

                    cityDto = null;
                    cityBtn.setText(getResources().getString(R.string.city));
                }
            }else if (requestCode == AppConst.CITY_REQUEST_CODE){
                CountryStateCityDto stateCityDto = (CountryStateCityDto)data.getSerializableExtra(AppConst.CITY);

                if (cityDto == null || cityDto.mId != stateCityDto.mId){
                    cityDto = stateCityDto;
                    cityBtn.setText(stateCityDto.mName);
                }
            }
        }
    }
}
