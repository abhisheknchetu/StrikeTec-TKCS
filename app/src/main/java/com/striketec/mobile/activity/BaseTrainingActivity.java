package com.striketec.mobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.striketec.mobile.dto.TrainingBatteryLayoutDTO;
import com.striketec.mobile.dto.TrainingBatteryVoltageDTO;
import com.striketec.mobile.dto.TrainingConnectStatusDTO;
import com.striketec.mobile.dto.DBTrainingPunchDto;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Qiang on 8/24/2017.
 */

public class BaseTrainingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DBTrainingPunchDto punchDto) {
        receivedPunchData(punchDto);
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TrainingConnectStatusDTO connectStatusDTO) {
        if (connectStatusDTO.getUpdateConnectStatus()){
            setDeviceConnectionState();
        }else {
            resetConnectDeviceBg(connectStatusDTO.getHand());
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TrainingBatteryLayoutDTO batteryLayoutDTO) {
        handleBatteryLayoutClickable(batteryLayoutDTO.getIsLeft(), batteryLayoutDTO.getClickage());
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TrainingBatteryVoltageDTO batteryVoltageDTO) {
        if (batteryVoltageDTO.getResetVoltage()){
            resetBatteryVoltage(batteryVoltageDTO.getHand());
        }else {
            setBatteryVoltage(batteryVoltageDTO.getVoltage());
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void receivedPunchData(DBTrainingPunchDto punchDto){};
    public void setDeviceConnectionState (){};
    public void setBatteryVoltage(String voltage){};
    public void handleBatteryLayoutClickable(boolean isLeft, boolean clickable){};
    public void resetConnectDeviceBg (String hand){};
    public void resetBatteryVoltage(String hand){};
}
