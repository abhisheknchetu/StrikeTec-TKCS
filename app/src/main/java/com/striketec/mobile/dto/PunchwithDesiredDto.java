package com.striketec.mobile.dto;

import java.io.Serializable;

public class PunchwithDesiredDto implements Serializable{
    public TrainingPunchDto mPunch;
    public String mDesiredPunchKey;

    public PunchwithDesiredDto(TrainingPunchDto mPunch, String mDesiredPunchKey){
        super();
        this.mPunch = mPunch;
        this.mDesiredPunchKey = mDesiredPunchKey;
    }

    public PunchwithDesiredDto(){

    }
}
