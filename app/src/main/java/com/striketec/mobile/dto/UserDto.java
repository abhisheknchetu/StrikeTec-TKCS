package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Qiang on 8/21/2017.
 */

public class UserDto implements Serializable {

    @SerializedName("id")
    public int mId;

    @SerializedName("facebook_id")
    public long mFacebookId;

    @SerializedName("first_name")
    public String mFirstName;

    @SerializedName("last_name")
    public String mlastName;

    @SerializedName("email")
    public String mEmail;

    @SerializedName("password")
    public String mPassword;

    @SerializedName("photo_url")
    public String mPhoto;

    @SerializedName("show_tip")
    public int mshowTip; //0 , 1

    @SerializedName("gender")
    public String mGender; //male/female

    @SerializedName("birthday")
    public String mBirthday;   //receive format : yyyy-MM-dd, update format : dd-MM-yyyy

    @SerializedName("weight")
    public int mWeight;

    @SerializedName("height")
    public int mHeight;

    @SerializedName("skill_level")
    public String mSkillLevel;

    @SerializedName("country")
    public CountryStateCityDto mCountry;

    @SerializedName("state")
    public CountryStateCityDto mState;

    @SerializedName("city")
    public CountryStateCityDto mCity;

    @SerializedName("left_hand_sensor")
    public String mLeftHandSensor;

    @SerializedName("right_hand_sensor")
    public String mRightHandSensor;

    @SerializedName("left_kick_sensor")
    public String mLeftKickSensor;

    @SerializedName("right_kick_sensor")
    public String mRightKickSensor;

    @SerializedName("is_spectator")
    public int mIsSpectator; //0 or 1

    @SerializedName("stance")
    public String mStance;

    @SerializedName("created_at")
    public String mCreatedAt;

    @SerializedName("updated_at")
    public String mUpdatedAt;

    @SerializedName("followers_count")
    public int mFollowersCount;

    @SerializedName("following_count")
    public int mFollowingCount;

    @SerializedName("preferences")
    public UserPreferenceDto mPreference;
}

