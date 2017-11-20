package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserPreferenceDto implements Serializable{
    @SerializedName("public_profile")
    public int mPublicProfile;

    @SerializedName("show_achivements")
    public int mShowAchievements;

    @SerializedName("show_training_stats")
    public int mShowTrainingStats;

    @SerializedName("show_challenges_history")
    public int mShowChallengesHistory;
}
