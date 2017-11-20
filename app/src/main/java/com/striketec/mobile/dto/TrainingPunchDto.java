package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

public class TrainingPunchDto extends IdDto{

    @SerializedName("session_round_id")
    public String mRoundId;

    @SerializedName("punch_time")
    public String mPunchTime;

    @SerializedName("punch_type")
    public String mPunchType;

    @SerializedName("hand")
    public String mHand;

    @SerializedName("punch_duration")
    public float mPunchDuration;

    @SerializedName("speed")
    public double mSpeed;

    @SerializedName("force")
    public double mForce;
}
