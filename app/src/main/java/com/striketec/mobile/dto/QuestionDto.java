package com.striketec.mobile.dto;

import java.io.Serializable;

public class QuestionDto implements Serializable{
    public String mSubject;
    public String mMessage;

    public QuestionDto(String mSubject, String mMessage){
        this.mSubject = mSubject;
        this.mMessage = mMessage;
    }

    public QuestionDto(){

    }
}
