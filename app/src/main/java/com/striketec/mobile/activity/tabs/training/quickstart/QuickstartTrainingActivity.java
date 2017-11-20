package com.striketec.mobile.activity.tabs.training.quickstart;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseTrainingActivity;
import com.striketec.mobile.activity.analysis.AnalysisRoundBreakdownActivity;
import com.striketec.mobile.activity.analysis.AnalysisRoundsActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.customview.CurveChartView;
import com.striketec.mobile.dto.PresetDto;
import com.striketec.mobile.dto.PunchDto;
import com.striketec.mobile.dto.DBTrainingPunchDto;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.interfaces.UploadFinishCallback;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.PresetUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuickstartTrainingActivity extends BaseTrainingActivity implements UploadFinishCallback{

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.audiobtn) ImageView audioBtn;
    @BindView(R.id.starttraining) Button startTrainingBtn;

    //sensor connection status
    @BindView(R.id.leftconnected) ImageView leftConnectionStatusBtn;
    @BindView(R.id.rightconnected) ImageView rightConnectionStatusBtn;
    @BindView(R.id.left_sensor_connection_layout) RelativeLayout leftSensorConnectionLayout;
    @BindView(R.id.right_sensor_connection_layout) RelativeLayout rightSensorConnectionLayout;

    //speed, force, count value of current punch
    @BindView(R.id.speed_value) TextView speedView;
    @BindView(R.id.punch_value) TextView punchcountView;
    @BindView(R.id.power_value) TextView forceView;

    //avg datas
    @BindView(R.id.lh_avg_speed) TextView lefthandAvgSpeedView;
    @BindView(R.id.lh_mph) TextView lefthandSpeedUnitView;
    @BindView(R.id.rh_avg_speed) TextView righthandAvgSpeedView;
    @BindView(R.id.rh_mph) TextView righthandSpeedUnitView;
    @BindView(R.id.lh_avg_force) TextView lefthandAvgForceView;
    @BindView(R.id.lh_lbs) TextView lefthandForceUnitView;
    @BindView(R.id.rh_avg_force) TextView righthandAvgForceView;
    @BindView(R.id.rh_lbs) TextView righthandForceUnitView;
    @BindView(R.id.lk_avg_speed) TextView leftkickAvgSpeedView;
    @BindView(R.id.lk_mph) TextView leftkickSpeedUnitView;
    @BindView(R.id.rk_avg_speed) TextView rightkickAvgSpeedView;
    @BindView(R.id.rk_mph) TextView rightkickSpeedUnitView;
    @BindView(R.id.lk_avg_force) TextView leftkickAvgForceView;
    @BindView(R.id.lk_lbs) TextView leftkickForceUnitView;
    @BindView(R.id.rk_avg_force) TextView rightkickAvgForceView;
    @BindView(R.id.rk_lbs) TextView rightkickForceUnitView;
    @BindView(R.id.punchtypeimage) ImageView punchtypeImageView;

    //punch type/status
    @BindView(R.id.training_punchtype) TextView punchtypeView;
    @BindView(R.id.trainingprogress_status) TextView trainingProgressStatus;

    @BindView(R.id.progress_digit) RelativeLayout digitLayout;
    @BindView(R.id.curvechart) CurveChartView chartView;
    @BindView(R.id.trainingprogress_time) TextView currentTimeView;
    @BindView(R.id.trainingprogressbar) ProgressBar progressBar;

    @BindView(R.id.left_sensor_battery) TextView leftHandBattery;
    @BindView(R.id.right_sensor_battery) TextView rightHandBattery;
    @BindView(R.id.battery_life_left) View leftHandBatteryView;
    @BindView(R.id.battery_life_right) View rightHandBatteryView;

    private MainActivity mainActivityInstance;

    boolean audioEnabled = true;
    private MediaPlayer bellplayer;

    private float maxSpeed = 0f, avgSpeed = 0, maxForce = 0, avgForce = 0;
    private float leftavgSpeed = 0, leftavgForce = 0;
    private float  rightavgSpeed = 0,  rightavgForce = 0;

    private ArrayList<DBTrainingPunchDto> punchLists = new ArrayList<>();
    private ArrayList<PunchDto> punchDTOs = new ArrayList<>();
    private ArrayList<PunchDto> leftpunchDTOS = new ArrayList<>();
    private ArrayList<PunchDto> rightpunchDTOS = new ArrayList<>();

    PresetDto presetDTO;
    Timer progressTimer, noroundTimer;
    TimerTask updateProgressTimerTask, updatenoroundTimerTask;

    private Handler mHandler;

    private int currentStatus = -1;   //0: prepare, 1: round, 2: resting
    private int roundvalue = 0;
    private int totalTime = 0;
    private int currentTime = 0;

    private boolean isSpeedGraph = false;
    private long trainingStartTime = 0L;
    private boolean isRoundTraining = false;

    private int defaultChartXTime = 30 * 1000; //this is for no round time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quickstart_training);

        isRoundTraining = getIntent().getBooleanExtra(AppConst.ROUNDTRAINING, false);
        mainActivityInstance = MainActivity.getInstance();

        if (isRoundTraining) {
            presetDTO = (PresetDto) getIntent().getSerializableExtra(AppConst.PRESET);
        }

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        mHandler = new Handler();
        titleView.setText(getResources().getString(R.string.quickstarttraining));

        if (isRoundTraining) {
            chartView.setVisibility(View.INVISIBLE);
            digitLayout.setVisibility(View.VISIBLE);
        }else {
            chartView.setVisibility(View.VISIBLE);
            digitLayout.setVisibility(View.INVISIBLE);
        }

        resetConnectDeviceBg("right");
        resetConnectDeviceBg("left");

        setDeviceConnectionState();
        resetPunchDetails();

        if (bellplayer == null)
            bellplayer = MediaPlayer.create(this, R.raw.boxing_bell);

        mainActivityInstance.setUploadCallback(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setBatteryVoltage(MainActivity.leftHandBatteryVoltage);
                setBatteryVoltage(MainActivity.rightHandBatteryVoltage);
            }
        }, 10);
    }

    @OnClick({R.id.starttraining, R.id.back, R.id.audiobtn, R.id.training_speedlayout, R.id.training_powerlayout, R.id.left_sensor_connection_layout, R.id.right_sensor_connection_layout})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.starttraining:
                if (startTrainingBtn.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.starttraining)))
                    startTraining();
                else {
                    startTrainingBtn.setText(getResources().getString(R.string.starttraining));
                    stopTraining();
                }
                break;

            case R.id.back:
                finish();
                break;

            case R.id.audiobtn:
                if (audioEnabled){
                    audioBtn.setImageResource(R.drawable.audio_off);
                    audioEnabled = false;
                }else {
                    audioBtn.setImageResource(R.drawable.audio_on);
                    audioEnabled = true;
                }
                break;

            case R.id.training_speedlayout:
                if (!isSpeedGraph){
                    isSpeedGraph = true;
                    chartView.changeGraph(isSpeedGraph);
                }
                break;

            case R.id.training_powerlayout:
                if (isSpeedGraph){
                    isSpeedGraph = false;
                    chartView.changeGraph(isSpeedGraph);
                }
                break;

            case R.id.left_sensor_connection_layout:
                if (mainActivityInstance.trainingManager.isTrainingRunning())
                    mainActivityInstance.reconnectSensor(true);
                else
                    mainActivityInstance.connectSensor(true);

                break;

            case R.id.right_sensor_connection_layout:
                if (mainActivityInstance.trainingManager.isTrainingRunning())
                    mainActivityInstance.reconnectSensor(false);
                else
                    mainActivityInstance.connectSensor(false);

                break;
        }
    }

    private void startTraining(){
        if (!AppConst.SENSOR_DEBUG) {
            if (!mainActivityInstance.leftSensorConnected && !mainActivityInstance.rightSensorConnected) {
                CommonUtils.showToastMessage(getResources().getString(R.string.connectwithsensors));
                return;
            }
        }

        if (isRoundTraining) {
            chartView.setMaxXAxisValue(Integer.parseInt(PresetUtil.timerwitSecsList.get(presetDTO.getRound_time())) * 1000);
        }else {
            chartView.setMaxXAxisValue(defaultChartXTime);
        }

        if (isRoundTraining) {

            if (currentStatus == -1) {
                //prepare for round 1
                startTrainingBtn.setText(getResources().getString(R.string.stop_training));
                currentStatus = 0;
                roundvalue = 1;

                if (AppConst.DEBUG)
                    totalTime = 5;
                else
                    totalTime = Integer.parseInt(PresetUtil.timerwitSecsList.get(presetDTO.getPrepare()));

                currentTime = totalTime;
                trainingProgressStatus.setText("PREPARE");
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_preparebar));
                trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_prepare));
                isSpeedGraph = false;

                chartView.setVisibility(View.INVISIBLE);
                digitLayout.setVisibility(View.VISIBLE);
            } else {
                if (currentStatus == 0) {
                    //current is prepare
                    startTrainingBtn.setText(getResources().getString(R.string.stop_training));
                } else if (currentStatus == 1) {

                    totalTime = Integer.parseInt(PresetUtil.timerwitSecsList.get(presetDTO.getRest()));
                    currentTime = totalTime;
                    trainingProgressStatus.setText("REST");
                    currentStatus++;
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_restbar));
                    trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_rest));

                    startTrainingBtn.setText(getResources().getString(R.string.stop_training));

                    chartView.setVisibility(View.INVISIBLE);
                    digitLayout.setVisibility(View.VISIBLE);
                } else if (currentStatus == 2) {
                    //current is rest
                    startTrainingBtn.setText(getResources().getString(R.string.stop_training));
                }
            }

            progressBar.setMax(totalTime);
            progressBar.setProgress(totalTime - currentTime);

            startProgressTimer();
        }else {
            playBoxingBell();
            chartView.setVisibility(View.VISIBLE);
            digitLayout.setVisibility(View.INVISIBLE);


            chartView.setPunchDatas(isSpeedGraph, new ArrayList());

            trainingProgressStatus.setText("NO ROUND");
            trainingProgressStatus.setTextColor(getResources().getColor(R.color.force_color));

            trainingStartTime = System.currentTimeMillis();

            resetPunchDetails();
            startNoRoundTimer();
            mainActivityInstance.startTrainingSession(AppConst.TRAINING_TYPE_QUICKSTART, -1);
            mainActivityInstance.startRoundTraining();
        }
    }

    private void stopTraining(){
        if (isRoundTraining) {
            stopProgressTimer();

            mainActivityInstance.stopRoundTraining();
            mainActivityInstance.stopTrainingSession();
        }else {
            trainingProgressStatus.setText("");

            stopNoRoundTimer();

            mainActivityInstance.stopRoundTraining();
            mainActivityInstance.stopTrainingSession();
        }

        if (CommonUtils.isOnline()) {
            startLoading();
            mainActivityInstance.startUplaod(true);
        }else
            finish();
    }

    public void resetPunchDetails(){
        if (punchLists != null && punchLists.size() > 0)
            punchLists.clear();

        if (punchDTOs != null && punchDTOs.size() > 0)
            punchDTOs.clear();

        if (leftpunchDTOS != null && leftpunchDTOS.size() > 0)
            leftpunchDTOS.clear();

        if (rightpunchDTOS != null && rightpunchDTOS.size() > 0)
            rightpunchDTOS.clear();

        punchtypeImageView.setImageResource(R.drawable.none_kck);
        punchtypeView.setText("");

        speedView.setText("0");
        punchcountView.setText("0");
        forceView.setText("0");

        lefthandAvgForceView.setText("0");
        lefthandAvgSpeedView.setText("0");
        leftkickAvgForceView.setText("0");
        leftkickAvgSpeedView.setText("0");

        righthandAvgForceView.setText("0");
        righthandAvgSpeedView.setText("0");
        rightkickAvgForceView.setText("0");
        rightkickAvgSpeedView.setText("0");

        lefthandAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
        lefthandSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
        lefthandAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
        lefthandForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));
        leftkickAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
        leftkickSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
        leftkickAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
        leftkickForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

        righthandAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
        righthandSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
        righthandAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
        righthandForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));
        rightkickAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
        rightkickSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
        rightkickAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
        rightkickForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

        maxForce = 0f;
        avgForce = 0f;
        maxSpeed = 0f;
        avgSpeed = 0f;

        leftavgForce = 0f;
        leftavgSpeed = 0f;

        rightavgForce = 0f;
        rightavgSpeed = 0f;
    }

    public void startNoRoundTimer (){
        currentTime = 0;
        noroundTimer = new Timer();
        initializeNoRoundTimerTask();
        noroundTimer.schedule(updatenoroundTimerTask, 0, 1000);
    }

    public void stopNoRoundTimer (){
        if (noroundTimer != null){
            noroundTimer.cancel();
            noroundTimer = null;
        }
    }

    public void initializeNoRoundTimerTask (){
        updatenoroundTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        currentTime++;
                        String text = PresetUtil.chagngeSecsToTime(currentTime) + " - STOP";

                        startTrainingBtn.setText(text);

                        if (AppConst.SENSOR_DEBUG) {
                            mainActivityInstance.sensorTest();
                        }

                        chartView.setPunchDatas(isSpeedGraph, punchDTOs);
                    }
                });
            }
        };
    }


    public void startProgressTimer (){
        progressTimer = new Timer();
        initializeProgressTimerTask();
        progressTimer.schedule(updateProgressTimerTask, 0, 1000);
    }

    public void stopProgressTimer (){
        if (progressTimer != null){
            progressTimer.cancel();
            progressTimer = null;
        }
    }

    public void initializeProgressTimerTask (){
        updateProgressTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentTime == 0){
                            if (currentStatus == 0){
                                //prepare is finished, go to round
                                totalTime = Integer.parseInt(PresetUtil.timerwitSecsList.get(presetDTO.getRound_time()));
                                currentTime = totalTime;
                                trainingProgressStatus.setText("ROUND " + roundvalue);
                                currentStatus++;
                                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_roundbar));
                                trainingProgressStatus.setTextColor(getResources().getColor(R.color.white));

                                //round is starting
                                playBoxingBell();
                                chartView.setPunchDatas(isSpeedGraph, new ArrayList());
                                chartView.setVisibility(View.VISIBLE);
                                digitLayout.setVisibility(View.INVISIBLE);

                                String text = PresetUtil.chagngeSecsToTime(currentTime) + " - STOP";
                                startTrainingBtn.setText(text);

                                trainingStartTime = System.currentTimeMillis();
                                resetPunchDetails();
                                if (roundvalue == 1)
                                    mainActivityInstance.startTrainingSession(AppConst.TRAINING_TYPE_QUICKSTART, -1);
                                mainActivityInstance.startRoundTraining();
                            }
                            else if (currentStatus == 1){
                                //round is finished, go to rest
                                if (AppConst.DEBUG)
                                    totalTime = 5;
                                else
                                    totalTime = Integer.parseInt(PresetUtil.timerwitSecsList.get(presetDTO.getRest()));

                                currentTime = totalTime;
                                trainingProgressStatus.setText("REST");
                                currentStatus++;
                                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_restbar));
                                trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_rest));

                                startTrainingBtn.setText(getResources().getString(R.string.stop_training));

                                //round is finished
                                playBoxingBell();
                                chartView.setVisibility(View.INVISIBLE);
                                digitLayout.setVisibility(View.VISIBLE);
                                punchtypeView.setText("");
                                mainActivityInstance.stopRoundTraining();

                                if (roundvalue == Integer.parseInt(PresetUtil.roundsList.get(presetDTO.getRounds()))){
                                    //training is finished
                                    CommonUtils.showToastMessage(getResources().getString(R.string.trainingfinished));
                                    currentStatus = -1;
                                    startTrainingBtn.setText(getResources().getString(R.string.starttraining));
                                    trainingProgressStatus.setText("");

                                    stopProgressTimer();

                                    mainActivityInstance.stopTrainingSession();

                                    if (CommonUtils.isOnline()) {
                                        startLoading();
                                        mainActivityInstance.startUplaod(true);
                                    }else
                                        finish();
                                }else {
                                    if (CommonUtils.isOnline()) {
                                        mainActivityInstance.startUplaod(false);
                                    }
                                }
                            }else if (currentStatus == 2){
                                currentStatus++;
                                roundvalue++;
                                totalTime = Integer.parseInt(PresetUtil.timerwitSecsList.get(presetDTO.getRound_time()));
                                currentTime = totalTime;
                                currentStatus = 1;
                                String text = PresetUtil.chagngeSecsToTime(currentTime) + " - STOP";
                                startTrainingBtn.setText(text);

                                trainingProgressStatus.setText("ROUND " + roundvalue);
                                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_roundbar));
