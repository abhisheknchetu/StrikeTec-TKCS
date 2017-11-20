package com.striketec.mobile.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DBTrainingSessionDto implements Serializable{
    @SerializedName("training_type_id")
    public String mTrainingType;

    @SerializedName("plan_id")
    public int mPlanId;

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

    public DBTrainingSessionDto(String trainingType, int planId, String startTime, String endTime,
                                double mAvgSpeed, double mMaxSpeed, double mAvgForce, double mMaxForce, int mPunchesCount, float mBestTime){
        super();
        this.mTrainingType = trainingType;
        this.mPlanId = planId;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mAvgSpeed = mAvgSpeed;
        this.mMaxSpeed = mMaxSpeed;
        this.mAvgForce = mAvgForce;
        this.mMaxForce = mMaxForce;
        this.mPunchesCount = mPunchesCount;
        this.mBestTime = mBestTime;
    }

    public DBTrainingSessionDto(){

    }

    @Override
    public String toString() {
        return "DBTrainingSessionDto [ trainingtype =" + mTrainingType +
                ", planId = " + mPlanId + " , startTime=" + mStartTime + ", endtime = " + mEndTime;
    }
}
