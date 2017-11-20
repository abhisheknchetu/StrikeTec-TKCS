package com.striketec.mobile.activity.settings.sensor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.credential.SigninActivity;
import com.striketec.mobile.activity.credential.SignupTrainingPlanActivity;
import com.striketec.mobile.adapters.SensorListAdapter;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.SensorDTO;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.SharedUtils;

import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class SearchSensorActivity extends AppCompatActivity {

    public static final String TAG = SearchSensorActivity.class.getSimpleName();

    private static final int LOCATION_PERMISSION = 101;
    private static final int REQUEST_ENABLE_BT = 0;

    private enum STATUS{
        SEARCH_DEVICE,
        CONNECT_DEVICE,
    }

    @BindView(R.id.searchdevices) LinearLayout sensorSearchView;
    @BindView(R.id.connectsensorview) LinearLayout sensorConnectView;

    @BindView(R.id.sensorcontrolparent) RelativeLayout sensorControlView;
    @BindView(R.id.nextparent) LinearLayout sensornextParent;
    @BindView(R.id.cancelparent) LinearLayout sensorcancelParent;
    @BindView(R.id.next) Button sensorNextBtn;
    @BindView(R.id.devicelist) ListView deviceListView;
    @BindView(R.id.foundsensors) TextView foundSensors;

    @BindView(R.id.progressimage) ImageView progressImageView;

    STATUS status = STATUS.SEARCH_DEVICE;

    SensorListAdapter sensorListAdapter;

    ArrayList<SensorDTO> sensorDTOs = new ArrayList<>();

    BluetoothAdapter bluetoothAdapter = null;
    ProgressDialog dialog;

    AnimationDrawable frameAnimation;

    String leftSensorMac = "";
    String rightSensorMac = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sensor);

        ButterKnife.bind(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sensorListAdapter = new SensorListAdapter(this, sensorDTOs);
        deviceListView.setAdapter(sensorListAdapter);

        searchSensor();
    }

    @OnClick({R.id.next, R.id.cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.next:
                actionSensorContinue();
                break;

            case R.id.cancel:
                finish();
                break;
        }
    }

    private void updateProfile(){
        if (CommonUtils.isOnline()){

            Map<String, Object> params = new HashedMap();

            params.put("left_hand_sensor", leftSensorMac);
            params.put("right_hand_sensor", rightSensorMac);


            RetrofitSingleton.USER_REST.updateUser(SharedUtils.getHeader(), params).enqueue(new IndicatorCallback<DefaultResponseDto>(this) {
                @Override
                public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        DefaultResponseDto responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage);

                            if (!TextUtils.isEmpty(responseDto.mMessage))
                                CommonUtils.showToastMessage(responseDto.mMessage);

                        }else {
                            if (!TextUtils.isEmpty(responseDto.mMessage))
                                CommonUtils.showAlert(SearchSensorActivity.this, responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(SearchSensorActivity.this, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    private void actionSensorContinue(){
        if (status == STATUS.CONNECT_DEVICE){
            ArrayList<SensorDTO> sensorDTOs = sensorListAdapter.getSensorDTOs();

            leftSensorMac = "";
            rightSensorMac = "";

            for (int i = 0; i < sensorDTOs.size(); i++){
                if (sensorDTOs.get(i).isSet && sensorDTOs.get(i).isLeft){
                    leftSensorMac = sensorDTOs.get(i).bluetoothDevice.getAddress();
                    if (AppConst.DEBUG){
                        Log.e(TAG, "left sensor name and address = " + sensorDTOs.get(i).bluetoothDevice.getName() + "    :  " + sensorDTOs.get(i).bluetoothDevice.getAddress());

                    }
                }

                if (sensorDTOs.get(i).isSet && !sensorDTOs.get(i).isLeft){
                    rightSensorMac = sensorDTOs.get(i).bluetoothDevice.getAddress();
                    if (AppConst.DEBUG){
                        Log.e(TAG, "right sensor name and address = " + sensorDTOs.get(i).bluetoothDevice.getName() + "    :  " + sensorDTOs.get(i).bluetoothDevice.getAddress());
                    }
                }
            }

            updateProfile();
        }
    }


    private void updateView(){
        if (status == STATUS.SEARCH_DEVICE){
            sensorSearchView.setVisibility(View.VISIBLE);
            sensorConnectView.setVisibility(View.INVISIBLE);

            if (frameAnimation == null) {
                frameAnimation = (AnimationDrawable) progressImageView.getDrawable();
                frameAnimation.start();
            }

            // Start the animation (looped playback by default).
            frameAnimation.start();

            sensorcancelParent.setVisibility(View.VISIBLE);
            sensornextParent.setVisibility(View.INVISIBLE);

        }else if (status == STATUS.CONNECT_DEVICE){
            sensorSearchView.setVisibility(View.INVISIBLE);
            sensorConnectView.setVisibility(View.VISIBLE);

            sensorcancelParent.setVisibility(View.INVISIBLE);
            sensornextParent.setVisibility(View.VISIBLE);
            sensorNextBtn.setText(getResources().getString(R.string.continues));
        }
    }

    private boolean deviceNameExistsInList(final String deviceName) {
        boolean result = false;
        if (null != deviceName) {
            for (String name : AppConst.MMAGLOVE_PREFIX) {
                if (deviceName.startsWith(name)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @SuppressLint("NewApi")
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    CommonUtils.showToastMessage("Start Search Sensor");
                }

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    try {
                        boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                        for (int i = 0; i < sensorDTOs.size(); i++) {
                            if (device.getAddress().equalsIgnoreCase(sensorDTOs.get(i).bluetoothDevice.getAddress())) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag == true) {
                            if (!TextUtils.isEmpty(device.getName()) && deviceNameExistsInList(device.getName())) {
                                CommonUtils.showToastMessage(String.format(getResources().getString(R.string.sensorfoundmsg), device.getName()));

                                sensorDTOs.add(new SensorDTO(device, false, false, false));

                                if (sensorDTOs.size() == AppConst.SENSOR_LIMIT){
                                    unregisterReceiver(myReceiver);

                                    status = STATUS.CONNECT_DEVICE;
                                    updateView();

                                    foundSensors.setText(String.format(getResources().getString(R.string.founddevices), String.valueOf(sensorDTOs.size())));

                                    if (frameAnimation != null) {
                                        frameAnimation.stop();
                                        frameAnimation = null;
                                    }

                                    sensorListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.i("Log", "Inside the exception: ");
                        e.printStackTrace();
                    }

                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    if (AppConst.DEBUG)
                        Log.e(TAG, "Searching is Finished");

                    CommonUtils.showToastMessage(getResources().getString(R.string.scaningfinished));

                    stopLoader();

                    if (sensorDTOs.size() == 0) {
                        CommonUtils.showToastMessage(getResources().getString(R.string.sensornotfound));
                        if (frameAnimation != null) {
                            frameAnimation.stop();
                            frameAnimation = null;
                        }

                        finish();
                    }else {

                        status = STATUS.CONNECT_DEVICE;
                        updateView();
                        foundSensors.setText(String.format(getResources().getString(R.string.founddevices), String.valueOf(sensorDTOs.size())));

                        if (frameAnimation != null) {
                            frameAnimation.stop();
                            frameAnimation = null;
                        }

                        sensorListAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void searchSensor(){

        status = STATUS.SEARCH_DEVICE;
        updateView();

        sensorDTOs.clear();

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }

    private void cancelSearch(){
        unregisterReceiver(myReceiver);
    }

    private void showLoader(String message) {
        if (dialog == null) {
            dialog = ProgressDialog.show(this, message, "Loading..", true);
            dialog.show();
        }
    }

    private void stopLoader() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
