package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UploadDataResponse extends DefaultResponseDto{
    @SerializedName("data")
    public List<StartTimeDto> mData;
}
