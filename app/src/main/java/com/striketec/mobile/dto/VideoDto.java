package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class VideoDto implements Serializable{

    @SerializedName("id")
    public int mId;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("file")
    public String mVideoPath;

    @SerializedName("thumbnail")
    public String mThumnail;

    @SerializedName("view_counts")
    public int mViewCounts;

    @SerializedName("author_name")
    public String mAuthorName;

    @SerializedName("duration")
    public String mDuration;

    @SerializedName("user_favourited")
    public boolean mFavorited;
}
