package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TrainingSessionDto extends IdDto {
    @SerializedName("user_id")
    public int mUserId;

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

    @SerializedName("avg_force")
    public double mAvgForce;

    @SerializedName("punches_count")
    public int mPunchCount;

    @SerializedName("max_speed")
    public double mMaxSpeed;

    @SerializedName("max_force")
    public double mMaxForce;

    @SerializedName("best_time")
    public float mBestTime;

    @SerializedName("round_ids")
    public List<IdDto> mRoundIds;
}
