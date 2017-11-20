package com.striketec.mobile.dto;

import java.io.Serializable;

public class TrainingBatteryVoltageDTO implements Serializable{
    private boolean resetVoltage;
    private String hand;
    private String voltage;

    public boolean getResetVoltage(){
        return resetVoltage;
    }

    public void setResetVoltage(boolean resetVoltage){
        this.resetVoltage = resetVoltage;
    }

    public String getHand() {
        return hand;
    }

    public void setHand(int power) {
        this.hand = hand;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }


    public TrainingBatteryVoltageDTO(boolean resetVoltage, String hand, String voltage) {
        super();
        this.resetVoltage = resetVoltage;
        this.hand = hand;
        this.voltage = voltage;
    }

    public TrainingBatteryVoltageDTO(){

    }
}
