package com.striketec.mobile.restapi;

import com.striketec.mobile.dto.AuthResponseDto;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.ServerCountryStateCityResponseDto;
import com.striketec.mobile.dto.ServerLeaderboardResponseDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface DataRest {

    @GET(RestUrl.GET_COUNTRY)
    Call<ServerCountryStateCityResponseDto> getCountries();

    @GET(RestUrl.GET_STATES_OF_COUNTRY)
    Call<ServerCountryStateCityResponseDto> getStates(@Path("country_id") int mCountryId);

    @GET(RestUrl.GET_CITIES_OF_STATE)
    Call<ServerCountryStateCityResponseDto> getCities(@Path("state_id") int mStateId);

    @GET(RestUrl.GET_LEADERBOARD)
    Call<ServerLeaderboardResponseDto> getLeaderboard (@Header("authorization") String token, @QueryMap Map<String, Object> params);

    @GET(RestUrl.GET_EXPLORE)
    Call<ServerLeaderboardResponseDto> getExplore (@Header("authorization") String token, @QueryMap Map<String, Object> params);
}