//                                    trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_round));
                                trainingProgressStatus.setTextColor(getResources().getColor(R.color.white));
                                chartView.setPunchDatas(isSpeedGraph, new ArrayList());
                                playBoxingBell();
                                trainingStartTime = System.currentTimeMillis();
                                resetPunchDetails();
                                mainActivityInstance.startRoundTraining();
                                chartView.setVisibility(View.VISIBLE);
                                digitLayout.setVisibility(View.INVISIBLE);
                            }

                            progressBar.setProgress(0);
                            progressBar.setMax(totalTime);
                            currentTimeView.setText(PresetUtil.chagngeSecsToTime(currentTime));
                        }else {
                            currentTime--;
                            if (currentStatus == 1 && currentTime == Integer.parseInt(PresetUtil.warningList.get(presetDTO.getWarning()))){
                                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_warningbar));
                                trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_warning));
                            }

                            if (currentStatus == 1){
                                if (currentTime == 0 || currentTime > totalTime - 2){

                                }else {
                                    if (AppConst.SENSOR_DEBUG)
                                        mainActivityInstance.sensorTest();

                                    chartView.setPunchDatas(isSpeedGraph, punchDTOs);
                                }

                                String text = PresetUtil.chagngeSecsToTime(currentTime) + " - STOP";
                                startTrainingBtn.setText(text);
                            }

                            progressBar.setProgress(totalTime - currentTime);
                            currentTimeView.setText(PresetUtil.chagngeSecsToTime(currentTime));
                        }
                    }
                });
            }
        };
    }

    @Override
    public void receivedPunchData(DBTrainingPunchDto trainingPunchDto) {

        punchLists.add(trainingPunchDto);

        PunchDto punchDTO = new PunchDto();
        punchDTO.setTime((int)(System.currentTimeMillis() - trainingStartTime));
        int currentSpeed = trainingPunchDto.mSpeed;
        int currentForce = trainingPunchDto.mForce;
        punchDTO.setPower(currentForce);
        punchDTO.setSpeed(currentSpeed);

        if (!isRoundTraining){
            if (punchDTO.getTime() >= chartView.getMaxXAxisValue()){
                chartView.setMaxXAxisValue(chartView.getMaxXAxisValue() + defaultChartXTime);
            }
        }

        String hand = trainingPunchDto.mHand.equalsIgnoreCase("L") ? "LEFT" : "RIGHT";

        speedView.setText(String.valueOf(currentSpeed));
        forceView.setText(String.valueOf(currentForce));

        maxForce = Math.max(maxForce, (float)currentForce);
        maxSpeed = Math.max(maxSpeed, (float) currentSpeed);

        if (hand.equalsIgnoreCase(AppConst.LEFT_HAND)){

            int leftpunchCount = leftpunchDTOS.size();
            leftavgForce = (leftavgForce * leftpunchCount + currentForce) / (leftpunchCount + 1);
            leftavgSpeed = (leftavgSpeed * leftpunchCount + currentSpeed) / (leftpunchCount + 1);

            leftpunchDTOS.add(punchDTO);

            lefthandAvgSpeedView.setText(String.valueOf((int)leftavgSpeed));
            lefthandAvgForceView.setText(String.valueOf((int)leftavgForce));

            updateTrainingDataView(true, true);

        }else if (hand.equalsIgnoreCase(AppConst.RIGHT_HAND)){
            int rightpunchCount = rightpunchDTOS.size();
            rightavgForce = (avgForce * rightpunchCount + currentForce) / (rightpunchCount + 1);
            rightavgSpeed = (avgSpeed * rightpunchCount + currentSpeed) / (rightpunchCount + 1);
            rightpunchDTOS.add(punchDTO);

            righthandAvgSpeedView.setText(String.valueOf((int)rightavgSpeed));
            righthandAvgForceView.setText(String.valueOf((int)rightavgForce));

            updateTrainingDataView(false, true);
        }

        int punchCount = punchDTOs.size();
        avgForce = (avgForce * punchCount + currentForce) / (punchCount + 1);
        avgSpeed = (avgSpeed * punchCount + currentSpeed) / (punchCount + 1);

        punchDTOs.add(punchDTO);
        punchcountView.setText(String.valueOf(punchDTOs.size()));

        String punchType = trainingPunchDto.mPunchType;

        if (punchType.equalsIgnoreCase(AppConst.JAB_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.JAB);
        } else if (punchType.equalsIgnoreCase(AppConst.HOOK_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.HOOK);
        } else if (punchType.equalsIgnoreCase(AppConst.STRAIGHT_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.STRAIGHT);
        } else if (punchType.equalsIgnoreCase(AppConst.UPPERCUT_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.UPPERCUT);
        } else if (punchType.equalsIgnoreCase(AppConst.UNRECOGNIZED_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.UNRECOGNIZED);
        }
    }

    private void setPunchTypeText(String hand, String punchType) {
        punchtypeView.setText(hand + " " + punchType);
    }

    private void updateTrainingDataView(boolean isLeft, boolean isHand){
        if (isLeft){
            if (isHand){
                punchtypeImageView.setImageResource(R.drawable.left_hand);
                lefthandAvgSpeedView.setTextColor(getResources().getColor(R.color.speed_color));
                lefthandSpeedUnitView.setTextColor(getResources().getColor(R.color.speed_color));
                lefthandAvgForceView.setTextColor(getResources().getColor(R.color.speed_color));
                lefthandForceUnitView.setTextColor(getResources().getColor(R.color.speed_color));

                leftkickAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                leftkickSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                leftkickAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                leftkickForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

                righthandAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                righthandSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                righthandAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                righthandForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

                rightkickAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                rightkickSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                rightkickAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                rightkickForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));
            }else {
                punchtypeImageView.setImageResource(R.drawable.left_kick);
                lefthandAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                lefthandSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                lefthandAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                lefthandForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

                leftkickAvgSpeedView.setTextColor(getResources().getColor(R.color.speed_color));
                leftkickSpeedUnitView.setTextColor(getResources().getColor(R.color.speed_color));
                leftkickAvgForceView.setTextColor(getResources().getColor(R.color.speed_color));
                leftkickForceUnitView.setTextColor(getResources().getColor(R.color.speed_color));

                righthandAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                righthandSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                righthandAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                righthandForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

                rightkickAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                rightkickSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                rightkickAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                rightkickForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));
            }
        }else {
            if (isHand){
                punchtypeImageView.setImageResource(R.drawable.right_hand);
                leftkickAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                leftkickSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                leftkickAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                leftkickForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

                lefthandAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                lefthandSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                lefthandAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                lefthandForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

                righthandAvgSpeedView.setTextColor(getResources().getColor(R.color.speed_color));
                righthandSpeedUnitView.setTextColor(getResources().getColor(R.color.speed_color));
                righthandAvgForceView.setTextColor(getResources().getColor(R.color.speed_color));
                righthandForceUnitView.setTextColor(getResources().getColor(R.color.speed_color));

                rightkickAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                rightkickSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                rightkickAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                rightkickForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));
            }else {
                punchtypeImageView.setImageResource(R.drawable.right_kick);
                leftkickAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                leftkickSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                leftkickAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                leftkickForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

                lefthandAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                lefthandSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                lefthandAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                lefthandForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

                righthandAvgSpeedView.setTextColor(getResources().getColor(R.color.punches_text));
                righthandSpeedUnitView.setTextColor(getResources().getColor(R.color.punches_text));
                righthandAvgForceView.setTextColor(getResources().getColor(R.color.punches_text));
                righthandForceUnitView.setTextColor(getResources().getColor(R.color.punches_text));

                rightkickAvgSpeedView.setTextColor(getResources().getColor(R.color.speed_color));
                rightkickSpeedUnitView.setTextColor(getResources().getColor(R.color.speed_color));
                rightkickAvgForceView.setTextColor(getResources().getColor(R.color.speed_color));
                rightkickForceUnitView.setTextColor(getResources().getColor(R.color.speed_color));
            }
        }
    }

    @Override
    public void setDeviceConnectionState() {
        if (mainActivityInstance.leftSensorConnected) {
            leftConnectionStatusBtn.setImageResource(R.drawable.hexagon_greenbtn);
            leftSensorConnectionLayout.setEnabled(false);
        }

        if (mainActivityInstance.rightSensorConnected) {
            rightConnectionStatusBtn.setImageResource(R.drawable.hexagon_greenbtn);
            rightSensorConnectionLayout.setEnabled(false);
        }
    }

    @Override
    public void handleBatteryLayoutClickable(boolean isLeft, boolean clickable) {
        if (isLeft){
            if (clickable){
                leftConnectionStatusBtn.setImageResource(R.drawable.hexagon_redbtn);
                leftSensorConnectionLayout.setEnabled(true);
            }else {
                leftConnectionStatusBtn.setImageResource(R.drawable.hexagon_greenbtn);
                leftSensorConnectionLayout.setEnabled(false);
            }
        }else {
            if (clickable){
                rightConnectionStatusBtn.setImageResource(R.drawable.hexagon_redbtn);
                rightSensorConnectionLayout.setEnabled(true);
            }else {
                rightConnectionStatusBtn.setImageResource(R.drawable.hexagon_greenbtn);
                rightSensorConnectionLayout.setEnabled(false);
            }
        }
    }

    @Override
    public void resetConnectDeviceBg(String hand) {
        if (hand.equals(AppConst.RIGHT_HAND)) {
            rightConnectionStatusBtn.setImageResource(R.drawable.hexagon_redbtn);
            rightSensorConnectionLayout.setEnabled(true);
        } else {
            leftConnectionStatusBtn.setImageResource(R.drawable.hexagon_redbtn);
            leftSensorConnectionLayout.setEnabled(true);
        }
    }

    @Override
    public void setBatteryVoltage(String batteryVoltage) {
        super.setBatteryVoltage(batteryVoltage);

        if (!batteryVoltage.equals("")) {
            try {
                JSONObject batteryJson = new JSONObject(batteryVoltage);

                if (batteryJson.getString("hand").equals("left")) {
                    leftHandBattery.setText(batteryJson.getString("batteryVoltage") + "%");
                    View parent = (View) leftHandBatteryView.getParent();
                    int width = parent.getWidth();
                    int childViewWidth = (width * Integer.parseInt(batteryJson.getString("batteryVoltage"))) / 100;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(childViewWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 0, 4, 0);
                    leftHandBatteryView.setLayoutParams(params);
                } else {
                    rightHandBattery.setText(batteryJson.getString("batteryVoltage") + "%");
                    View parent = (View) rightHandBatteryView.getParent();
                    int width = parent.getWidth();
                    int childViewWidth = (width * Integer.parseInt(batteryJson.getString("batteryVoltage"))) / 100;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(childViewWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 0, 4, 0);
                    rightHandBatteryView.setLayoutParams(params);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void resetBatteryVoltage(String hand) {
        super.resetBatteryVoltage(hand);

        if (hand.equals("left")) {
            leftHandBattery.setText("0%");
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            leftHandBatteryView.setLayoutParams(params);
        } else {
            rightHandBattery.setText("0%");
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            rightHandBatteryView.setLayoutParams(params);
        }
    }

    private void playBoxingBell(){
        if (audioEnabled) {
            bellplayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityInstance.removeUploadCallback();
        stopProgressTimer();
        mainActivityInstance.stopRoundTraining();
        mainActivityInstance.stopTrainingSession();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        if (mainActivityInstance.trainingManager.isTrainingRunning()){
            CommonUtils.showToastMessage(getResources().getString(R.string.error_trainingfinish));
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void syncFinished() {
        endLoading();

        //get current and last session and go to round analysis or punch analysis screen
        mainActivityInstance.getSessionsWithType(AppConst.TRAINING_TYPE_QUICKSTART, true);
    }

    @Override
    public void getSessionsWithType(ArrayList<TrainingSessionDto> sessionDtos) {
        if (sessionDtos != null && sessionDtos.size() > 0){
            TrainingSessionDto currentSessionDto = sessionDtos.get(sessionDtos.size() - 1);
            TrainingSessionDto lastSessionDto = sessionDtos.get(sessionDtos.size() - 1);

            for (int i = 0; i < sessionDtos.size(); i++){
                TrainingSessionDto temp = sessionDtos.get(sessionDtos.size() - 1 - i);

                if (!temp.mStartTime.equalsIgnoreCase(currentSessionDto.mStartTime)) {
                    lastSessionDto = temp;
                    break;
                }
            }

            Intent roundresultIntent = null;

            if (currentSessionDto.mRoundIds.size() == 0){
                finish();
            }else if (currentSessionDto.mRoundIds.size() < 2){
                //go to punches analysis
                roundresultIntent = new Intent(this, AnalysisRoundBreakdownActivity.class);
                roundresultIntent.putExtra(AppConst.FROM_ROUND, false);
            }else {
                //go to rounds analysis
                roundresultIntent = new Intent(this, AnalysisRoundsActivity.class);
            }

            if (lastSessionDto != null){
                roundresultIntent.putExtra(AppConst.LAST_SESSION, lastSessionDto);
            }
            roundresultIntent.putExtra(AppConst.SESSION_INTENT, currentSessionDto);
            startActivity(roundresultIntent);
            finish();
        }else {
            finish();
        }
    }

    @Override
    public void getSessionsError() {
        finish();
    }
}
