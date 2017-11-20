package com.striketec.mobile.restapi;

import com.striketec.mobile.dto.SubscriptionDtoNew;
import com.striketec.mobile.response.ResponseArray;
import com.striketec.mobile.response.ResponseObject;
import com.striketec.mobile.response.SubscriptionMainResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * user : rakeshk2
 * date : 10/23/2017
 * description : methods related to subscription api.
 **/
public interface SubscriptionRest {

    @GET(RestUrl.GET_SUBSCRIPTION_PLANS)
    Call<ResponseArray<SubscriptionDtoNew>> getSubscriptions();

    @FormUrlEncoded
    @POST(RestUrl.CREATE_SUBSCRIPTION)
    Call<ResponseObject<SubscriptionMainResponse>> createSubscription(
            @Field("subscription_id") int subscription_id, @Field("user_id") int user_id,
            @Field("order_id") String order_id, @Field("purchase_token") String purchase_token,
            @Field("purchase_time") String purchase_time, @Field("is_auto_renewing") int is_auto_renewing);

    @FormUrlEncoded
    @POST(RestUrl.CANCEL_SUBSCRIPTION)
    Call<ResponseObject<SubscriptionMainResponse>> cancelSubscription(@Field("user_subscription_id") String subscriptionId);

    @GET(RestUrl.GET_SUBSCRIPTION_STATUS)
    Call<ResponseObject<SubscriptionMainResponse>> getSubscriptionStatus(@Query("user_subscription_id") String subscriptionId);
}

