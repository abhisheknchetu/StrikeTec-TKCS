package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

public class SyncResponseDto extends DefaultResponseDto {

    @SerializedName("id")
    public int id;

    @SerializedName("serverTime")
    public String serverTime;
}
