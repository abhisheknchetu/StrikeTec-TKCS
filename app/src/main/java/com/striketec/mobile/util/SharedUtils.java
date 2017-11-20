package com.striketec.mobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.striketec.mobile.dto.ComboDto;
import com.striketec.mobile.dto.PresetDto;
import com.striketec.mobile.dto.SetsDto;
import com.striketec.mobile.dto.VideoDto;
import com.striketec.mobile.dto.WorkoutDto;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Qiang on 8/7/2017.
 */

public class SharedUtils {

    private static Context mContext;
    private static SharedPreferences preferences;

    public static final String header_prefix = "Bearer";

    public static final String PRESET = "preset";
    public static final String VIDEOS = "videos";
    public static final String COMBO = "combo";
    public static final String SET = "set";
    public static final String WORKOUT = "workout";

    public static final String REMEMBER_ME = "rememberme";
    public static String LOGGEDINTYPE = "loggedintype";

    public static String AUTH = "authorization";
    public static String EMAIl = "email";
    public static String PWD = "password";
    public static String FBID = "facebookid";
    public static String USER_SERVER_ID = "userid";
    public static String FACEBOOK = "facebook";
    public static String TOKEN = "token";

    public static String LOGGEDIN = "loggedin";

    public static void init(Context context){
        mContext = context;
        preferences = mContext.getSharedPreferences(AppConst.PREFERENCE, Context.MODE_PRIVATE);
    }

    public static String getHeader(){
        return header_prefix + " " + getStringvalue(AUTH, "");
    }

    public static void saveBoolenValue (String key, boolean value){
        preferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolenvalue (String key, boolean defaultValue){
        return preferences.getBoolean(key, defaultValue);
    }

    public static void saveStringValue (String key, String value){
        preferences.edit().putString(key, value).apply();
    }

    public static String getStringvalue (String key, String defaultValue){
        return preferences.getString(key, defaultValue);
    }

    public static void saveIntValue (String key, int value){
        preferences.edit().putInt(key, value).apply();
    }

    public static int getIntvalue (String key, int defaultValue){
        return preferences.getInt(key, defaultValue);
    }

    public static void saveVideoLists(ArrayList<VideoDto> arrayList){
        Gson gson = new Gson();
        final String jsonString = gson.toJson(arrayList);

        preferences.edit().putString(VIDEOS, jsonString).apply();
    }

    public static ArrayList<VideoDto> getVideoLists (){
        Gson gson = new Gson();
        ArrayList<VideoDto> results;
        Type type = new TypeToken<ArrayList<VideoDto>>(){}.getType();
        String jsonString = preferences.getString(VIDEOS, "");
        if (TextUtils.isEmpty(jsonString))
            results = new ArrayList<>();
        else
            results = gson.fromJson(jsonString, type);

        return results;
    }


    public static void savePresetLists(ArrayList<PresetDto> arrayList){
        Gson gson = new Gson();
        final String jsonString = gson.toJson(arrayList);

        preferences.edit().putString(PRESET, jsonString).apply();
    }

    public static ArrayList<PresetDto> getPresetList (){
        Gson gson = new Gson();
        ArrayList<PresetDto> results;
        Type type = new TypeToken<ArrayList<PresetDto>>(){}.getType();
        String jsonString = preferences.getString(PRESET, "");
        if (TextUtils.isEmpty(jsonString))
            results = new ArrayList<>();
        else
            results = gson.fromJson(jsonString, type);
        return results;
    }

    public static void saveCombinationList (ArrayList<ComboDto> arrayList){
        Gson gson = new Gson();
        final String jsonString = gson.toJson(arrayList);

        preferences.edit().putString(COMBO, jsonString).apply();
    }

    public static ArrayList<ComboDto> getSavedCombinationList (){
        Gson gson = new Gson();

        ArrayList<ComboDto> results;

        Type type = new TypeToken<ArrayList<ComboDto>>(){}.getType();
        String jsonString = preferences.getString(COMBO, "");
        if (TextUtils.isEmpty(jsonString))
            results = new ArrayList<>();
        else
            results = gson.fromJson(jsonString, type);

        if (results.size() == 0){
            results = ComboSetUtil.defaultCombos;
            saveCombinationList(results);
        }

        return results;
    }

    public static void saveSetList (ArrayList<SetsDto> arrayList){
        Gson gson = new Gson();
        final String jsonString = gson.toJson(arrayList);

        preferences.edit().putString(SET, jsonString).apply();
    }

    public static ArrayList<SetsDto> getSavedSetList (){

        Gson gson = new Gson();

        ArrayList<SetsDto> results;
        Type type = new TypeToken<ArrayList<SetsDto>>(){}.getType();
        String jsonString = preferences.getString(SET, "");
        if (TextUtils.isEmpty(jsonString))
            results = new ArrayList<>();
        else
            results = gson.fromJson(jsonString, type);

        if (results.size() == 0){
            results = ComboSetUtil.defaultSets;
            saveSetList(results);
        }

        return results;
    }

    public static void saveWorkoutList (ArrayList<WorkoutDto> arrayList){
        Gson gson = new Gson();
        final String jsonString = gson.toJson(arrayList);

        preferences.edit().putString(WORKOUT, jsonString).apply();
    }

    public static ArrayList<WorkoutDto> getSavedWorkouts (){
        Gson gson = new Gson();

        ArrayList<WorkoutDto> results;
        Type type = new TypeToken<ArrayList<WorkoutDto>>(){}.getType();
        String jsonString = preferences.getString(WORKOUT, "");
        if (TextUtils.isEmpty(jsonString))
            results = new ArrayList<>();
        else
            results = gson.fromJson(jsonString, type);

        if (results.size() == 0){
            results = ComboSetUtil.defaultWorkouts;
            saveWorkoutList(results);
        }

        return results;
    }
}
