package com.striketec.mobile.restapi;

import com.google.zxing.common.StringUtils;
import com.striketec.mobile.dto.AuthResponseDto;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CredentialRest {
    @FormUrlEncoded
    @POST(RestUrl.SIGN_UP)
    Call<AuthResponseDto> registerUser(@Field("first_name") String fname, @Field("last_name") String lastname, @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST(RestUrl.SIGN_IN)
    Call<AuthResponseDto> loginUser(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST(RestUrl.FB_SIGNUP)
    Call<AuthResponseDto> fbSignup(@Field("facebook_id") String facebookId, @Field("first_name") String fname, @Field("last_name") String lastname, @Field("email") String email);

    @FormUrlEncoded
    @POST(RestUrl.FB_SIGNIN)
    Call<AuthResponseDto> fbLogin(@Field("facebook_id") String facebookId);

}
