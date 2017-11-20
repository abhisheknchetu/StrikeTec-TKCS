package com.striketec.mobile.dto;

/**
user : rakeshk2
date : 11/10/2017
description : User subscription model
**/
public class UserSubscriptionModel {
    private int download_left;
    private String expiry_date;
    private boolean is_cancelled;
    private String current_description;
    private String download_description;
    private String purchase_token;

    public String getPurchaseToken() {
        return purchase_token;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchase_token = purchaseToken;
    }

    public String getCurrentDescription() {
        return current_description;
    }

    public void setCurrentDescription(String current_description) {
        this.current_description = current_description;
    }

    public String getDownloadDescription() {
        return download_description;
    }

    public void setDownloadDescription(String download_description) {
        this.download_description = download_description;
    }

    public int getSubscriptionDownloadLeft() {
        return download_left;
    }

    public void setSubscriptionDownloadLeft(int downloadLeft) {
        this.download_left = downloadLeft;
    }

    public String getExpiryDate() {
        return expiry_date;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiry_date = expiryDate;
    }

    public boolean isCancelled() {
        return is_cancelled;
    }

    public void setCancelled(boolean cancelled) {
        is_cancelled = cancelled;
    }
}
