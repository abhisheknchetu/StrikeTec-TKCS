package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LeaderboardDto implements Serializable{

    @SerializedName("id")
    public int mId;

    @SerializedName("user_id")
    public int mUserId;

    @SerializedName("sessions_count")
    public int mSessionCount;

    @SerializedName("avg_force")
    public float mAvgForce;

    @SerializedName("avg_speed")
    public float mAvgSpeed;

    @SerializedName("punches_count")
    public int mPunchCount;

    @SerializedName("max_speed")
    public float mMaxSpeed;

    @SerializedName("max_force")
    public float mMaxForce;

    @SerializedName("avg_time")
    public float mAvgTime;

    @SerializedName("rank")
    public int mRank;

    @SerializedName("user")
    public LeaderboardUserDto mUser;
}
