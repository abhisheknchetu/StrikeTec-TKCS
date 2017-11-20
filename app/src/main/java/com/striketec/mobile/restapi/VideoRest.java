package com.striketec.mobile.restapi;

import com.striketec.mobile.dto.AuthResponseDto;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.ServerTrainingPunchesDtoResponse;
import com.striketec.mobile.dto.ServerTrainingRoundsDtoResponse;
import com.striketec.mobile.dto.ServerTrainingSessionDtoResponse;
import com.striketec.mobile.dto.UploadDataResponse;
import com.striketec.mobile.dto.VideoResponseDto;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface VideoRest {

    @GET(RestUrl.GET_VIDEOS)
    Call<VideoResponseDto> getVideos(@Header("authorization") String token, @QueryMap Map<String, Object> queryMap);

    @GET(RestUrl.SEARCH_VIDEOS)
    Call<VideoResponseDto> searchVideos(@Header("authorization") String token, @QueryMap Map<String, Object> queryMap);

    @GET(RestUrl.GET_FAVORITE_VIDEOS)
    Call<VideoResponseDto> getFavoriteVideos(@Header("authorization") String token, @QueryMap Map<String, Object> queryMap);

    @POST(RestUrl.FAVORITE_VIDEO)
    Call<DefaultResponseDto> favoriteVideo(@Header("authorization") String token, @Path("videoId") int videoId);

    @POST(RestUrl.UNFAVORITE_VIDEO)
    Call<DefaultResponseDto> unfavoriteVideo(@Header("authorization") String token, @Path("videoId") int videoId);

    @POST(RestUrl.INCREASE_VIEWCOUNT)
    Call<DefaultResponseDto> increaseViewCount(@Header("authorization") String token, @Path("videoId") int videoId);

}

