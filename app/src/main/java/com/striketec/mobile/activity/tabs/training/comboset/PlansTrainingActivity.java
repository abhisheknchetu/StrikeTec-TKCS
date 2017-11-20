package com.striketec.mobile.activity.tabs.training.comboset;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseTrainingActivity;
import com.striketec.mobile.activity.analysis.AnalysisCombinationActivity;
import com.striketec.mobile.activity.analysis.AnalysisRoundsActivity;
import com.striketec.mobile.activity.analysis.AnalysisSetroutineActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.dto.ComboDto;
import com.striketec.mobile.dto.DBTrainingPunchDto;
import com.striketec.mobile.dto.PunchDto;
import com.striketec.mobile.dto.SetsDto;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.dto.WorkoutDto;
import com.striketec.mobile.interfaces.UploadFinishCallback;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.ComboSetUtil;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PlansTrainingActivity extends BaseTrainingActivity implements UploadFinishCallback/*, TextToSpeech.OnInitListener*/ {

    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.audiobtn)
    ImageView audioBtn;
    @BindView(R.id.starttraining)
    Button startTrainingBtn;

    //sensor connection status
    @BindView(R.id.leftconnected)
    ImageView leftConnectionStatusBtn;
    @BindView(R.id.rightconnected)
    ImageView rightConnectionStatusBtn;
    @BindView(R.id.left_sensor_connection_layout)
    RelativeLayout leftSensorConnectionLayout;
    @BindView(R.id.right_sensor_connection_layout)
    RelativeLayout rightSensorConnectionLayout;

    //speed, force, count value of current punch
    @BindView(R.id.speed_value)
    TextView speedView;
    @BindView(R.id.punch_value)
    TextView punchcountView;
    @BindView(R.id.power_value)
    TextView forceView;

    //avg datas
    @BindView(R.id.lh_avg_speed)
    TextView lefthandAvgSpeedView;
    @BindView(R.id.lh_mph)
    TextView lefthandSpeedUnitView;
    @BindView(R.id.rh_avg_speed)
    TextView righthandAvgSpeedView;
    @BindView(R.id.rh_mph)
    TextView righthandSpeedUnitView;
    @BindView(R.id.lh_avg_force)
    TextView lefthandAvgForceView;
    @BindView(R.id.lh_lbs)
    TextView lefthandForceUnitView;
    @BindView(R.id.rh_avg_force)
    TextView righthandAvgForceView;
    @BindView(R.id.rh_lbs)
    TextView righthandForceUnitView;
    @BindView(R.id.lk_avg_speed)
    TextView leftkickAvgSpeedView;
    @BindView(R.id.lk_mph)
    TextView leftkickSpeedUnitView;
    @BindView(R.id.rk_avg_speed)
    TextView rightkickAvgSpeedView;
    @BindView(R.id.rk_mph)
    TextView rightkickSpeedUnitView;
    @BindView(R.id.lk_avg_force)
    TextView leftkickAvgForceView;
    @BindView(R.id.lk_lbs)
    TextView leftkickForceUnitView;
    @BindView(R.id.rk_avg_force)
    TextView rightkickAvgForceView;
    @BindView(R.id.rk_lbs)
    TextView rightkickForceUnitView;
    @BindView(R.id.punchtypeimage)
    ImageView punchtypeImageView;

    //punch type/status
    @BindView(R.id.training_punchtype)
    TextView punchtypeView;
    @BindView(R.id.trainingprogress_status)
    TextView trainingProgressStatus;

    @BindView(R.id.progress_digit)
    RelativeLayout digitLayout;
    @BindView(R.id.trainingprogress_time)
    TextView currentTimeView;
    @BindView(R.id.trainingprogressbar)
    ProgressBar progressBar;

    @BindView(R.id.progress_view)
    LinearLayout progressView;

    @BindView(R.id.punch_type_parent)
    LinearLayout comboNumParent;
    @BindView(R.id.punch_result_parent)
    LinearLayout comboResultParent;
    @BindView(R.id.next_combo_tip)
    LinearLayout nextcomboTip;
    @BindView(R.id.next_combo)
    TextView nextComboTipContent;

    @BindView(R.id.left_sensor_battery)
    TextView leftHandBattery;
    @BindView(R.id.right_sensor_battery)
    TextView rightHandBattery;
    @BindView(R.id.battery_life_left)
    View leftHandBatteryView;
    @BindView(R.id.battery_life_right)
    View rightHandBatteryView;
    boolean audioEnabled = true;
    Timer progressCombosetTimer, progressWorkoutTimer;
    TimerTask updateProgressComboSetTimerTask, updateProgressWorkoutTimerTask;
    int temp = 0;
    private String selectedAudioPath = null;
    //    private TextToSpeech tts;
    private MainActivity mainActivityInstance;
    private MediaPlayer bellplayer;
    private float maxSpeed = 0f, avgSpeed = 0, maxForce = 0, avgForce = 0;
    private float leftavgSpeed = 0, leftavgForce = 0;
    private float rightavgSpeed = 0, rightavgForce = 0;
    private ArrayList<DBTrainingPunchDto> punchLists = new ArrayList<>();
    private ArrayList<PunchDto> punchDTOs = new ArrayList<>();
    private ArrayList<PunchDto> leftpunchDTOS = new ArrayList<>();
    private ArrayList<PunchDto> rightpunchDTOS = new ArrayList<>();
    private Handler mHandler;
    private int currentStatus = -1;   //0: prepare, 1: round, 2: resting
    private int roundvalue = 0;
    private int totalTime = 0;
    private int roundCount = 0;
    private int roundTime = 0;
    private int warningTime = 0;
    private int prepareTime = 0;
    private int restTime = 0;
    private int currentTime = 0;
    private int currentProgressTime = 0;
    private long trainingStartTime = 0L;
    private String type;
    private String trainingType;
    private int comboid = -1, setid = -1;
    private ComboDto currentComboDTO;
    private SetsDto currentSetDTO;
    private WorkoutDto workoutDTO = null;
    private int maxview = 0;
    private int currentPunchIndex = 0;
    private int currentComboIndex = 0;
    private int currentSetIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_training);

        type = getIntent().getStringExtra(AppConst.TRAININGTYPE);
        mainActivityInstance = MainActivity.getInstance();

        ButterKnife.bind(this);

        initViews();
    }

    @OnClick({R.id.punchtypeimage, R.id.starttraining, R.id.back, R.id.audiobtn, R.id.left_sensor_connection_layout, R.id.right_sensor_connection_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.starttraining:
                if (startTrainingBtn.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.starttraining)))
                    startTraining();
                else {
                    startTrainingBtn.setText(getResources().getString(R.string.starttraining));
                    stopTraining();
                }
                break;

            //created by rakeshk2 on 10/27/2017 to start listening.
            case R.id.punchtypeimage:
                if (!startTrainingBtn.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.starttraining))) {
                    askSpeechInput();
                }
                break;

            case R.id.back:
                finish();
                break;

            case R.id.audiobtn:
                if (audioEnabled) {
                    audioBtn.setImageResource(R.drawable.audio_off);
                    audioEnabled = false;

                    /**
                     user : rakeshk2
                     date : 11/8/2017
                     description : Stop playing audio.
                     **/
                    stopPlaying();
                } else {
                    audioBtn.setImageResource(R.drawable.audio_on);
                    audioEnabled = true;
//                    playRecordingComboSets();
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

    /**
     * user : rakeshk2
     * date : 10/26/2017
     * Description : Text to Speech
     *
     * @param text text to speak
     */
    /*@Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
//                speakOut("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }*/

    /**
     * user : rakeshk2
     * date : 10/26/2017
     * Description : To speak given text
     *
     * @param text text to speak
     */
    /*private void speakOut(String text) {

        //Speak text (Deprecated in API 21)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }*/

    /**
     * user : rakeshk2
     * date : 10/26/2017
     * description : Showing speech input dialog.
     **/
    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak now");
        try {
            startActivityForResult(intent, AppConst.REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * user : rakeshk2
     * date : 10/27/2017
     * description : Get voice Recognized response result.
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AppConst.REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String recognizedText = result.get(0);
//                    txtText.setText(result.get(0));
                    CommonUtils.showToastMessage(recognizedText);

                    if (recognizedText.contains("stop")) {
                        stopRunningTraining();
                    }
                }
                break;
            }
        }
    }

    /**
     * user : rakeshk2
     * date : 10/26/2017
     * description : Stop running training when user say stop.
     **/
    private void stopRunningTraining() {
        if (!startTrainingBtn.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.starttraining)))
            startTrainingBtn.setText(getResources().getString(R.string.starttraining));
        stopTraining();
    }

    private void initViews() {

        mHandler = new Handler();

        if (type.equalsIgnoreCase(AppConst.COMBINATION)) {
            titleView.setText(getResources().getString(R.string.combinationtraining));
            comboid = getIntent().getIntExtra(AppConst.COMBO_ID, -1);
            trainingType = AppConst.TRAINING_TYPE_COMBINATION;
        } else if (type.equalsIgnoreCase(AppConst.SETS)) {
            titleView.setText(getResources().getString(R.string.setroutinetraining));
            setid = getIntent().getIntExtra(AppConst.SET_ID, -1);
            trainingType = AppConst.TRAINING_TYPE_SETS;
        } else if (type.equalsIgnoreCase(AppConst.WORKOUT)) {
            titleView.setText(getResources().getString(R.string.workouttraining));
            workoutDTO = (WorkoutDto) getIntent().getSerializableExtra(AppConst.WORKOUT_ID);
            trainingType = AppConst.TRAINING_TYPE_WORKOUT;
        }

        if (bellplayer == null)
            bellplayer = MediaPlayer.create(this, R.raw.boxing_bell);

        progressView.setVisibility(View.VISIBLE);
        nextcomboTip.setVisibility(View.INVISIBLE);

        resetConnectDeviceBg("right");
        resetConnectDeviceBg("left");

        setDeviceConnectionState();
        resetPunchDetails();

        mainActivityInstance.setUploadCallback(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setBatteryVoltage(MainActivity.leftHandBatteryVoltage);
                setBatteryVoltage(MainActivity.rightHandBatteryVoltage);
            }
        }, 10);

        /**
         user : rakeshk2
         date : 11/8/2017
         description : Create object for text to speech.
         **/
