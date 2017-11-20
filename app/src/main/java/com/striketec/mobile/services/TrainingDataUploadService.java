package com.striketec.mobile.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.dto.DBTrainingRoundDto;
import com.striketec.mobile.dto.DBTrainingSessionDto;
import com.striketec.mobile.dto.DBTrainingPunchDto;
import com.striketec.mobile.dto.UploadDataResponse;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


public class TrainingDataUploadService extends Service {

    private static final String TAG = TrainingDataUploadService.class.getSimpleName();

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    final int UPLOAD_PERIOD = 5 * 60 * 1000;
    int UPLOAD_LIMIT = 200;

    public TrainingDataUploadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        startTimer();

        return Service.START_STICKY;
    }

    private void startTimer(){
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 0, UPLOAD_PERIOD);
    }

    private void initializeTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startUpload();
                        if (AppConst.DEBUG){
                            Log.e(TAG, "start uploading = " + System.currentTimeMillis());
                        }
                    }
                });
            }
        };
    }

    private void stopTimerTask(){
        if (timer != null){
            timerTask.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimerTask();
    }

    private void startUpload(){
        MainActivity mainActivity = MainActivity.getInstance();

        if (mainActivity != null){
            if (mainActivity.isUploading || mainActivity.trainingManager.isTrainingRunning()) {
                return;
            }
        }

        if (CommonUtils.isOnline()){
            startuploadRounds();
        }else {

        }
    }

    private void startuploadSessions(){
        Gson gsonTrainingSession = new GsonBuilder().create();
        ArrayList<DBTrainingSessionDto> dbTrainingSessionDtos = StriketecApp.dbAdapter.getAllNonSynchronizedTrainingSessionRecords(UPLOAD_LIMIT, SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1));

        if (dbTrainingSessionDtos.size() == 0){

        }else {
            HashMap<String, ArrayList<DBTrainingSessionDto>> hashMap = new HashMap<>();
            hashMap.put("data", dbTrainingSessionDtos);
            String jsonsession = gsonTrainingSession.toJson(hashMap);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), jsonsession);

            if (AppConst.DEBUG)
                Log.e(TAG, "session json string = " + jsonsession);

            RetrofitSingleton.TRAINING_DATA_REST.uploadSessions("application/json", SharedUtils.getHeader(), body).enqueue(new IndicatorCallback<UploadDataResponse>(getApplicationContext(), false) {
                @Override
                public void onResponse(Call<UploadDataResponse> call, Response<UploadDataResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        UploadDataResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage + "    " + responseDto.mData.size());

                            //update training session table
                            for (int i = 0; i < responseDto.mData.size(); i++){
                                StriketecApp.dbAdapter.syncedTrainingSession(SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1), responseDto.mData.get(i).mStartTime);
                            }

                            startuploadSessions();

                        }else {
                            Log.e(TAG, "respone = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadDataResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    Log.e(TAG, "failed = " + t.getLocalizedMessage());
                }
            });
        }
    }

    private void startuploadRounds(){
        Gson gsonTrainingRound = new GsonBuilder().create();
        ArrayList<DBTrainingRoundDto> dbTrainingRoundDtos = StriketecApp.dbAdapter.getAllNonSynchronizedTrainingRoundRecords(UPLOAD_LIMIT, SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1));

        if (dbTrainingRoundDtos.size() == 0){
            startuploadPunches();
        }else {
            HashMap<String, ArrayList<DBTrainingRoundDto>> hashMap = new HashMap<>();
            hashMap.put("data", dbTrainingRoundDtos);
            String jsonround = gsonTrainingRound.toJson(hashMap);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), jsonround);

            if (AppConst.DEBUG)
                Log.e(TAG, "round json string = " + jsonround);

            RetrofitSingleton.TRAINING_DATA_REST.uploadRounds( "application/json", SharedUtils.getHeader(), body).enqueue(new IndicatorCallback<UploadDataResponse>(getApplicationContext(), false) {
                @Override
                public void onResponse(Call<UploadDataResponse> call, Response<UploadDataResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        UploadDataResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage + responseDto.mData.size());

                            //update training round table
                            for (int i = 0; i < responseDto.mData.size(); i++){
                                StriketecApp.dbAdapter.syncedTrainingRound(SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1), responseDto.mData.get(i).mStartTime);
                            }

                            startuploadRounds();
                        }else {
                            Log.e(TAG, "respone = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadDataResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    Log.e(TAG, "failed = " + t.getLocalizedMessage());
                }
            });
        }
    }

    private void startuploadPunches(){
        Gson gsonTrainingPunch = new GsonBuilder().create();
        ArrayList<DBTrainingPunchDto> dbTrainingPunches = StriketecApp.dbAdapter.getAllNonSynchronizedTrainingPunchRecords(UPLOAD_LIMIT, SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1));

        if (dbTrainingPunches.size() == 0){
            startuploadSessions();
        }else {
            HashMap<String, ArrayList<DBTrainingPunchDto>> hashMap = new HashMap<>();
            hashMap.put("data", dbTrainingPunches);
            String jsonpunch = gsonTrainingPunch.toJson(hashMap);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), jsonpunch);

            if (AppConst.DEBUG)
                Log.e(TAG, "punch json string = " + jsonpunch);

            RetrofitSingleton.TRAINING_DATA_REST.uploadPunches("application/json",  SharedUtils.getHeader(), body).enqueue(new IndicatorCallback<UploadDataResponse>(getApplicationContext(), false) {
                @Override
                public void onResponse(Call<UploadDataResponse> call, Response<UploadDataResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        UploadDataResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage +  responseDto.mData.size());

                            //update training punch table
                            for (int i = 0; i < responseDto.mData.size(); i++){
                                StriketecApp.dbAdapter.syncedTrainingPunch(SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1), responseDto.mData.get(i).mStartTime);
                            }

                            startuploadPunches();

                        }else {
                            Log.e(TAG, "respone = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadDataResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    Log.e(TAG, "failed = " + t.getLocalizedMessage());
                }
            });
        }
    }
}
