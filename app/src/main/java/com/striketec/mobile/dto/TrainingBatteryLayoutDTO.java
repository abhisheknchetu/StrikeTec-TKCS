package com.striketec.mobile.dto;

import java.io.Serializable;

public class TrainingBatteryLayoutDTO implements Serializable{
    private boolean isLeff;
    private boolean isClickable;

    public boolean getIsLeft(){
        return isLeff;
    }

    public void setIsLeft(boolean isLeff){
        this.isLeff = isLeff;
    }

    public boolean getClickage(){
        return isClickable;
    }

    public void setClickable(boolean isClickable){
        this.isClickable = isClickable;
    }

    public TrainingBatteryLayoutDTO(boolean isLeff, boolean isClickable) {
        super();
        this.isLeff = isLeff;
        this.isClickable = isClickable;
    }

    public TrainingBatteryLayoutDTO(){

    }
}
