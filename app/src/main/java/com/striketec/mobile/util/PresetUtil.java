package com.striketec.mobile.util;

import android.content.Context;
import android.util.Log;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.analysis.AnalysisCombinationActivity;
import com.striketec.mobile.dto.CountryStateCityDto;
import com.striketec.mobile.dto.ServerCountryStateCityResponseDto;
import com.striketec.mobile.dto.ServerTrainingPunchesDtoResponse;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Qiang on 8/18/2017.
 */
public class PresetUtil {
    private static int presetNum = 0;
    private static Context mContext;

    public static ArrayList<String> roundsList = new ArrayList<>();
    public static ArrayList<String> timeList = new ArrayList<>(); //with time format
    public static ArrayList<String> timerwitSecsList = new ArrayList<>(); //with sec
    public static ArrayList<String> warningTimewithSecList = new ArrayList<>();
    public static ArrayList<String> warningList = new ArrayList<>(); //as secs;
    public static ArrayList<String> weightList = new ArrayList<>();
    public static ArrayList<String> heightList = new ArrayList<>();
    public static ArrayList<String> gloveList = new ArrayList<>();
    public static ArrayList<String> genderList = new ArrayList<>();
    public static ArrayList<String> reachList = new ArrayList<>();
    public static ArrayList<String> stanceList = new ArrayList<>();
    public static ArrayList<String> skillLeveList = new ArrayList<>();
    public static ArrayList<CountryStateCityDto> countryList = new ArrayList<>();
    public static ArrayList<CountryStateCityDto> worldcountryList = new ArrayList<>();
    public static ArrayList<String> dayList = new ArrayList<>();
    public static ArrayList<String> monthList = new ArrayList<>();
    public static ArrayList<String> threeCharMonthList  = new ArrayList<>();
    public static ArrayList<String> digitMonthList = new ArrayList<>();
    public static ArrayList<String> yearList = new ArrayList<>();
    public static ArrayList<String> statYearList = new ArrayList<>();
    public static ArrayList<String> goalActivityList = new ArrayList<>();
    public static ArrayList<String> goalTypeList = new ArrayList<>();
    public static ArrayList<String> sortList = new ArrayList<>();

    public static void init(Context context){
        mContext = context;

        for (int i = AppConst.GLOVE_MIN; i <= AppConst.GLOVE_MAX ; i = i + AppConst.GLOVE_INTERVAL){
            gloveList.add(String.valueOf(i));
        }

        genderList.add("Male");
        genderList.add("Female");

        stanceList.add(AppConst.TRADITIONAL_TEXT);
        stanceList.add(AppConst.NON_TRADITIONAL_TEXT);

        skillLeveList.add(AppConst.SKILL_LEVEL_ONE);
        skillLeveList.add(AppConst.SKILL_LEVEL_TWO);
        skillLeveList.add(AppConst.SKILL_LEVEL_THREE);

        for (int i = 1; i < 32; i++){
            if (i < 10)
                dayList.add("0" + String.valueOf(i));
            else
                dayList.add(String.valueOf(i));
        }

        for (int i = 1900; i < 2101; i++){
            yearList.add(String.valueOf(i));
        }

        monthList.add("JANUARY");
        monthList.add("FEBRUARY");
        monthList.add("MARCH");
        monthList.add("APRIL");
        monthList.add("MAY");
        monthList.add("JUNE");
        monthList.add("JULY");
        monthList.add("AUGUST");
        monthList.add("SEPTEMBER");
        monthList.add("OCTOBER");
        monthList.add("NOVEMBER");
        monthList.add("DECEMBER");

        threeCharMonthList.add("Jan");
        threeCharMonthList.add("Feb");
        threeCharMonthList.add("Mar");
        threeCharMonthList.add("Apr");
        threeCharMonthList.add("May");
        threeCharMonthList.add("Jun");
        threeCharMonthList.add("Jul");
        threeCharMonthList.add("Aug");
        threeCharMonthList.add("Sep");
        threeCharMonthList.add("Oct");
        threeCharMonthList.add("Nov");
        threeCharMonthList.add("Dec");

        for (int i = 1; i < 13; i++){
            if (i < 10){
                digitMonthList.add("0" + i);
            }else {
                digitMonthList.add(String.valueOf(i));
            }
        }

        for (int i = 2016; i < 2020; i++){
            statYearList.add(String.valueOf(i));
        }

        for (int i = AppConst.WEIGHT_MIN; i <= AppConst.WEIGHT_MAX ; i = i + AppConst.WEIGHT_INTERVAL){
            weightList.add(0, String.valueOf(i));
        }

        for (int i = AppConst.HEIGHT_MIN; i <= AppConst.HEIGHT_MAX ; i = i + AppConst.HEIGHT_INTERVAL){
            heightList.add(0, String.valueOf(i));
        }

        for (int i = AppConst.REACH_MIN; i <= AppConst.REACH_MAX ; i = i + AppConst.REACH_INTERVAL){
            reachList.add(0, String.valueOf(i));
        }

        for (int i = AppConst.ROUNDS_MIN; i <= AppConst.ROUNDS_MAX ; i = i + AppConst.ROUNDS_INTERVAL){
            roundsList.add(0, String.valueOf(i));
        }

        for (int i = AppConst.PRESET_MIN_TIME; i <= AppConst.PRESET_MAX_TIME ; i = i + AppConst.PRESET_INTERVAL_TIME){
            timerwitSecsList.add(0, String.valueOf(i));
            timeList.add(0, changeSecondsToMinutes(i));
        }

        for (int i = AppConst.WARNING_MIN_TIME; i <= AppConst.WARNING_MAX_TIME; i = i + AppConst.WARNING_INTERVAL_TIME){
            warningTimewithSecList.add(0, changeSeconsToMinutesforWarning(i));
            warningList.add(0, String.valueOf(i));
        }

        goalActivityList.add(mContext.getResources().getString(R.string.activity_boxing));
        goalActivityList.add(mContext.getResources().getString(R.string.activity_kickboxing));

        goalTypeList.add(mContext.getResources().getString(R.string.goal_type_1));
        goalTypeList.add(mContext.getResources().getString(R.string.goal_type_2));

        sortList.add(mContext.getResources().getString(R.string.sort_tournamentrank));
        sortList.add(mContext.getResources().getString(R.string.sort_name));
        sortList.add(mContext.getResources().getString(R.string.sort_speed));
        sortList.add(mContext.getResources().getString(R.string.sort_power));
        sortList.add(mContext.getResources().getString(R.string.sort_punchcount));
        sortList.add(mContext.getResources().getString(R.string.sort_numberchallenges));
        sortList.add(mContext.getResources().getString(R.string.sort_hourstrained));
    }

