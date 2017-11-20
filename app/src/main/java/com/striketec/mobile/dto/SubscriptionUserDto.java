package com.striketec.mobile.dto;

public class SubscriptionUserDto {

    public int user_subscription_id;
    public String tutorials;
    public String tournaments;
    public String battles;
    public String purchase_token;
    public String expiry_date;
    public boolean is_cancelled;

    public int getUser_subscription_id() {
        return user_subscription_id;
    }

    public void setUser_subscription_id(int user_subscription_id) {
        this.user_subscription_id = user_subscription_id;
    }

    public String getTutorials() {
        return tutorials;
    }

    public void setTutorials(String tutorials) {
        this.tutorials = tutorials;
    }

    public String getTournaments() {
        return tournaments;
    }

    public void setTournaments(String tournaments) {
        this.tournaments = tournaments;
    }

    public String getBattles() {
        return battles;
    }

    public void setBattles(String battles) {
        this.battles = battles;
    }

    public String getPurchase_token() {
        return purchase_token;
    }

    public void setPurchase_token(String purchase_token) {
        this.purchase_token = purchase_token;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public boolean is_cancelled() {
        return is_cancelled;
    }

    public void setIs_cancelled(boolean is_cancelled) {
        this.is_cancelled = is_cancelled;
    }


}
