package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

public class DefaultResponseDto {

    @SerializedName("error")
    public boolean mError;

    @SerializedName("message")
    public String mMessage;

    @SerializedName("token")
    public String mToken;
}
