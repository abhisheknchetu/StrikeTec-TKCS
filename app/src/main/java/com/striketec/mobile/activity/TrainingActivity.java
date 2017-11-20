package com.striketec.mobile.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.tabs.MainActivity;

import com.striketec.mobile.dto.TrainingBatteryLayoutDTO;
import com.striketec.mobile.dto.TrainingBatteryVoltageDTO;
import com.striketec.mobile.dto.TrainingConnectStatusDTO;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DBAdapter;
import com.striketec.mobile.util.TrainingManager;
import com.striketec.mobile.util.sensormanager.bluetooth.connection.ConnectionManager;
import com.striketec.mobile.util.sensormanager.bluetooth.connection.ConnectionThread;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.PunchDetectionConfig;
import com.striketec.mobile.util.sensormanager.mmaGlove.EffectivePunchMassCalculator;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Qiang on 8/23/2017.
 */

public abstract class TrainingActivity extends BaseActivity {

    public static final String TAG = TrainingActivity.class.getSimpleName();

    public static double boxerPunchMassEffect;
    public  boolean receivePunchable = false;

    private static TrainingActivity instance;
    public static TrainingActivity getInstance(){
        return instance;
    }

    public String trainingSessionStartTime = "";
    public String trainingRoundStartTime = "";

    public boolean isDeviceLeftConnectionFinish;
    public boolean isDeviceRightConnectionFinish;
    public int checkDeviceConnectionFlag = 0;
    public String deviceLeft = "";
    public String deviceRight = "";
    public boolean leftSensorConnected = false;
    public boolean rightSensorConnected = false;

    public ConnectionThread leftHandConnectionThread = null;
    public ConnectionThread rightHandConnectionThread = null;
    public static ConnectionManager rightDeviceConnectionManager, leftDeviceConnectionManager;
    public static String leftHandBatteryVoltage = "", rightHandBatteryVoltage = "";

    public String boxerName = "Tester";
    public String boxerStance = AppConst.TRADITIONAL;

    public TrainingManager trainingManager = new TrainingManager();
    public ProgressDialog dialog;

    public DBAdapter dbAdapter;