    public static void getCountries(){
        if (CommonUtils.isOnline()){

            RetrofitSingleton.DATA_REST.getCountries().enqueue(new IndicatorCallback<ServerCountryStateCityResponseDto>(mContext) {
                @Override
                public void onResponse(Call<ServerCountryStateCityResponseDto> call, Response<ServerCountryStateCityResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        ServerCountryStateCityResponseDto responseDto = response.body();

                        if (!responseDto.mError){
                            countryList.clear();
                            countryList.addAll(responseDto.mData);

                            worldcountryList.clear();
                            worldcountryList.addAll(responseDto.mData);

                            CountryStateCityDto world = new CountryStateCityDto();
                            world.mName = mContext.getResources().getString(R.string.world);
                            world.mId = 0;
                            worldcountryList.add(0, world);


                        }else {
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerCountryStateCityResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                }
            });
        }else {
        }
    }

    public static String changeSeconsToMinutesforWarning (int secs){
        int min = secs / 60;
        int sec = secs % 60;

        if (sec == 0)
            return min + ":00";
        else if (sec == 5){
            return min + ":05";
        }else
            return min + ":" + sec;
    }

    //change secs to *:** format time
    public static String changeSecondsToMinutes(int secs){
        int min = secs / 60;
        int sec = secs % 60;

        return  min  + ":" + String.format("%02d", sec);
//        if (sec == 0)
//            return min + ":00";
//        else
//            return min + ":" + sec;
    }

    //change secs to **:** format time
    public static String changeSecondsToMinutesWithTwoDigit(int secs){
        int min = secs / 60;
        int sec = secs % 60;

        return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }

    public static String changeSecondsToHours (int secs){
        int hour = secs / 3600;
        int min = (secs % 3600) / 60;
        int sec = secs % 60;

        return String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
    }

    public static String chagngeSecsToTime(int secs){
        String result = "";

        int hour = secs / 3600;

        int min = secs / 60;
        int sec = secs % 60;

        if (hour == 0) {
            if (sec < 10)
                result = min + ":0" + sec;
            else
                result = min + ":" + sec;
        }else {
            if (sec < 10)
                sec = 0 + sec;

            if (min < 10)
                min = 0+min;

            result = hour + ":" + min + ":" + sec;
        }

        return result;
    }

    public static String generatePresetName(){

        return "PRESET " + String.valueOf(presetNum +1);
    }

    public static int getGlovePosition(String gloveType){
        for (int i = 0; i < gloveList.size(); i++){
            if (gloveType.equalsIgnoreCase(gloveList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getReachPosition(String reachvalue){
        for (int i = 0; i < reachList.size(); i++){
            if (reachvalue.equalsIgnoreCase(reachList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getWeightPosition(String weight){
        for (int i = 0; i < weightList.size(); i++){
            if (weight.equalsIgnoreCase(weightList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getHeightPosition(String height){
        for (int i = 0; i < heightList.size(); i++){
            if (height.equalsIgnoreCase(heightList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getGenderPosition(String gender){
        for (int i = 0; i < genderList.size(); i++){
            if (gender.equalsIgnoreCase(genderList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getStanceposition(String stance){
        for (int i = 0; i < stanceList.size(); i++){
            if (stance.equalsIgnoreCase(stanceList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getSkilllevelPosition(String skilllevel){
        for (int i = 0; i < skillLeveList.size(); i++){
            if (skilllevel.equalsIgnoreCase(skillLeveList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getCountryPosition(int countryId){
        for (int i = 0; i < countryList.size(); i++){
            if (countryId == countryList.get(i).mId)
                return i;
        }

        return 0;
    }

    public static int getDayPosition(String day){
        for (int i = 0; i < dayList.size(); i++){
            if (day.equalsIgnoreCase(dayList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getMonthPosition(String month){
        for (int i = 0; i < threeCharMonthList.size(); i++){
            if (month.equalsIgnoreCase(threeCharMonthList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getYearPosition(String year){
        for (int i = 0; i < yearList.size(); i++){
            if (year.equalsIgnoreCase(yearList.get(i)))
                return i;
        }

        return 0;
    }

    public static int getStatYearPosition(String statyear){
        for (int i = 0; i < statYearList.size(); i++){
            if (statyear.equalsIgnoreCase(statYearList.get(i)))
                return i;
        }

        return 0;
    }
}
