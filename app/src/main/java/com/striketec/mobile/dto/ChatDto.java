package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatDto implements Serializable{

    public String opponentUserName;
    public String message;
    public String messageTime;
}
