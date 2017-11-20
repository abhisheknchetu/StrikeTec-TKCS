package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrainingRoundDto extends IdDto {
    @SerializedName("training_session_id")
    public int mStrainingSessionId;

    @SerializedName("start_time")
    public String mStartTime;

    @SerializedName("end_time")
    public String mEndTime;

    @SerializedName("avg_speed")
    public double mAvgSpeed;

    @SerializedName("avg_force")
    public double mAvgForce;

    @SerializedName("punches_count")
    public int mPunchCount;

    @SerializedName("max_speed")
    public double mMaxSpeed;

    @SerializedName("max_force")
    public double mMaxForce;

    @SerializedName("best_time")
    public double mBestTime;
}
