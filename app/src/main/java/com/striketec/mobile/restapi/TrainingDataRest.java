package com.striketec.mobile.restapi;

import com.striketec.mobile.dto.ServerTrainingPunchesDtoResponse;
import com.striketec.mobile.dto.ServerTrainingRoundsDtoResponse;
import com.striketec.mobile.dto.ServerTrainingSessionDtoResponse;
import com.striketec.mobile.dto.UploadDataResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface TrainingDataRest {

    @POST(RestUrl.UPLOAD_SESSIONS)
    Call<UploadDataResponse> uploadSessions(@Header("Content-Type") String contenttype, @Header("authorization") String token, @Body RequestBody body);

    @POST(RestUrl.UPLOAD_ROUNDS)
    Call<UploadDataResponse> uploadRounds(@Header("Content-Type") String contenttype, @Header("authorization") String token, @Body RequestBody body);

    @POST(RestUrl.UPLOAD_PUNCHES)
    Call<UploadDataResponse> uploadPunches (@Header("Content-Type") String contenttype, @Header("authorization") String token, @Body RequestBody body);

    //retrieve all training sessions in duration (training type id is optional)
    @GET(RestUrl.RETRIEVE_SESSIONS)
    Call<ServerTrainingSessionDtoResponse> getSessions (@Header("authorization") String token, @QueryMap Map<String, Object> queryMap);

    //retrieve all rounds with session id
    @GET(RestUrl.RETRIEVE_ROUNDS_WITH_SESSIONID)
    Call<ServerTrainingRoundsDtoResponse> getRoundswithSessionId (@Header("authorization") String token, @Path("session_id") String sessionId);

    //retrieve all punches with round id
    @GET(RestUrl.RETRIEVE_PUNCHES_WITH_ROUNDID)
    Call<ServerTrainingPunchesDtoResponse> getPunchesWithRoundId (@Header("authorization") String token, @Path("round_id") String roundId);

    //retrieve all rounds with training type
    @GET(RestUrl.RETRIEVE_ROUND_BY_TRAINING_TYPE)
    Call<ServerTrainingRoundsDtoResponse> getRoundswithTrainingType (@Header("authorization") String token, @QueryMap Map<String, Object> queryMap);
}

