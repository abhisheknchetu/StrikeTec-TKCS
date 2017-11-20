package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServerLeaderboardResponseDto extends DefaultResponseDto{
    @SerializedName("data")
    public List<LeaderboardDto> mData;
}
