package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Qiang on 8/21/2017.
 */

public class LeaderboardUserDto implements Serializable {

    @SerializedName("id")
    public int mId;

    @SerializedName("first_name")
    public String mFirstName;

    @SerializedName("last_name")
    public String mlastName;

    @SerializedName("photo_url")
    public String mPhoto;

    @SerializedName("skill_level")
    public String mSkillLevel;

    @SerializedName("weight")
    public int mWeight;

    @SerializedName("age")
    public int mAge;

    @SerializedName("gender")
    public String mGender; //male/female

    @SerializedName("height")
    public int mHeight;

    @SerializedName("country")
    public CountryStateCityDto mCountry;

    @SerializedName("state")
    public CountryStateCityDto mState;

    @SerializedName("city")
    public CountryStateCityDto mCity;

    @SerializedName("user_following")
    public boolean mUserFollowing;

    @SerializedName("user_follower")
    public boolean mUserFollower;


}

