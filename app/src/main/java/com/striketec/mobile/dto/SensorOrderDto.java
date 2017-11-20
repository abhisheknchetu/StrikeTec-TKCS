package com.striketec.mobile.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class SensorOrderDto implements Serializable{

    public String mDescription;
    public int mResId;

    public SensorOrderDto(String mDescription, int mResId ){
        super();
        this.mDescription = mDescription;
        this.mResId = mResId;
    }

    public SensorOrderDto(){

    }
}
