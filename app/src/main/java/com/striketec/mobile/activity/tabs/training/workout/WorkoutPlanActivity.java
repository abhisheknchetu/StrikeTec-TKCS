package com.striketec.mobile.activity.tabs.training.workout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.tabs.training.comboset.PlansTrainingActivity;
import com.striketec.mobile.adapters.PresetListAdapter;
import com.striketec.mobile.adapters.WorkoutListAdapter;
import com.striketec.mobile.dto.PresetDto;
import com.striketec.mobile.dto.WorkoutDto;
import com.striketec.mobile.dto.WorkoutRoundDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.ComboSetUtil;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class WorkoutPlanActivity extends BaseActivity {

    public static final int ROUND_TIME = 1;
    public static final int REST_TIME = 2;
    public static final int PREPARE_TIME = 3;
    public static final int WARNING_TIME = 4;

    @BindView(R.id.title)
    TextView titleView;

    @BindView(R.id.workoutbtn)
    Button workoutBtn;

    @BindView(R.id.workoutview)
    DiscreteScrollView workoutParentView;

    @BindView(R.id.totaltime) TextView totaltimeView;

    int workoutID = -1;

    int currentRoundTimePosition = 0;
    int currentRestPosition = 0;
    int currentPreparePosition = 0;
    int currentWarningPosition = 0;
    int currentGlovePosition = 0;

    int currentworkoutPosition = 0;

    int tmproundTimePosition = 0;
    int tmprestPosition = 0;
    int tmpprepareposition = 0;
    int tmpwarningPosition = 0;

    ArrayList<WorkoutRoundDto> roundDTOs = new ArrayList<>();
    ArrayList<WorkoutDto> workoutDTOs = new ArrayList<>();
    WorkoutDto workoutDto;

    WorkoutroundListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan);


        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.workoutplan));

        workoutDTOs = SharedUtils.getSavedWorkouts();

        adapter = new WorkoutroundListAdapter(this, roundDTOs);
        workoutParentView.setAdapter(adapter);

        workoutDto = workoutDTOs.get(0);
        currentRoundTimePosition = workoutDto.getRound();

        showWorkoutInfo(workoutDto);
    }

    @OnClick({R.id.starttraining, R.id.back, R.id.workoutbtn, R.id.editbtn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.starttraining:
                startTraining();
                break;

            case R.id.back:
                finish();
                break;

            case R.id.workoutbtn:
                showSavedWorkoutDialog();
                break;

            case R.id.editbtn:
                showPresetEditDialog();
                break;
        }
    }

    private void startTraining(){
        Intent trainingIntent = new Intent(this, PlansTrainingActivity.class);

        workoutDto.setRound(currentRoundTimePosition);
        workoutDto.setRest(currentRestPosition);
        workoutDto.setPrepare(currentPreparePosition);
        workoutDto.setWarning(currentWarningPosition);
        ComboSetUtil.updateWorkoutDto(workoutDto);
        SharedUtils.saveWorkoutList(workoutDTOs);

        trainingIntent.putExtra(AppConst.TRAININGTYPE, AppConst.WORKOUT);
        trainingIntent.putExtra(AppConst.WORKOUT_ID, workoutDto);
        startActivity(trainingIntent);
        finish();
    }

    private void showWorkoutInfo(WorkoutDto workoutDTO){
        workoutBtn.setText(workoutDTO.getName());

        roundDTOs.clear();

        for (int i = 0; i < workoutDTO.getRoundcount(); i++){
            WorkoutRoundDto workoutRoundDTO = new WorkoutRoundDto();
            workoutRoundDTO.setName("ROUND " + (i + 1));
            workoutRoundDTO.setComboIDLists(workoutDTO.getRoundsetIDs().get(i));
            roundDTOs.add(workoutRoundDTO);
        }

        workoutParentView.scrollToPosition(0);
        adapter.notifyDataSetChanged();

        currentRoundTimePosition = workoutDTO.getRound();
        currentWarningPosition = workoutDTO.getWarning();
        currentRestPosition = workoutDTO.getRest();
        currentPreparePosition = workoutDTO.getPrepare();
        currentGlovePosition = workoutDTO.getGlove();

        workoutID = workoutDTO.getId();

        setTotalTime();
    }

    private void showSavedWorkoutDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_presetpopup);

        ListView workoutListView = (ListView)dialog.findViewById(R.id.listview);
        final WorkoutListAdapter workoutListAdapter = new WorkoutListAdapter(this, workoutDTOs, currentworkoutPosition);
        workoutListView.setAdapter(workoutListAdapter);

        workoutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                workoutDto = workoutListAdapter.getItem(position);
                currentworkoutPosition = position;
                showWorkoutInfo(workoutDto);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showPresetEditDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_preset_time);

        final TextView roundTimeView, restTimeView, prepareTimeView, warningTimeView;
        LinearLayout roundParent, restParent, prepareParent, warningParent;
        Button cancelBtn, okBtn;

        tmproundTimePosition = currentRoundTimePosition;
        tmpprepareposition = currentPreparePosition;
        tmprestPosition = currentRestPosition;
        tmpwarningPosition = currentWarningPosition;

        roundTimeView = (TextView)dialog.findViewById(R.id.round_time);
        roundParent = (LinearLayout)dialog.findViewById(R.id.round_parent);
        roundParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWheelPicker(ROUND_TIME, roundTimeView);
            }
        });

        restTimeView = (TextView)dialog.findViewById(R.id.rest_time);
        restParent = (LinearLayout)dialog.findViewById(R.id.rest_parent);
        restParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWheelPicker(REST_TIME, restTimeView);
            }
        });

        prepareTimeView = (TextView)dialog.findViewById(R.id.prepare_time);
        prepareParent = (LinearLayout)dialog.findViewById(R.id.prepare_parent);
        prepareParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWheelPicker(PREPARE_TIME, prepareTimeView);
            }
        });

        warningTimeView = (TextView)dialog.findViewById(R.id.warning_time);
        warningParent = (LinearLayout)dialog.findViewById(R.id.warning_parent);
        warningParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWheelPicker(WARNING_TIME, warningTimeView);
            }
        });



        roundTimeView.setText(PresetUtil.timeList.get(currentRoundTimePosition));
        restTimeView.setText(PresetUtil.timeList.get(currentRestPosition));
        prepareTimeView.setText(PresetUtil.timeList.get(currentPreparePosition));
        warningTimeView.setText(PresetUtil.warningTimewithSecList.get(currentWarningPosition));

        cancelBtn = (Button) dialog.findViewById(R.id.cancel_btn);
        okBtn = (Button)dialog.findViewById(R.id.ok_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRoundTimePosition = tmproundTimePosition;
                currentPreparePosition = tmpprepareposition;
                currentRestPosition = tmprestPosition;
                currentWarningPosition = tmpwarningPosition;

                setTotalTime();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showWheelPicker(final int type, final TextView timeView){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wheelpicker);
        dialog.setCancelable(false);

        //set prepare picker
        WheelView wheelPicker = (WheelView)dialog.findViewById(R.id.picker);
        ArrayList dataList;

        int currentposition = 0;

        switch (type){
            case ROUND_TIME:
                dataList = PresetUtil.timeList;
                currentposition = currentRoundTimePosition;

                break;

            case REST_TIME:
                dataList = PresetUtil.timeList;
                currentposition = currentRestPosition;
                break;

            case PREPARE_TIME:
                dataList = PresetUtil.timeList;
                currentposition = currentPreparePosition;
                break;

            case WARNING_TIME:
                dataList = PresetUtil.warningTimewithSecList;
                currentposition = currentWarningPosition;
                break;
            default:
                dataList = PresetUtil.timeList;
        }

        ArrayWheelAdapter wheelAdapter = new ArrayWheelAdapter(this, dataList.toArray());

        wheelAdapter.setItemResource(R.layout.wheel_roundtime_text_item);
        wheelAdapter.setItemTextResource(R.id.text);
        wheelAdapter.setActiveTextColor(Color.parseColor(getResources().getString(R.string.prepare_select)));
        wheelAdapter.setDeactiveTextColor(Color.parseColor(getResources().getString(R.string.prepare_unselect)));
        wheelAdapter.setActiveTextSzie(getResources().getInteger(R.integer.wheel_roundtime_active));
        wheelAdapter.setDeactiveTextSize(getResources().getInteger(R.integer.wheel_roundtime_deactive));
        wheelPicker.setViewAdapter(wheelAdapter);
        wheelPicker.setVisibleItems(3);

        wheelPicker.setCurrentItem(currentposition);

        wheelPicker.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                switch (type) {
                    case ROUND_TIME:
                        tmproundTimePosition = newValue;
                        timeView.setText(PresetUtil.timeList.get(newValue));
//                        setTotalTime();
                        break;

                    case REST_TIME:
                        tmprestPosition = newValue;
                        timeView.setText(PresetUtil.timeList.get(newValue));
//                        setTotalTime();
                        break;

                    case PREPARE_TIME:
                        tmpprepareposition = newValue;
                        timeView.setText(PresetUtil.timeList.get(newValue));
//                        setTotalTime();
                        break;

                    case WARNING_TIME:
                        tmpwarningPosition = newValue;
                        timeView.setText(PresetUtil.warningTimewithSecList.get(newValue));
                        break;
                }
            }
        });

        TextView setBtn = (TextView)dialog.findViewById(R.id.okbtn);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void setTotalTime(){
        int round = 0;
        if (roundDTOs.size() > 1) {
            round = roundDTOs.size() - 1;

            int roundtime = Integer.parseInt(PresetUtil.timerwitSecsList.get(currentRoundTimePosition));
            int resttime = Integer.parseInt(PresetUtil.timerwitSecsList.get(currentRestPosition));
            int preparetime = Integer.parseInt(PresetUtil.timerwitSecsList.get(currentPreparePosition));

            String totalTime = PresetUtil.changeSecondsToHours(round * (roundtime + resttime) + preparetime - resttime);
            totaltimeView.setText(totalTime);
        }else {
            round = 0;
            totaltimeView.setText(PresetUtil.changeSecondsToHours(0));
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
