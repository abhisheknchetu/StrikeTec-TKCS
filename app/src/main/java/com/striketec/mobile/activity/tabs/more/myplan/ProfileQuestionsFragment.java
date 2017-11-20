package com.striketec.mobile.activity.tabs.more.myplan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.credential.CountrySelectActivity;
import com.striketec.mobile.adapters.CustomSpinnerAdapter;
import com.striketec.mobile.dto.CountryStateCityDto;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.PresetUtil;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

import static android.app.Activity.RESULT_OK;

public class ProfileQuestionsFragment extends Fragment {

    public static final String TAG = ProfileQuestionsFragment.class.getSimpleName();
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

    private String oldgender="", oldbirthday="", oldweight="", oldheight="", oldstance="";
    private int oldcountryid = -1, oldstateid = -1, oldcityid = -1;

    public CountryStateCityDto countryDto = null, stateDto = null, cityDto = null;

    UserDto userDto;

    MyTrainingplanActivity myTrainingplanActivity;

    public static ProfileQuestionsFragment profileQuestionsFragment;
    private static Context mContext;

    public static ProfileQuestionsFragment newInstance(Context context) {
        mContext = context;
        profileQuestionsFragment = new ProfileQuestionsFragment();
        return profileQuestionsFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        myTrainingplanActivity = (MyTrainingplanActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myplan_profile_questions, container, false);

        ButterKnife.bind(this, view);

        userDto = StriketecApp.currentUser;

        initViews();

        return view;
    }

    private void initViews(){
        genderSpinnerAdapter = new CustomSpinnerAdapter(myTrainingplanActivity, R.layout.custom_spinner_text_with_img, PresetUtil.genderList, AppConst.SPINNER_TEXT_ORANGE);
        genderSpinner.setAdapter(genderSpinnerAdapter);

        weightSpinnerAdapter = new CustomSpinnerAdapter(myTrainingplanActivity, R.layout.custom_spinner_digit_with_img, PresetUtil.weightList, AppConst.SPINNER_DIGIT_ORANGE);
        weightSpinner.setAdapter(weightSpinnerAdapter);

        heightSpinnerAdapter = new CustomSpinnerAdapter(myTrainingplanActivity, R.layout.custom_spinner_digit_with_img, PresetUtil.heightList, AppConst.SPINNER_DIGIT_ORANGE);
        heightSpinner.setAdapter(heightSpinnerAdapter);

        stanceSpinnerAdpater = new CustomSpinnerAdapter(myTrainingplanActivity, R.layout.custom_spinner_text_with_img, PresetUtil.stanceList, AppConst.SPINNER_TEXT_ORANGE);
        stanceSpinner.setAdapter(stanceSpinnerAdpater);

        if (!TextUtils.isEmpty(userDto.mStance)){
            stanceSpinner.setSelection(PresetUtil.getStanceposition(userDto.mStance));
            oldstance = userDto.mStance;
        }

        if (!TextUtils.isEmpty(userDto.mGender)){
            genderSpinner.setSelection(PresetUtil.getGenderPosition(userDto.mGender));
            oldgender = userDto.mGender;
        }

        if (userDto.mWeight > 0){
            weightSpinner.setSelection(PresetUtil.getWeightPosition(String.valueOf(userDto.mWeight)));
            oldweight = String.valueOf(userDto.mWeight);
        }

        if (userDto.mHeight > 0){
            heightSpinner.setSelection(PresetUtil.getHeightPosition(String.valueOf(userDto.mHeight)));
            oldheight = String.valueOf(userDto.mHeight);
        }

        if(!TextUtils.isEmpty(userDto.mBirthday)){
            birthdayBtn.setText(userDto.mBirthday);
            oldbirthday = birthdayBtn.getText().toString().trim();
        }

        if (userDto.mCountry != null){
            countryDto = userDto.mCountry;
            countryBtn.setText(countryDto.mName);
            oldcountryid = userDto.mCountry.mId;
        }

        if (userDto.mState != null){
            stateDto = userDto.mState;
            stateBtn.setText(stateDto.mName);
            oldstateid = userDto.mState.mId;
        }

        if (userDto.mCity != null){
            cityDto = userDto.mCity;
            cityBtn.setText(cityDto.mName);
            oldcityid = userDto.mCity.mId;
        }
    }

    @OnClick({R.id.genderbtn, R.id.dateofbirthbtn, R.id.weightbtn, R.id.heightbtn, R.id.stancebtn, R.id.country, R.id.state, R.id.city})
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

    public boolean checkUpdateAble(){
        return !oldgender.toLowerCase().equalsIgnoreCase(gender.toLowerCase()) ||
                !oldbirthday.toLowerCase().equalsIgnoreCase(birthday.toLowerCase()) ||
                !oldweight.toLowerCase().equalsIgnoreCase(weight.toLowerCase()) ||
                !oldheight.toLowerCase().equalsIgnoreCase(height.toLowerCase()) ||
                !oldstance.toLowerCase().equalsIgnoreCase(stance.toLowerCase()) ||
                oldcountryid != countryDto.mId || oldstateid != stateDto.mId || oldcityid != cityDto.mId;
    }

    public void updateParam(HashMap<String, Object> params){
        params.put("gender", gender.toLowerCase());
        params.put("birthday", DatesUtil.changeDateFormat1(birthday));
        params.put("weight", Integer.parseInt(weight));
        params.put("height", Integer.parseInt(height));
        params.put("stance", stance);
        params.put("skill_level", userDto.mSkillLevel);
        params.put("country_id", countryDto.mId);
        params.put("city_id", cityDto.mId);
        params.put("state_id", stateDto.mId);
    }

    /*
        check user's profile values are empty or not
     */
    public boolean checkContinue(){
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
        Intent countryIntent = new Intent(myTrainingplanActivity, CountrySelectActivity.class);
        countryIntent.putExtra(AppConst.TYPE, AppConst.COUNTRY);
        countryIntent.putExtra(AppConst.COUNTRY, countryDto == null ? -1 : countryDto.mId);
        startActivityForResult(countryIntent, AppConst.COUNTRY_REQUEST_CODE);
        myTrainingplanActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void gotoStateActivity(){
        if (countryDto == null) {
            CommonUtils.showAlert(myTrainingplanActivity, getResources().getString(R.string.emptycountry));
            return;
        }

        Intent countryIntent = new Intent(myTrainingplanActivity, CountrySelectActivity.class);
        countryIntent.putExtra(AppConst.TYPE, AppConst.STATE);
        countryIntent.putExtra(AppConst.COUNTRY, countryDto.mId);
        countryIntent.putExtra(AppConst.STATE, stateDto == null ? -1 : stateDto.mId);
        startActivityForResult(countryIntent, AppConst.STATE_REQUEST_CODE);
        myTrainingplanActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void gotoCityActivity(){
        if (stateDto == null) {
            CommonUtils.showAlert(myTrainingplanActivity, getResources().getString(R.string.emptystate));
            return;
        }

        Intent countryIntent = new Intent(myTrainingplanActivity, CountrySelectActivity.class);
        countryIntent.putExtra(AppConst.TYPE, AppConst.CITY);
        countryIntent.putExtra(AppConst.STATE, stateDto.mId);
        countryIntent.putExtra(AppConst.CITY, cityDto == null ? -1 : cityDto.mId);
        startActivityForResult(countryIntent, AppConst.CITY_REQUEST_CODE);
        myTrainingplanActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
            showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog(){
        DatePickerDialog pDialog = null;

        pDialog = new DatePickerDialog(myTrainingplanActivity, datePickerListener, year, month - 1, day);
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

        pDialog.show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
