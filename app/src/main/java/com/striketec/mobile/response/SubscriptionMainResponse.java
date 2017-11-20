package com.striketec.mobile.response;

import com.striketec.mobile.dto.SubscriptionDtoNew;
import com.striketec.mobile.dto.SubscriptionUserDto;

import java.util.List;

/**
 * user : rakeshk2
 * date : 11/25/2017
 * description : API object response handler.
 **/
public class SubscriptionMainResponse {

    private SubscriptionDtoNew subscription;
    private SubscriptionUserDto user_subscription;

    public SubscriptionDtoNew getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionDtoNew subscription) {
        this.subscription = subscription;
    }

    public SubscriptionUserDto getUser_subscription() {
        return user_subscription;
    }

    public void setUser_subscription(SubscriptionUserDto user_subscription) {
        this.user_subscription = user_subscription;
    }

}
