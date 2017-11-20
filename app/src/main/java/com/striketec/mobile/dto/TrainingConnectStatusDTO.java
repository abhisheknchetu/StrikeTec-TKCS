package com.striketec.mobile.dto;

import java.io.Serializable;

public class TrainingConnectStatusDTO implements Serializable{
    private boolean updateConnectStatus;
    private boolean resetConnectStatus;
    private String hand;

    public boolean getUpdateConnectStatus(){
        return updateConnectStatus;
    }

    public void setUpdateConnectStatus(boolean updateConnectStatus){
        this.updateConnectStatus = updateConnectStatus;
    }

    public boolean getResetConnectStatus(){
        return resetConnectStatus;
    }

    public void setResetConnectStatus(boolean resetConnectStatus){
        this.resetConnectStatus = resetConnectStatus;
    }

    public String getHand() {
        return hand;
    }

    public void setHand(int power) {
        this.hand = hand;
    }

    public TrainingConnectStatusDTO(boolean updateConnectStatus, boolean resetConnectStatus, String hand) {
        super();
        this.updateConnectStatus = updateConnectStatus;
        this.resetConnectStatus = resetConnectStatus;
        this.hand = hand;
    }

    public TrainingConnectStatusDTO(){

    }
}
