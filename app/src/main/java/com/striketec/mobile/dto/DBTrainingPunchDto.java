package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

public class DBTrainingPunchDto {

    @SerializedName("round_start_time")
    public String mRoundStartTime;

    @SerializedName("punch_time")
    public String mPunchTime;

    @SerializedName("punch_type")
    public String mPunchType;

    @SerializedName("hand")
    public String mHand;

    @SerializedName("punch_duration")
    public float mPunchDuration;

    @SerializedName("speed")
    public int mSpeed;

    @SerializedName("force")
    public int mForce;

    public DBTrainingPunchDto(String mRoundStartTime, String mPunchTime, String mPunchType, String mHand,
                              float mPunchDuration , int force, int speed) {
        super();

        this.mRoundStartTime = mRoundStartTime;
        this.mPunchTime = mPunchTime;
        this.mPunchType = mPunchType;
        this.mHand = mHand;
        this.mSpeed = speed;
        this.mForce = force;
        this.mPunchDuration = mPunchDuration;
    }

    public DBTrainingPunchDto(){

    }

    public String toString() {
        return "Punch Info [round time =" + mRoundStartTime + "hand=" + mHand +  ", type=" + mPunchType + ", speed=" + mSpeed + ", force=" + mForce + ", punchtime=" + mPunchTime+ "]";
    }
}
