package com.striketec.mobile.activity.tabs.training.sensortest;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.TrainingActivity;
import com.striketec.mobile.dto.TrainingBatteryLayoutDTO;
import com.striketec.mobile.dto.TrainingBatteryVoltageDTO;
import com.striketec.mobile.dto.TrainingConnectStatusDTO;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SensorTestActivity extends TrainingActivity {

    public static final String TAG = SensorTestActivity.class.getSimpleName();
    private static SensorTestActivity instance;
    @BindView(R.id.leftsensorid)
    EditText leftsensoridView;
    @BindView(R.id.rightsensorid)
    EditText rightsensoridView;
    @BindView(R.id.leftsensorconnect)
    Button leftsensorconnectBtn;
    @BindView(R.id.rightsensorconnect)
    Button rightsensorconnectBtn;
    @BindView(R.id.starttraining)
    Button starttrainingBtn;
    @BindView(R.id.forceview)
    TextView forceView;
    @BindView(R.id.speedview)
    TextView speedView;
    @BindView(R.id.countview)
    TextView countView;
    int totalcount;
    String leftsensorId, rightsensorId;

    public static SensorTestActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();

        instance = this;
    }

    @OnClick({R.id.leftsensorconnect, R.id.rightsensorconnect, R.id.starttraining})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.leftsensorconnect:
                connectSensor(true);
                break;
            case R.id.rightsensorconnect:
                connectSensor(false);
                break;
            case R.id.starttraining:
                if (starttrainingBtn.getText().toString().trim().equals(getResources().getString(R.string.starttraining))) {

                    if (!leftSensorConnected && !rightSensorConnected) {
                        CommonUtils.showToastMessage("Please connect with sensors");
                        return;
                    }

                    starttrainingBtn.setText(getResources().getString(R.string.stoptraining));

                    startRoundTraining();

                } else {
                    stopRoundTraining();
                    starttrainingBtn.setText(getResources().getString(R.string.starttraining));
                }
                break;
        }
    }

    private void connectSensor(boolean isLeft) {
        deviceLeft = leftsensoridView.getText().toString().trim();
        deviceRight = rightsensoridView.getText().toString().trim();

        if (TextUtils.isEmpty(deviceLeft) && TextUtils.isEmpty(deviceRight)) {
            CommonUtils.showToastMessage(AppConst.DEVICE_ID_MUST_NOT_BE_BLANK_ERROR);
        } else if (deviceLeft.equalsIgnoreCase(deviceRight)) {
            CommonUtils.showToastMessage(AppConst.DEVICE_ID_MUST_NOT_BE_SAME);
        } else {

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, AppConst.REQUEST_ENABLE_BT);
            }

            if (AppConst.DEBUG)
                Log.e(TAG, "start connect sensor");

            if (isLeft) {
                new ShowLoaderTask(this).execute("left");
            } else {
                new ShowLoaderTask(this).execute("right");
            }
        }
    }


    @Override
    public void setDataToLiveMonitorMap(String liveMonitorData) {
        JSONObject punchDataJson = null;
        try {
            JSONObject roundDetailsJson = new JSONObject(liveMonitorData);
            if (roundDetailsJson.getString("success").equals("true")) {
                punchDataJson = roundDetailsJson.getJSONObject("jsonObject");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String punchType;

        try {
            String speed = punchDataJson.getString("speed");
            String force = punchDataJson.getString("force");

            forceView.setText(force);
            speedView.setText(speed);
            String count = String.valueOf(Integer.parseInt(countView.getText().toString().trim()) + 1);
            countView.setText(count);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlingConnectionToastResponse(Response response, Message msg) {
        try {
            switch (response) {
                case success:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(AppConst.TOAST), Toast.LENGTH_SHORT).show();
                    deviceConnectionSuccess(msg);

                    if (msg.getData().getString(AppConst.CONNECTED_DEVICE_TEXT).equals(CommonUtils.getMacAddress(deviceRight))) {
                        rightsensorconnectBtn.setText(getResources().getString(R.string.connected));
                    }

                    if (msg.getData().getString(AppConst.CONNECTED_DEVICE_TEXT).equals(CommonUtils.getMacAddress(deviceLeft))) {
                        leftsensorconnectBtn.setText(getResources().getString(R.string.connected));
                    }

                    break;

                case unsuccess:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(AppConst.TOAST), Toast.LENGTH_SHORT).show();
                    if (msg.getData().getString("DeviceAddress").equals(CommonUtils.getMacAddress(deviceRight))) {

                        EventBus.getDefault().post(new TrainingBatteryLayoutDTO(false, true));

                        isDeviceRightConnectionFinish = true;
                        checkDeviceConnectionFlag++;
                        rightHandConnectionThread = null;
                        rightSensorConnected = false;
                        initiateTraining();

                    } else if (msg.getData().getString("DeviceAddress").equals(CommonUtils.getMacAddress(deviceLeft))) {

                        EventBus.getDefault().post(new TrainingBatteryLayoutDTO(true, true));

                        isDeviceLeftConnectionFinish = true;
                        checkDeviceConnectionFlag++;
                        leftHandConnectionThread = null;
                        leftSensorConnected = false;
                        initiateTraining();
                    }

                    EventBus.getDefault().post(new TrainingConnectStatusDTO(false, true, msg.getData().getString("HAND").toString()));

                    break;

                case closed:
                    isDeviceLeftConnectionFinish = false;
                    isDeviceRightConnectionFinish = false;
                    checkDeviceConnectionFlag = 0;

                    if (msg.getData().getString("HAND").toString().equals("left")) {
                        leftHandBatteryVoltage = "";
                        leftSensorConnected = false;
                    } else {
                        rightHandBatteryVoltage = "";
                        rightSensorConnected = false;
                    }

                    EventBus.getDefault().post(new TrainingBatteryVoltageDTO(true, msg.getData().getString("HAND").toString(), ""));
                    EventBus.getDefault().post(new TrainingConnectStatusDTO(false, true, msg.getData().getString("HAND").toString()));

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        forceView.setText("0");
        speedView.setText("0");
        countView.setText("0");

        starttrainingBtn.setText(getResources().getString(R.string.starttraining));

    }


}