//        tts = new TextToSpeech(this, this);

    }

    public void resetPunchDetails() {
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

    private void setInfo() {
        if (comboid != -1) {
            currentComboDTO = ComboSetUtil.getComboDtowithID(comboid);
            currentTime = 0;
            initComboTrainingView();

            nextcomboTip.setVisibility(View.INVISIBLE);
            digitLayout.setVisibility(View.INVISIBLE);

        } else if (setid != -1) {
            currentTime = 0;
            currentSetDTO = ComboSetUtil.getSetDtowithID(setid);
            currentComboIndex = 0;
            currentComboDTO = ComboSetUtil.getComboDtowithID(currentSetDTO.getComboIDLists().get(0));
            initComboTrainingView();
            nextcomboTip.setVisibility(View.INVISIBLE);
            digitLayout.setVisibility(View.INVISIBLE);
        } else if (workoutDTO != null) {
            currentTime = 0;
            currentComboIndex = 0;
            currentComboDTO = ComboSetUtil.getComboDtowithID(workoutDTO.getRoundsetIDs().get(0).get(0));
            roundCount = workoutDTO.getRoundcount();
            roundTime = Integer.parseInt(PresetUtil.timerwitSecsList.get(workoutDTO.getRound()));
            warningTime = Integer.parseInt(PresetUtil.timerwitSecsList.get(workoutDTO.getWarning()));
            prepareTime = Integer.parseInt(PresetUtil.timerwitSecsList.get(workoutDTO.getPrepare()));
            restTime = Integer.parseInt(PresetUtil.timerwitSecsList.get(workoutDTO.getRest()));

            progressView.setVisibility(View.INVISIBLE);
            nextcomboTip.setVisibility(View.INVISIBLE);
            digitLayout.setVisibility(View.VISIBLE);

            initComboTrainingView();
            //startProgressWorkoutTimer();
        } else {
            titleView.setText("TRAINING");
        }
    }

    private void initComboTrainingView() {
        currentPunchIndex = 0;
        comboResultParent.removeAllViews();
        comboNumParent.removeAllViews();

        maxview = AppConst.MAX_NUM_FORPUNCH;//Math.max(EFDConstants.MAX_NUM_FORPUNCH, comboDTO.getComboTypes().size());

        for (int i = 0; i < maxview; i++) {
            addPunchNumView(i);
        }

        for (int i = 0; i < currentComboDTO.getComboTypes().size(); i++) {
            addPunchResultView(i, currentComboDTO.getComboTypes().get(i));
        }

        //first 3 views has to be invisible
//        int min = Math.min(4, currentComboDTO.getComboTypes().size());

        int min = Math.max(0, 3 - currentPunchIndex);
        int max = Math.min(7, currentComboDTO.getComboTypes().size() - currentPunchIndex + 3);

        for (int i = 0; i < maxview; i++) {
            LinearLayout child = (LinearLayout) comboNumParent.getChildAt(i);
            TextView keyView = (TextView) child.findViewById(R.id.key);

            if (i < min) {
                child.setVisibility(View.INVISIBLE);
            } else if (i < max) {
                child.setVisibility(View.VISIBLE);
                keyView.setText(currentComboDTO.getComboTypes().get(currentPunchIndex + i - 3));

                if (i == max - 1) {
                    View divider = child.findViewById(R.id.combo_divider);
                    divider.setVisibility(View.INVISIBLE);
                }
            } else {
                child.setVisibility(View.INVISIBLE);
            }
        }

        if (workoutDTO == null) {
            trainingProgressStatus.setText(ComboSetUtil.punchTypeMap.get(currentComboDTO.getComboTypes().get(0)));
            trainingProgressStatus.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void addPunchResultView(int position, String key) {
        final LinearLayout newLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_training_punchresult, null);
        TextView keyView = (TextView) newLayout.findViewById(R.id.key);

        if (position == 0) {
            keyView.setBackgroundResource(R.drawable.next_punch_first);
            keyView.setTextColor(getResources().getColor(R.color.white));
        } else {
            keyView.setBackgroundResource(R.drawable.punchkey_bg_later);
        }

        keyView.setText(key);

        comboResultParent.addView(newLayout, position);

        if (position != 0) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) newLayout.getLayoutParams();
            params.leftMargin = CommonUtils.dpTopx(-3);
            newLayout.setLayoutParams(params);
        }
    }

    private void addPunchNumView(int position) {
        final LinearLayout newLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_training_punchkey, null);
        TextView keyView = (TextView) newLayout.findViewById(R.id.key);
        View comboDivider = newLayout.findViewById(R.id.combo_divider);

        keyView.setAlpha(1 - (float) (Math.abs(3 - position) * 0.32));
        comboDivider.setAlpha(0.25f);
        comboNumParent.addView(newLayout, position);

        if (position == 3) {
            keyView.setTextSize(getResources().getInteger(R.integer.punch_textsize_90));
        } else {
            keyView.setTextSize(getResources().getInteger(R.integer.punch_textsize_60));
        }

        if (position == maxview - 1) {
            comboDivider.setVisibility(View.GONE);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        newLayout.setLayoutParams(params);
    }

    private void gotoNextCombo() {
        currentPunchIndex = 0;
        int currentTrainingComboCount = 0;

        if (setid != -1) {
            currentTrainingComboCount = currentSetDTO.getComboIDLists().size();
        } else if (workoutDTO != null) {
            currentTrainingComboCount = workoutDTO.getRoundsetIDs().get(roundvalue - 1).size();
        }

        if (currentComboIndex == currentTrainingComboCount - 1) {
            if (setid != -1) {
                playBoxingBell();
                mainActivityInstance.receivePunchable = false;
                stopTraining();
            } else {
                mainActivityInstance.receivePunchable = false;
            }
        } else {
            nextcomboTip.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.INVISIBLE);

            if (setid != -1) {
                ComboDto comboDTO = ComboSetUtil.getComboDtowithID(currentSetDTO.getComboIDLists().get(currentComboIndex + 1));
                nextComboTipContent.setText(comboDTO.getCombos());

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextcomboTip.setVisibility(View.INVISIBLE);
                        currentComboIndex++;
                        currentComboDTO = ComboSetUtil.getComboDtowithID(currentSetDTO.getComboIDLists().get(currentComboIndex));
                        progressView.setVisibility(View.VISIBLE);
                        mainActivityInstance.receivePunchable = true;
                        initComboTrainingView();
                    }
                }, 1000);

            } else if (workoutDTO != null) {
                ComboDto comboDTO = ComboSetUtil.getComboDtowithID(workoutDTO.getRoundsetIDs().get(roundvalue - 1).get(currentComboIndex + 1));
                nextComboTipContent.setText(comboDTO.getCombos());

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextcomboTip.setVisibility(View.INVISIBLE);
                        currentComboIndex++;
                        currentComboDTO = ComboSetUtil.getComboDtowithID(workoutDTO.getRoundsetIDs().get(roundvalue - 1).get(currentComboIndex));

                        /**
                         user : rakeshk2
                         date : 11/8/2017
                         description : Start playing Audio according to current combo set.
                         **/
                        playRecordingScripted();

                        progressView.setVisibility(View.VISIBLE);
                        mainActivityInstance.receivePunchable = true;
                        initComboTrainingView();
                    }
                }, 1000);
            }
        }
    }

    private void updateView(boolean success) {

        if (currentPunchIndex == currentComboDTO.getComboTypes().size() - 1) {

            //update num view
            LinearLayout child = (LinearLayout) comboNumParent.getChildAt(3);
            TextView keyView = (TextView) child.findViewById(R.id.key);
            keyView.setTextSize(getResources().getInteger(R.integer.punch_textsize_90));

            LinearLayout resultChild = (LinearLayout) comboResultParent.getChildAt(currentPunchIndex);
            TextView resultkeyView = (TextView) resultChild.findViewById(R.id.key);

            //update result view

            if (currentPunchIndex == 0) {
                if (success)
                    resultkeyView.setBackgroundResource(R.drawable.punch_success_first);
                else
                    resultkeyView.setBackgroundResource(R.drawable.punch_fail_first);
            } else {
                if (success)
                    resultkeyView.setBackgroundResource(R.drawable.punch_success_next);
                else
                    resultkeyView.setBackgroundResource(R.drawable.punch_fail_next);
            }

            resultkeyView.setTextColor(getResources().getColor(R.color.black));

            //training is finished,
            if (comboid != -1) {
                playBoxingBell();
                mainActivityInstance.receivePunchable = false;
                stopTraining();

                //go to stats activity;
            } else if (setid != -1) {
                mainActivityInstance.receivePunchable = false;
                gotoNextCombo();
            } else if (workoutDTO != null) {
                mainActivityInstance.receivePunchable = false;
                gotoNextCombo();
            }
        } else {
            currentPunchIndex++;

            int min = Math.max(0, 3 - currentPunchIndex);
            int max = Math.min(7, currentComboDTO.getComboTypes().size() - currentPunchIndex + 3);

            for (int i = 0; i < maxview; i++) {
                LinearLayout child = (LinearLayout) comboNumParent.getChildAt(i);
                TextView keyView = (TextView) child.findViewById(R.id.key);

                if (i < min) {
                    child.setVisibility(View.INVISIBLE);
                } else if (i < max) {
                    child.setVisibility(View.VISIBLE);
                    keyView.setText(currentComboDTO.getComboTypes().get(currentPunchIndex + i - 3));

                    if (i == 3) {
                        keyView.setTextSize(getResources().getInteger(R.integer.punch_textsize_90));
                    } else {
                        keyView.setTextSize(getResources().getInteger(R.integer.punch_textsize_60));
                    }

                    if (i == max - 1) {
                        View divider = child.findViewById(R.id.combo_divider);
                        divider.setVisibility(View.INVISIBLE);
                    }
                } else {
                    child.setVisibility(View.INVISIBLE);
                }
            }

            LinearLayout resultcurrentChild = (LinearLayout) comboResultParent.getChildAt(currentPunchIndex - 1);
            TextView resultcurrentkeyView = (TextView) resultcurrentChild.findViewById(R.id.key);

            //update result view

            if (currentPunchIndex == 1) {
                if (success)
                    resultcurrentkeyView.setBackgroundResource(R.drawable.punch_success_first);
                else
                    resultcurrentkeyView.setBackgroundResource(R.drawable.punch_fail_first);
            } else {
                if (success)
                    resultcurrentkeyView.setBackgroundResource(R.drawable.punch_success_next);
                else
                    resultcurrentkeyView.setBackgroundResource(R.drawable.punch_fail_next);
            }

            resultcurrentkeyView.setTextColor(getResources().getColor(R.color.black));

            //update next result view
            LinearLayout resultnextChild = (LinearLayout) comboResultParent.getChildAt(currentPunchIndex);
            final TextView resultnextkeyView = (TextView) resultnextChild.findViewById(R.id.key);

            resultnextkeyView.setBackgroundResource(R.drawable.next_punch_next);
            resultnextkeyView.setTextColor(getResources().getColor(R.color.white));

            if (resultnextkeyView.getText().toString().length() == 2) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateView(true);
                    }
                }, 300);
            }

            if (workoutDTO == null)
                trainingProgressStatus.setText(ComboSetUtil.punchTypeMap.get(currentComboDTO.getComboTypes().get(currentPunchIndex)));
        }
    }

    private void startTraining() {
        if (!AppConst.SENSOR_DEBUG) {
            if (!mainActivityInstance.leftSensorConnected && !mainActivityInstance.rightSensorConnected) {
                CommonUtils.showToastMessage(getResources().getString(R.string.connectwithsensors));
                return;
            }
        }

        if (comboid != -1 || setid != -1) {
            //this is combo training
            startProgressCombosetTimer();
            playBoxingBell();

            if (comboid != -1) {
                /**
                 user : rakeshk2
                 date : 11/2/2017
                 description : Start playing Audio.
                 **/
//                playRecordingComboSets();
                playRecordingForCombo();

                mainActivityInstance.startTrainingSession(AppConst.TRAINING_TYPE_COMBINATION, comboid);
            } else {
                mainActivityInstance.startTrainingSession(AppConst.TRAINING_TYPE_SETS, setid);
            }

            mainActivityInstance.startRoundTraining();
            startTrainingBtn.setText(getResources().getString(R.string.stop_training));
        } else if (workoutDTO != null) {
            if (currentStatus == -1) {

                playRecordingScripted();

                startProgressCombosetTimer();
                startProgressWorkoutTimer();
                //prepare for round 1
                currentStatus = 0;
                roundvalue = 1;

                if (AppConst.DEBUG)
                    totalTime = 5;
                else
                    totalTime = prepareTime;

                currentProgressTime = totalTime;
                trainingProgressStatus.setText("PREPARE");
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_preparebar));
                trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_prepare));

                nextcomboTip.setVisibility(View.INVISIBLE);
                progressView.setVisibility(View.INVISIBLE);
                digitLayout.setVisibility(View.VISIBLE);

                progressBar.setMax(totalTime);
                progressBar.setProgress(totalTime - currentProgressTime);
            }
        }
    }

    /**
     * user : rakeshk2
     * date : 11/2/2017
     * description : Play Audio for combos and sets training.
     **/
    public void playRecordingComboSets() {
        try {
            if (audioEnabled) {
                ArrayList<ComboDto> comboDtos = SharedUtils.getSavedCombinationList();
                if (comboid != -1) {
                    selectedAudioPath = CommonUtils.searchFile(comboDtos.get(comboid - 1).getName());
                    if (selectedAudioPath != null) {
//                    selectedAudioPath = CommonUtils.searchFile(comboDtos.get(comboid).getName());
                        bellplayer = MediaPlayer.create(this, Uri.parse(getString(R.string.file) + selectedAudioPath));
                        bellplayer.start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * user : rakeshk2
     * date : 11/2/2017
     * description : Play Audio for scripted training.
     **/
    public void playRecordingScripted() {
        try {
            if (audioEnabled) {
                if (currentComboDTO != null) {
                    selectedAudioPath = CommonUtils.searchFile(currentComboDTO.getName());
                    if (selectedAudioPath != null) {
                        bellplayer = MediaPlayer.create(this, Uri.parse(getString(R.string.file) + selectedAudioPath));
                        bellplayer.start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * user : rakeshk2
     * date : 11/2/2017
     * description : Stop playing Audio file.
     **/
    public void stopPlaying() {
        if (bellplayer != null) {
            bellplayer.release();
            bellplayer = null;
        }
    }

    private void stopTraining() {

        stopProgressCombosetTimer();
        stopProgressWorkoutTimer();

        startTrainingBtn.setText(getResources().getString(R.string.starttraining));
        mainActivityInstance.stopRoundTraining();
        mainActivityInstance.stopTrainingSession();

        if (CommonUtils.isOnline()) {
            startLoading();
            mainActivityInstance.startUplaod(true);
        } else
            finish();
    }

    public void startProgressCombosetTimer() {
        progressCombosetTimer = new Timer();
        initializeProgressCombosetTimerTask();
        progressCombosetTimer.schedule(updateProgressComboSetTimerTask, 0, 1000);
    }

    public void stopProgressCombosetTimer() {
        /**
         user : rakeshk2
         date : 11/2/2017
         description : Stop Playing Recording.
         **/
        stopPlaying();

        if (progressCombosetTimer != null) {
            progressCombosetTimer.cancel();
            progressCombosetTimer = null;
        }
    }

    public void initializeProgressCombosetTimerTask() {
        updateProgressComboSetTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        currentTime++;
                        String text = PresetUtil.chagngeSecsToTime(currentTime) + " - STOP";

                        startTrainingBtn.setText(text);

                        if (AppConst.SENSOR_DEBUG) {

                            if (mainActivityInstance.receivePunchable) {
                                if (AppConst.SENSOR_DEBUG)
                                    mainActivityInstance.sensorTest();
                            }
                        }

                    }
                });

            }
        };
    }

    public void startProgressWorkoutTimer() {
        progressWorkoutTimer = new Timer();
        initializeProgressWorkoutTimerTask();
        progressWorkoutTimer.schedule(updateProgressWorkoutTimerTask, 0, 1000);
    }

    public void stopProgressWorkoutTimer() {
        if (progressWorkoutTimer != null) {
            progressWorkoutTimer.cancel();
            progressWorkoutTimer = null;
        }
    }

    public void initializeProgressWorkoutTimerTask() {
        updateProgressWorkoutTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentProgressTime == 0) {
                            if (currentStatus == 0) {
                                //prepare is finished, go to round
                                totalTime = roundTime;
                                currentProgressTime = totalTime;

                                trainingProgressStatus.setText("ROUND " + roundvalue);
                                currentStatus++;
                                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_roundbar));
                                trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_round));

                                trainingStartTime = System.currentTimeMillis();
                                resetPunchDetails();
                                if (roundvalue == 1)
                                    mainActivityInstance.startTrainingSession(AppConst.TRAINING_TYPE_WORKOUT, workoutDTO.getId());

                                mainActivityInstance.startRoundTraining();

                                progressView.setVisibility(View.VISIBLE);
                                digitLayout.setVisibility(View.INVISIBLE);
                                //round is starting
                                playBoxingBell();
                            } else if (currentStatus == 1) {
                                //round is finished, go to rest
                                if (AppConst.DEBUG)
                                    totalTime = 5;
                                else
                                    totalTime = restTime;

                                currentProgressTime = totalTime;
                                trainingProgressStatus.setText("REST");
                                currentStatus++;
                                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_restbar));
                                trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_rest));

                                if (roundvalue == roundCount) {
                                    //training is finished
                                    currentStatus = -1;
                                    startTrainingBtn.setText(getResources().getString(R.string.starttraining));

                                    stopTraining();
                                } else {
                                    mainActivityInstance.stopRoundTraining();

                                    if (CommonUtils.isOnline()) {
                                        mainActivityInstance.startUplaod(false);
                                    }
                                }

                                punchtypeView.setText("");

                                nextcomboTip.setVisibility(View.INVISIBLE);
                                progressView.setVisibility(View.INVISIBLE);
                                digitLayout.setVisibility(View.VISIBLE);
                                //round is finished
                                playBoxingBell();
                            } else if (currentStatus == 2) {
                                currentStatus++;
                                roundvalue++;
                                totalTime = roundTime;
                                currentProgressTime = totalTime;
                                currentStatus = 1;
                                trainingProgressStatus.setText("ROUND " + roundvalue);
                                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_roundbar));
                                trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_round));

                                currentComboIndex = 0;
                                currentComboDTO = ComboSetUtil.getComboDtowithID(workoutDTO.getRoundsetIDs().get(roundvalue - 1).get(0));
                                initComboTrainingView();

                                trainingStartTime = System.currentTimeMillis();
                                resetPunchDetails();
                                mainActivityInstance.startRoundTraining();

                                progressView.setVisibility(View.VISIBLE);
                                digitLayout.setVisibility(View.INVISIBLE);

                                playBoxingBell();
                            }

                            progressBar.setProgress(0);
                            progressBar.setMax(totalTime);
                            currentTimeView.setText(PresetUtil.chagngeSecsToTime(currentProgressTime));
                        } else {
                            currentProgressTime--;
                            if (currentStatus == 1 && currentProgressTime == warningTime) {
                                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_warningbar));
                                trainingProgressStatus.setTextColor(getResources().getColor(R.color.progress_warning));
                            }
                            progressBar.setProgress(totalTime - currentProgressTime);
                            currentTimeView.setText(PresetUtil.chagngeSecsToTime(currentProgressTime));
                        }
                    }
                });
            }
        };
    }

    private void playBoxingBell() {
        if (audioEnabled) {
            if (bellplayer == null)
                bellplayer = MediaPlayer.create(this, R.raw.boxing_bell);
            bellplayer.start();
        }
    }

    @Override
    public void receivedPunchData(DBTrainingPunchDto trainingPunchDto) {
        punchLists.add(trainingPunchDto);

        PunchDto punchDTO = new PunchDto();
        punchDTO.setTime((int) (System.currentTimeMillis() - trainingStartTime));
        int currentSpeed = trainingPunchDto.mSpeed;
        int currentForce = trainingPunchDto.mForce;
        punchDTO.setPower(currentForce);
        punchDTO.setSpeed(currentSpeed);

        String hand = trainingPunchDto.mHand.equalsIgnoreCase("L") ? "LEFT" : "RIGHT";

        speedView.setText(String.valueOf(currentSpeed));
        forceView.setText(String.valueOf(currentForce));

        maxForce = Math.max(maxForce, (float) currentForce);
        maxSpeed = Math.max(maxSpeed, (float) currentSpeed);

        if (hand.equalsIgnoreCase(AppConst.LEFT_HAND)) {

            int leftpunchCount = leftpunchDTOS.size();
            leftavgForce = (leftavgForce * leftpunchCount + currentForce) / (leftpunchCount + 1);
            leftavgSpeed = (leftavgSpeed * leftpunchCount + currentSpeed) / (leftpunchCount + 1);

            leftpunchDTOS.add(punchDTO);

            lefthandAvgSpeedView.setText(String.valueOf((int) leftavgSpeed));
            lefthandAvgForceView.setText(String.valueOf((int) leftavgForce));

            updateTrainingDataView(true, true);

        } else if (hand.equalsIgnoreCase(AppConst.RIGHT_HAND)) {
            int rightpunchCount = rightpunchDTOS.size();
            rightavgForce = (avgForce * rightpunchCount + currentForce) / (rightpunchCount + 1);
            rightavgSpeed = (avgSpeed * rightpunchCount + currentSpeed) / (rightpunchCount + 1);
            rightpunchDTOS.add(punchDTO);

            righthandAvgSpeedView.setText(String.valueOf((int) rightavgSpeed));
            righthandAvgForceView.setText(String.valueOf((int) rightavgForce));

            updateTrainingDataView(false, true);
        }

        int punchCount = punchDTOs.size();
        avgForce = (avgForce * punchCount + currentForce) / (punchCount + 1);
        avgSpeed = (avgSpeed * punchCount + currentSpeed) / (punchCount + 1);

        punchDTOs.add(punchDTO);
        punchcountView.setText(String.valueOf(punchDTOs.size()));

        String punchType = trainingPunchDto.mPunchType;

        if (punchType.equalsIgnoreCase(AppConst.JAB_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.JAB, currentSpeed, currentForce);
        } else if (punchType.equalsIgnoreCase(AppConst.HOOK_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.HOOK, currentSpeed, currentForce);
        } else if (punchType.equalsIgnoreCase(AppConst.STRAIGHT_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.STRAIGHT, currentSpeed, currentForce);
        } else if (punchType.equalsIgnoreCase(AppConst.UPPERCUT_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.UPPERCUT, currentSpeed, currentForce);
        } else if (punchType.equalsIgnoreCase(AppConst.UNRECOGNIZED_ABBREVIATION_TEXT)) {
            setPunchTypeText(hand, AppConst.UNRECOGNIZED, currentSpeed, currentForce);
        }
    }

    private void setPunchTypeText(String hand, String punchType, int speed, int force) {
        String detectedPunchType = hand + " " + punchType;
        punchtypeView.setText(detectedPunchType);

        try {
            /**
             * Announce the shot.
             */
            /*if (audioEnabled) {
//                ConvertTextToSpeech.SpeakText(PlansTrainingActivity.this, detectedPunchType);
                speakOut(detectedPunchType);
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String successString = ComboSetUtil.punchTypeMap.get(currentComboDTO.getComboTypes().get(currentPunchIndex));

        if (successString.equalsIgnoreCase(AppConst.JAB) || successString.equalsIgnoreCase(AppConst.STRAIGHT)) {
            if (punchType.equalsIgnoreCase(successString)) {
                updateView(true);
            } else {
                updateView(false);
            }

        } else {
            if (detectedPunchType.equalsIgnoreCase(successString)) {
                updateView(true);
            } else {
                updateView(false);
            }
        }
    }

    private void updateTrainingDataView(boolean isLeft, boolean isHand) {
        if (isLeft) {
            if (isHand) {
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
            } else {
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
        } else {
            if (isHand) {
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
            } else {
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
        if (isLeft) {
            if (clickable) {
                leftConnectionStatusBtn.setImageResource(R.drawable.hexagon_redbtn);
                leftSensorConnectionLayout.setEnabled(true);
            } else {
                leftConnectionStatusBtn.setImageResource(R.drawable.hexagon_greenbtn);
                leftSensorConnectionLayout.setEnabled(false);
            }
        } else {
            if (clickable) {
                rightConnectionStatusBtn.setImageResource(R.drawable.hexagon_redbtn);
                rightSensorConnectionLayout.setEnabled(true);
            } else {
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

    @Override
    protected void onResume() {
        super.onResume();
        setInfo();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityInstance.removeUploadCallback();
        mainActivityInstance.stopRoundTraining();
        mainActivityInstance.stopTrainingSession();
        stopProgressCombosetTimer();
        stopProgressWorkoutTimer();

        /**
         user : rakeshk2
         date : 10/26/2017
         description : shutdown Text to speech.
         **/
        /*if (tts != null) {
            tts.stop();
            tts.shutdown();
        }*/
    }

    @Override
    public void onBackPressed() {
        if (mainActivityInstance.trainingManager.isTrainingRunning()) {
            CommonUtils.showToastMessage(getResources().getString(R.string.error_trainingfinish));
        } else {
            super.onBackPressed();
        }
    }

    private void tmpStart() {
        if (mainActivityInstance.receivePunchable) {
            temp++;
            if (temp % 2 == 0) {
                updateView(true);
            } else {
                updateView(false);
            }
        }
    }

    @Override
    public void syncFinished() {
        endLoading();
        mainActivityInstance.getSessionsWithType(trainingType, false);
    }

    @Override
    public void getSessionsWithType(ArrayList<TrainingSessionDto> sessionDtos) {
        if (sessionDtos != null && sessionDtos.size() > 0) {
            TrainingSessionDto currentSessionDto = sessionDtos.get(sessionDtos.size() - 1);

            Intent nextIntent = null;
            if (trainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_COMBINATION)) {
                nextIntent = new Intent(this, AnalysisCombinationActivity.class);
            } else if (trainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_SETS)) {
                nextIntent = new Intent(this, AnalysisSetroutineActivity.class);
            } else {
                nextIntent = new Intent(this, AnalysisRoundsActivity.class);
            }

            nextIntent.putExtra(AppConst.SESSION_INTENT, currentSessionDto);

            startActivity(nextIntent);
            finish();
        } else {
            finish();
        }
    }

    /**
     * user : rakeshk2
     * date : 11/15/2017
     * description : Play recording after bell time delay
     **/
    public void playRecordingForCombo() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playRecordingComboSets();
            }
        }, 1000);
    }

    @Override
    public void getSessionsError() {
        finish();
    }
}
