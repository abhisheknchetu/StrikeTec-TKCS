package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

public class NotificationDto_Temp {
    public int type; //1:like 2: following, 3:beat battle request
    public String timeDiff;
    public String oppoenentUserName;
    public String content;
    public boolean mUserFollowing;
    public boolean mUserFollower;
}
