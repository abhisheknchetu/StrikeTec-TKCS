package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoalDto {
    @SerializedName("activity")
    public String mActivity;   //boxing, kickboxing

    @SerializedName("type")
    public String mType;  //punches, workouts

    @SerializedName("start_date")
    public String mStartDate; //yyyy-MM-dd

    @SerializedName("goal")
    public int mGoal;         //mGoal : total number

    @SerializedName("finished")
    public int mFinished;     // finished number
}