    public enum Response {
        success, unsuccess, closed;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, AppConst.REQUEST_ENABLE_BT);
        }

        setDeviceHandlers();
        dbAdapter = DBAdapter.getInstance(this);
        instance = this;
    }

    public class ShowLoaderTask extends AsyncTask<String, Integer, String> {

        Activity activity;

        public ShowLoaderTask(Activity activity){
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {

            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            dialog = new ProgressDialog(activity);
            dialog.setMessage("Please wait...");
            dialog.setTitle("Connecting With Device...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String sensor = params[0];

            if (sensor.equalsIgnoreCase("left")) {
                startDeviceConnection(true);
            }else if (sensor.equalsIgnoreCase("right")){
                startDeviceConnection(false);
            }else {
                startDeviceConnection(false);
            }

            return "Done!";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    } // end ShowLoaderTask

    public double calculateBoxerPunchMassEffect(HashMap<String, String> boxerDetails) {

        double effectivePunchMass = AppConst.GUEST_TRAINING_EFFECTIVE_PUNCH_MASS;

        String weightCountValue = "150";
        String traineeSkillLevelValue = "Beginner";
        String gender = "male";
        EffectivePunchMassCalculator calculator = new EffectivePunchMassCalculator();
        effectivePunchMass = calculator.calculatePunchMassEffect(weightCountValue, traineeSkillLevelValue, gender);

        return effectivePunchMass;
    }

    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConst.MESSAGE_WRITE:

                    if (msg.getData().containsKey("jsonData")) {
                        try {
                            if (trainingManager.isTrainingRunning()) {
                                JSONObject punchDataJson = new JSONObject(msg.getData().getString("jsonData"));
                                setDataToLiveMonitorMap(punchDataJson.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (msg.getData().containsKey("batteryVoltage")) {

                        String batteryVoltageString = msg.getData().getString("batteryVoltage");
                        try {
                            JSONObject batteryJson = new JSONObject(batteryVoltageString);
                            if (batteryJson.getString("hand").equals("left")) {
                                leftHandBatteryVoltage = batteryVoltageString;
                            } else {
                                rightHandBatteryVoltage = batteryVoltageString;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        EventBus.getDefault().post(new TrainingBatteryVoltageDTO(false, "", batteryVoltageString));
                    }
                    break;

                case AppConst.MESSAGE_TOAST:

                    try {
                        setDeviceConnectionFinishFlag();
                        Response response = Response.valueOf(msg.getData().getString("CONNECTION"));
                        Log.e(TAG, "respone  = " + response + "    " + msg);
                        handlingConnectionToastResponse(response, msg);
                        initiateTraining();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void setDeviceHandlers() {
        if (!trainingManager.isTrainingRunning()) {
            MainActivity.rightDeviceConnectionManager = new ConnectionManager(mHandler);
            MainActivity.leftDeviceConnectionManager = new ConnectionManager(mHandler);
        }
    }

    public void initiateTraining() {

        try {
            if (isDeviceRightConnectionFinish || isDeviceLeftConnectionFinish) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Flags are used for start match after both device get connected or not connected
    private void setDeviceConnectionFinishFlag() {
        if (checkDeviceConnectionFlag >= 2) {
            isDeviceLeftConnectionFinish = false;
            isDeviceRightConnectionFinish = false;
            checkDeviceConnectionFlag = 0;
        }
        if (TextUtils.isEmpty(deviceLeft)) {
            isDeviceLeftConnectionFinish = true;
            checkDeviceConnectionFlag++;
        }
        if (TextUtils.isEmpty(deviceRight)) {
            isDeviceRightConnectionFinish = true;
            checkDeviceConnectionFlag++;
        }
    }

    public void startTrainingSession (String trainingType, int planId){
        if (!trainingManager.isTrainingRunning()){
            trainingManager.startTraining();

            trainingSessionStartTime = dbAdapter.insertTrainingSession(StriketecApp.currentUser.mId, trainingType, planId);

            boxerPunchMassEffect = calculateBoxerPunchMassEffect(null);

            if (PunchDetectionConfig.getInstance().getPunchMassEff() != boxerPunchMassEffect) {
                Log.e(TAG, "Resetting Punch mass effect = " + boxerPunchMassEffect + " (" + PunchDetectionConfig.getInstance().getPunchMassEff() + ")");
                PunchDetectionConfig.getInstance().setPunchMassEff(boxerPunchMassEffect);
            }

            if (leftHandConnectionThread != null) {
                setConnectionManagerConnected(leftHandConnectionThread, boxerName, boxerStance, AppConst.LEFT_HAND, trainingSessionStartTime);
            }
            if (rightHandConnectionThread != null) {
                setConnectionManagerConnected(rightHandConnectionThread, boxerName, boxerStance, AppConst.RIGHT_HAND, trainingSessionStartTime);
            }

            EventBus.getDefault().post(new TrainingConnectStatusDTO(true, false, ""));
        }
    }

    public void startRoundTraining(){
        if (!TextUtils.isEmpty(trainingSessionStartTime) && TextUtils.isEmpty(trainingRoundStartTime)){
            receivePunchable = true;

            trainingRoundStartTime = dbAdapter.insertTrainingRound(StriketecApp.currentUser.mId, trainingSessionStartTime);
        }
    }

    public void stopRoundTraining(){
        if (!TextUtils.isEmpty(trainingRoundStartTime)){
            receivePunchable = false;

            dbAdapter.endTrainingRound(StriketecApp.currentUser.mId, trainingSessionStartTime, trainingRoundStartTime);
            trainingRoundStartTime = "";
        }
    }

    public void stopTrainingSession(){
        if (trainingManager.isTrainingRunning()){
            trainingManager.stopTraining();

            dbAdapter.endTrainingSession(StriketecApp.currentUser.mId, trainingSessionStartTime);

            trainingSessionStartTime = "";

            if (leftDeviceConnectionManager != null){
                leftDeviceConnectionManager.stopWriteCSV();
            }

            if (rightDeviceConnectionManager != null){
                rightDeviceConnectionManager.stopWriteCSV();
            }
        }
    }

    private void setConnectionManagerConnected(ConnectionThread connectionThread, String boxerName, String boxerStance, String boxerHand, String sessionStartTime) {
        ConnectionManager connectionManager = connectionThread.getBluetoothConnectionManager();
        connectionManager.setBoxerName(boxerName);
        connectionManager.setBoxerStance(boxerStance);
        connectionManager.setBoxerHand(boxerHand);
        connectionManager.setSessionStartTime(sessionStartTime);
        connectionManager.connected(connectionThread.getBluetoothSocket());
    }

    public void startDeviceConnection(boolean isLeft) {
        if (!isLeft)
            rightDeviceConnectionManager.connect(deviceRight);
        else
            leftDeviceConnectionManager.connect(deviceLeft);//CommonUtils.getMacAddress(deviceLeft)
    }

    public void deviceConnectionSuccess(Message msg) {

        if (msg.getData().getString(AppConst.CONNECTED_DEVICE_TEXT).equals(CommonUtils.getMacAddress(deviceLeft))) {
            isDeviceLeftConnectionFinish = true;
            leftHandConnectionThread = leftDeviceConnectionManager.getConnectionThread();

            //left device is connected
            leftSensorConnected = true;

			/*for reconnection device */
            if (trainingManager.isTrainingRunning()) {
                setConnectionManagerConnected(leftHandConnectionThread, boxerName, boxerStance, AppConst.LEFT_HAND, trainingSessionStartTime);
            }

            EventBus.getDefault().post(new TrainingBatteryLayoutDTO(true, false));
        }

        if (msg.getData().getString(AppConst.CONNECTED_DEVICE_TEXT).equals(CommonUtils.getMacAddress(deviceRight))) {
            isDeviceRightConnectionFinish = true;
            rightHandConnectionThread = rightDeviceConnectionManager.getConnectionThread();

            rightSensorConnected = true;

			/*for reconnection device */
            if (trainingManager.isTrainingRunning()) {
                setConnectionManagerConnected(rightHandConnectionThread, boxerName, boxerStance, AppConst.RIGHT_HAND, trainingSessionStartTime);
            }

            EventBus.getDefault().post(new TrainingBatteryLayoutDTO(false, false));
        }
    }

    public void disconnectSensors(){
        leftDeviceConnectionManager.disconnect();
        rightDeviceConnectionManager.disconnect();
        rightHandConnectionThread = null;
        leftHandConnectionThread = null;
    }

    public void reconnectSensor(boolean isLeft) {
        if (isLeft){
            if (leftDeviceConnectionManager.readerThread == null && trainingManager.isTrainingRunning()) {

                EventBus.getDefault().post(new TrainingBatteryLayoutDTO(true, false));

                if (!TextUtils.isEmpty(deviceLeft)) {
                    leftDeviceConnectionManager = new ConnectionManager(mHandler);
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, AppConst.REQUEST_ENABLE_BT);
                    }

                    CommonUtils.showToastMessage(getResources().getString(R.string.left_sensor_connect));

                    leftDeviceConnectionManager.connect(CommonUtils.getMacAddress(deviceLeft));

                } else {
                }
            }
        }else {
            if (rightDeviceConnectionManager.readerThread == null && trainingManager.isTrainingRunning()) {

                EventBus.getDefault().post(new TrainingBatteryLayoutDTO(false, false));

                if (!TextUtils.isEmpty(deviceRight)) {
                    rightDeviceConnectionManager = new ConnectionManager(mHandler);
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, AppConst.REQUEST_ENABLE_BT);
                    }

                    CommonUtils.showToastMessage(getResources().getString(R.string.right_sensor_connect));

                    rightDeviceConnectionManager.connect(CommonUtils.getMacAddress(deviceRight));

                } else {
                }
            }
        }
    }

    public abstract void setDataToLiveMonitorMap(String liveMonitorMap);

    public abstract void handlingConnectionToastResponse(Response response, Message message);

}
