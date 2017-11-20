package com.striketec.mobile.restapi;

import com.striketec.mobile.dto.AuthResponseDto;
import com.striketec.mobile.dto.DefaultResponseDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserRest {
    @FormUrlEncoded
    @POST(RestUrl.PASSWORD)
    Call<DefaultResponseDto> resetPasswordRequest (@Field("email") String email);

    @FormUrlEncoded
    @POST(RestUrl.RESET_PASSWORD)
    Call<DefaultResponseDto> resetPassword (@Header("authorization") String token, @Field("password") String password);

    @FormUrlEncoded
    @POST(RestUrl.CHANGE_PASSWORD)
    Call<DefaultResponseDto> changePassword (@Header("authorization") String token, @Field("old_password") String oldpassword, @Field("password") String newpassword);

    @FormUrlEncoded
    @POST(RestUrl.VERIFY_CODE)
    Call<DefaultResponseDto> verifyCode (@Header("authorization") String token, @Field("code") String verifycode);

    @FormUrlEncoded
    @POST(RestUrl.UPDATE_USER)
    Call<DefaultResponseDto> updateUser (@Header("authorization") String token, @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST(RestUrl.UPDATE_USER_PREFERENCES)
    Call<DefaultResponseDto> updateUserPreferences (@Header("authorization") String token, @FieldMap Map<String, Object> params);

    @GET(RestUrl.FOLLOW_USER)
    Call<DefaultResponseDto> followUser (@Header("authorization") String token, @Path("user_id") int mUserId);

    @GET(RestUrl.UNFOLLOW_USER)
    Call<DefaultResponseDto> unfollowUser (@Header("authorization") String token, @Path("user_id") int mUserId);

    @GET(RestUrl.GET_USER)
    Call<AuthResponseDto> getUserInfo (@Header("authorization") String token, @Path("user_id") int mUserId);

    @FormUrlEncoded
    @POST(RestUrl.UPDATE_TOKEN)
    Call<DefaultResponseDto> updateDeviceToken (@Header("authorization") String token, @FieldMap Map<String, Object> params);

}
