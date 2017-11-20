package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CountryStateCityDto implements Serializable{
    @SerializedName("id")
    public int mId;

    @SerializedName("name")
    public String mName;
}
