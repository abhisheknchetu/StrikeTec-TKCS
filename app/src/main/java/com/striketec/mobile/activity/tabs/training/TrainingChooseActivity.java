package com.striketec.mobile.activity.tabs.training;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.tabs.training.comboset.CombosetSettingsActivity;
import com.striketec.mobile.activity.tabs.training.quickstart.QuickstartSettingsActivity;
import com.striketec.mobile.activity.tabs.training.roundtraining.RoundtrainingSettingsActivity;
import com.striketec.mobile.activity.tabs.training.workout.WorkoutPlanActivity;
import com.striketec.mobile.dto.AudioRecordingsDto;
import com.striketec.mobile.response.ResponseArray;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.CommonUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class TrainingChooseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_choose);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        String strUrl = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";

        //Download audio files from server.
        downloadAudiosFromServer(strUrl);
//        getRecordingsList();
    }

    @OnClick({R.id.quickstarttraining, R.id.roundtraining, R.id.combosettraining, R.id.workoutparent, R.id.closebtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quickstarttraining:
                Intent quickstartIntent = new Intent(TrainingChooseActivity.this, QuickstartSettingsActivity.class);
                startActivity(quickstartIntent);
                finish();
                break;
            case R.id.roundtraining:
                Intent roundtrainingIntent = new Intent(TrainingChooseActivity.this, RoundtrainingSettingsActivity.class);
                startActivity(roundtrainingIntent);
                finish();
                break;
            case R.id.combosettraining:
                Intent combosetIntent = new Intent(TrainingChooseActivity.this, CombosetSettingsActivity.class);
                startActivity(combosetIntent);
                finish();
                break;
            case R.id.workoutparent:
                Intent workoutIntent = new Intent(TrainingChooseActivity.this, WorkoutPlanActivity.class);
                startActivity(workoutIntent);
                finish();
                break;
            case R.id.closebtn:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Download audio files from server.
     *
     * @param url audio url.
     */
    private void downloadAudiosFromServer(String url) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Audio downloading...");
            request.setTitle("Attack Audio");

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "sample_video.mp4");

            //get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(TrainingChooseActivity.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * user : rakeshk2
     * date : 11/20/2017
     * description : Get list of Audio Recordings from server.
     **/
    private void getRecordingsList() {

        if (CommonUtils.isOnline()) {

            RetrofitSingleton.RECORDING_REST.getRecordings().enqueue(new IndicatorCallback<ResponseArray<AudioRecordingsDto>>(this, false) {
                @Override
                public void onResponse(Call<ResponseArray<AudioRecordingsDto>> call, Response<ResponseArray<AudioRecordingsDto>> response) {
                    super.onResponse(call, response);
                    if (response.body() != null) {
                        try {
                            final ResponseArray<AudioRecordingsDto> responseBody = response.body();

                            if (responseBody.getError().equalsIgnoreCase(getString(R.string.false_text))) {
                                List<AudioRecordingsDto> subscriptionList = responseBody.getData();
                                for (int audioCounter = 0; audioCounter <= subscriptionList.size(); audioCounter++) {
                                    downloadAudiosFromServer(subscriptionList.get(audioCounter).audio);
                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseArray<AudioRecordingsDto>> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showToastMessage(t.getLocalizedMessage());
                }
            });
        } else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

}
