package com.striketec.mobile.activity.analysis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.TipMistakeListAdapter;
import com.striketec.mobile.customview.CustomCircleView;
import com.striketec.mobile.dto.MistakeDto;
import com.striketec.mobile.dto.TrainingPunchDto;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.PresetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnalysisTipsActivity extends BaseActivity {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.active_percent) TextView activePercentView;
    @BindView(R.id.inactive_percent) TextView inactivePercentView;
    @BindView(R.id.bestpunch_speed) TextView bestpunchSpeedView;
    @BindView(R.id.bestpunch_force) TextView bestpunchForceView;
    @BindView(R.id.active_time) TextView activeTimeView;
    @BindView(R.id.inactive_time) TextView inactiveTimeView;

    @BindView(R.id.stats_percent) CustomCircleView circlePercentView;

    @BindView(R.id.totalmistake) TextView mistakeInfoView;
    @BindView(R.id.mistakesview)  ListView mistakesView;

    float avgforce = 0, avgspeed = 0;

    private ArrayList<TrainingPunchDto> trainingPunchDtos = new ArrayList<>();
    private ArrayList<ArrayList<MistakeDto>> mistakeDtos = new ArrayList<>();
    private HashMap<String, ArrayList<TrainingPunchDto>> punchtypeMap = new HashMap<>();

    TipMistakeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_tips);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.tips));

        circlePercentView.setDeactivePaint(getResources().getColor(R.color.circle_deactive));
        circlePercentView.setStrokeWidth(getResources().getInteger(R.integer.int_30));
        circlePercentView.setActivePaint(getResources().getColor(R.color.speed_color));
        circlePercentView.setInnerPaint(getResources().getColor(R.color.force_color));

        adapter = new TipMistakeListAdapter(this, mistakeDtos);
        mistakesView.setAdapter(adapter);
    }

    @OnClick({R.id.back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    private void loadInfo(){
        bestpunchSpeedView.setText("0");
        bestpunchForceView.setText("0");
        activeTimeView.setText("00:00");
        inactiveTimeView.setText("00:00");

        updateCirlceView(true, 0, 0);

        int totalTime = CommonUtils.getRandomNum(50, 40);
        int activetime = getActiveTime();
        int inactivetime = totalTime - activetime;

        int activepercent = (int)(activetime / (totalTime * 1f) * 100);
        int inactivepercent = 100 - activepercent;

        int activeAngle = (int)(activetime / (totalTime * 1f) * 360);
        int inactiveAngle = 360 - activeAngle;

        activePercentView.setText(activepercent + "%");
        inactivePercentView.setText(inactivepercent + "%");

        updateCirlceView(true, activeAngle, inactiveAngle);

        activeTimeView.setText(PresetUtil.changeSecondsToMinutesWithTwoDigit(activetime));
        inactiveTimeView.setText(PresetUtil.changeSecondsToMinutesWithTwoDigit(inactivetime));

        Collections.sort(trainingPunchDtos, FORCE_COMPARATOR);
        bestpunchSpeedView.setText(String.valueOf((int)trainingPunchDtos.get(0).mSpeed));
        bestpunchForceView.setText(String.valueOf((int)trainingPunchDtos.get(0).mForce));

        //calculate mistakes
        calculatedMistakes();
    }

    private void updateCirlceView(boolean hasinner, float outerangle, float innerangle){
        circlePercentView.setAngle(outerangle);
        circlePercentView.setInnerAngle(innerangle);
        circlePercentView.setInner(hasinner);

        circlePercentView.invalidate();
    }

    private int getActiveTime(){
        float sum = 0;
        avgforce = 0f;
        avgspeed = 0f;

        for (int i = 0; i < trainingPunchDtos.size(); i++){
            sum += trainingPunchDtos.get(i).mPunchDuration;
            avgforce += trainingPunchDtos.get(i).mForce;
            avgspeed += trainingPunchDtos.get(i).mSpeed;
        }

        if (trainingPunchDtos.size() > 0){
            avgforce = avgforce / trainingPunchDtos.size();
            avgspeed = avgspeed / trainingPunchDtos.size();
        }

        return (int)sum;
    }

    private void loadPunches(){
        for (int i = 0; i < 50; i++){
            TrainingPunchDto punchResultDto = new TrainingPunchDto();

            String desiredType, startTime;
            float duration;
            int speed, force;

            String hand, punch;
            int hand_index = CommonUtils.getRandomNum(2, 0);
            int punch_index = CommonUtils.getRandomNum(4, 0);

            if (hand_index == 0)
                hand = "Left";
            else
                hand = "Right";

            if (punch_index == 0){
                punch = "Jab";
            }else if (punch_index == 1){
                punch = "hook";
            }else if (punch_index == 2){
                punch = "straight";
            }else {
                punch = "uppercut";
            }

            startTime = String.valueOf(System.currentTimeMillis());
            duration = ((float)CommonUtils.getRandomNum(50, 20)) / 100;
            force = CommonUtils.getRandomNum(600, 100);
            speed = CommonUtils.getRandomNum(35, 10);

            punchResultDto.mHand = hand;
            punchResultDto.mPunchType = punch;
            punchResultDto.mPunchTime = startTime;
            punchResultDto.mPunchDuration = duration;
            punchResultDto.mSpeed = speed;
            punchResultDto.mForce = force;

            trainingPunchDtos.add(punchResultDto);
        }
    }

    private void calculatedMistakes(){

        String mistakeInfo = "";
        int mistakenumber = 0;

        for (int i = 0; i < trainingPunchDtos.size(); i++){
            String type = trainingPunchDtos.get(i).mHand + " " + trainingPunchDtos.get(i).mPunchType;
            if (punchtypeMap.containsKey(type)){
                punchtypeMap.get(type).add(trainingPunchDtos.get(i));
            }else {
                ArrayList<TrainingPunchDto> results = new ArrayList<>();
                results.add(trainingPunchDtos.get(i));
                punchtypeMap.put(type, results);
            }
        }

        for (String type : punchtypeMap.keySet()){
            ArrayList<TrainingPunchDto> punchResultDtos = punchtypeMap.get(type);

            ArrayList<MistakeDto> mistakes = new ArrayList<>();
            for (int i = 0; i < punchResultDtos.size(); i++){
                if (punchResultDtos.get(i).mSpeed < (avgspeed * 0.6)){
                    mistakes.add(new MistakeDto(punchResultDtos.get(i), true));
                }

                if (punchResultDtos.get(i).mForce < (avgforce * 0.6)){
                    mistakes.add(new MistakeDto(punchResultDtos.get(i), false));
                }
            }

            if (mistakes.size() > 0){
                if (TextUtils.isEmpty(mistakeInfo))
                    mistakeInfo = type;
                else
                    mistakeInfo = mistakeInfo + ", " + type;

                mistakenumber += mistakes.size();

                mistakeDtos.add(mistakes);
            }
        }

        if (mistakeDtos.size() == 0){
            mistakeInfoView.setText(getResources().getString(R.string.successtraining));
        }else {
            mistakeInfoView.setText(String.format(getResources().getString(R.string.mistakestotalmessage), mistakenumber, mistakeInfo));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPunches();
        loadInfo();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public Comparator<TrainingPunchDto> FORCE_COMPARATOR = new Comparator<TrainingPunchDto>() {
        @Override
        public int compare(TrainingPunchDto lhs, TrainingPunchDto rhs) {
            return (int) (rhs.mForce * 100 - lhs.mForce * 100);
        }
    };
}
