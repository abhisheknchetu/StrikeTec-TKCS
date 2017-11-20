package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

public class DBTrainingRoundDto {

    @SerializedName("session_start_time")
    public String mSessionStartTime;

    @SerializedName("start_time")
    public String mStartTime;

    @SerializedName("end_time")
    public String mEndTime;

    @SerializedName("avg_speed")
    public double mAvgSpeed;

    @SerializedName("max_speed")
    public double mMaxSpeed;

    @SerializedName("avg_force")
    public double mAvgForce;

    @SerializedName("max_force")
    public double mMaxForce;

    @SerializedName("punches_count")
    public int mPunchesCount;

    @SerializedName("best_time")
    public float mBestTime;

    public DBTrainingRoundDto(String mSessionStartTime, String mStartTime, String mEndTime,
                              double mAvgSpeed, double mMaxSpeed, double mAvgForce, double mMaxForce, int mPunchesCount, float mBestTime){
        super();
        this.mSessionStartTime = mSessionStartTime;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mAvgSpeed = mAvgSpeed;
        this.mMaxSpeed = mMaxSpeed;
        this.mAvgForce = mAvgForce;
        this.mMaxForce = mMaxForce;
        this.mPunchesCount = mPunchesCount;
        this.mBestTime = mBestTime;
    }

    @Override
    public String toString() {
        return "DBTrainingRoundDto [ mSessionStartTime=" + mSessionStartTime
                + ", mStartTime=" + mStartTime + ", mEndTime=" + mEndTime;
    }
}
