package com.striketec.mobile.restapi;

import com.striketec.mobile.dto.AudioRecordingsDto;
import com.striketec.mobile.response.ResponseArray;
import com.striketec.mobile.response.ResponseObject;
import com.striketec.mobile.response.SubscriptionMainResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * user : rakeshk2
 * date : 11/17/2017
 * description : methods related to Audio recording apis.
 **/
public interface RecordingRest {

    @GET(RestUrl.GET_RECORDING_FILES)
    Call<ResponseArray<AudioRecordingsDto>> getRecordings();

    @FormUrlEncoded
    @POST(RestUrl.UPLOAD_RECORDING_FILES)
    Call<ResponseObject<AudioRecordingsDto>> uploadRecordings(@Field("user_id") String userId);

}

