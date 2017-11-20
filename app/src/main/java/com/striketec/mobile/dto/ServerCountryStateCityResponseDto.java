package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServerCountryStateCityResponseDto extends DefaultResponseDto{
    @SerializedName("data")
    public List<CountryStateCityDto> mData;
}
