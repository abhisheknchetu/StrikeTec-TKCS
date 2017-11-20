package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

public class AuthResponseDto extends DefaultResponseDto {

    @SerializedName("user")
    public UserDto mUser;
}
