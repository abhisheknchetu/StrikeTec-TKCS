package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VideoResponseDto extends DefaultResponseDto{

    @SerializedName("videos")
    public List<VideoDto> mVideos;
}
