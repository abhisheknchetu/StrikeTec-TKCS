package com.striketec.mobile.response;

import com.striketec.mobile.dto.SubscriptionDtoNew;

import java.util.List;

/**
 * user : rakeshk2
 * date : 10/24/2017
 * description : Dto to show subscription status.
 **/
public class SubscriptionStatusDto {

    public int id;
    public String user_id;
    public String device_id;
    public String order_id;
    public String purchase_token;
    public String battle_left;
    public String tutorial_left;
    public String tournament_left;
    public String purchase_time;
    public float is_auto_renewing;
    public String subscription_id;
    public String expiry_date;
    public String is_cancelled;
    public String created_at;
    public String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPurchase_token() {
        return purchase_token;
    }

    public void setPurchase_token(String purchase_token) {
        this.purchase_token = purchase_token;
    }

    public String getBattle_left() {
        return battle_left;
    }

    public void setBattle_left(String battle_left) {
        this.battle_left = battle_left;
    }

    public String getTutorial_left() {
        return tutorial_left;
    }

    public void setTutorial_left(String tutorial_left) {
        this.tutorial_left = tutorial_left;
    }

    public String getTournament_left() {
        return tournament_left;
    }

    public void setTournament_left(String tournament_left) {
        this.tournament_left = tournament_left;
    }

    public String getPurchase_time() {
        return purchase_time;
    }

    public void setPurchase_time(String purchase_time) {
        this.purchase_time = purchase_time;
    }

    public float getIs_auto_renewing() {
        return is_auto_renewing;
    }

    public void setIs_auto_renewing(float is_auto_renewing) {
        this.is_auto_renewing = is_auto_renewing;
    }

    public String getSubscription_id() {
        return subscription_id;
    }

    public void setSubscription_id(String subscription_id) {
        this.subscription_id = subscription_id;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getIs_cancelled() {
        return is_cancelled;
    }

    public void setIs_cancelled(String is_cancelled) {
        this.is_cancelled = is_cancelled;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

}
