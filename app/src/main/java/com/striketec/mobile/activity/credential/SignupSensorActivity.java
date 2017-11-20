package com.striketec.mobile.activity.credential;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.adapters.SensorListAdapter;
import com.striketec.mobile.dto.AuthResponseDto;
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

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class SignupSensorActivity extends AppCompatActivity {

    public static final String TAG = SignupSensorActivity.class.getSimpleName();


    private enum STATUS{
        MAIN,
        SPECTATOR,
        SEARCH_DEVICE,
        CONNECT_DEVICE,
        ALLCONNECTED,
    }

    @BindView(R.id.sensorparent) LinearLayout sensorParentView;
    @BindView(R.id.main) LinearLayout sensorMainView;
    @BindView(R.id.searchdevices) LinearLayout sensorSearchView;
    @BindView(R.id.connectsensorview) LinearLayout sensorConnectView;

    @BindView(R.id.spectatorparent) LinearLayout spectatorParentView;
    @BindView(R.id.sensorcontrolparent) RelativeLayout sensorControlView;
    @BindView(R.id.nextparent) LinearLayout sensornextParent;
    @BindView(R.id.cancelparent) LinearLayout sensorcancelParent;
    @BindView(R.id.spectornextparent) LinearLayout spectatorNextParent;
    @BindView(R.id.next) Button sensorNextBtn;
    @BindView(R.id.cancel) Button sensorCancelBtn;
    @BindView(R.id.spectatornext) Button spectatorNextBtn;
    @BindView(R.id.startspectator) TextView startSpectatorView;
    @BindView(R.id.devicelist) ListView deviceListView;
    @BindView(R.id.foundsensors) TextView foundSensors;

    @BindView(R.id.progressimage) ImageView progressImageView;

    STATUS status = STATUS.MAIN;

    SensorListAdapter sensorListAdapter;

    ArrayList<SensorDTO> sensorDTOs = new ArrayList<>();

    BluetoothAdapter bluetoothAdapter = null;
    ProgressDialog dialog;

    AnimationDrawable frameAnimation;

    UserDto userDto;
    String leftSensorMac = "";
    String rightSensorMac = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_sensor);

        userDto = (UserDto)getIntent().getSerializableExtra(AppConst.USER_INTENT);

        if (userDto == null)
            userDto = new UserDto();


        ButterKnife.bind(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sensorListAdapter = new SensorListAdapter(this, sensorDTOs);
        deviceListView.setAdapter(sensorListAdapter);

        updateView();
    }

    @OnClick({R.id.startspectator, R.id.spectator_close, R.id.next, R.id.cancel, R.id.spectatornext, R.id.spectator_getsensor, R.id.getsensor})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.startspectator:
                status = STATUS.SPECTATOR;
                updateView();
                break;

            case R.id.spectator_close:
                status = STATUS.MAIN;
                updateView();
                break;

            case R.id.next:
                actionSensorContinue();
                break;

            case R.id.cancel:
                actionSensorCancel();
                break;

            case R.id.spectatornext:
                updateProfile(true);
                break;

            case R.id.spectator_getsensor:
                CommonUtils.showToastMessage("Go to shop website");
                break;

            case R.id.getsensor:
                CommonUtils.showToastMessage("Go to shop website");
                break;
        }
    }

    private void updateProfile(boolean isspectator){
        if (CommonUtils.isOnline()){

            Map<String, Object> params = new HashedMap();
            params.put("gender", userDto.mGender.toLowerCase());
            params.put("stance", userDto.mStance);
            params.put("birthday", DatesUtil.changeDateFormat1(userDto.mBirthday));
            params.put("weight", userDto.mWeight);
            params.put("height", userDto.mHeight);
            params.put("skill_level", userDto.mSkillLevel);
            params.put("country_id", userDto.mCountry.mId);
            params.put("city_id", userDto.mCity.mId);
            params.put("state_id", userDto.mState.mId);
            params.put("is_spectator", isspectator);

            if (!isspectator){
                params.put("left_hand_sensor", leftSensorMac);
                params.put("right_hand_sensor", rightSensorMac);
            }
Log.e("Super", "getheader =" + SharedUtils.getHeader() + "   " + isspectator);
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

                            gotoSigninActivity();

                        }else {
                            if (!TextUtils.isEmpty(responseDto.mMessage))
                                CommonUtils.showAlert(SignupSensorActivity.this, responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(SignupSensorActivity.this, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    private void actionSensorContinue(){
        if (status == STATUS.MAIN) {
            searchSensor();
        } else if (status == STATUS.CONNECT_DEVICE){
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

            updateProfile(false);
        }
    }

    private void actionSensorCancel (){
        if (status == STATUS.SEARCH_DEVICE){
            status = STATUS.CONNECT_DEVICE;
            updateView();

        }else if (status == STATUS.CONNECT_DEVICE){
            status = STATUS.MAIN;
            updateView();
        }
    }

    private void updateView(){
        if (status == STATUS.SPECTATOR){
            spectatorParentView.setVisibility(View.VISIBLE);
            sensorControlView.setVisibility(View.INVISIBLE);

            startSpectatorView.setClickable(false);

        } else if (status == STATUS.MAIN){
            spectatorParentView.setVisibility(View.INVISIBLE);
            sensorControlView.setVisibility(View.VISIBLE);

            sensorMainView.setVisibility(View.VISIBLE);
            sensorSearchView.setVisibility(View.INVISIBLE);
            sensorConnectView.setVisibility(View.INVISIBLE);

            sensornextParent.setVisibility(View.VISIBLE);
            sensorNextBtn.setText(getResources().getString(R.string.searchforneardevice));
            sensorcancelParent.setVisibility(View.INVISIBLE);

            startSpectatorView.setClickable(true);
            startSpectatorView.setVisibility(View.VISIBLE);
        } else if (status == STATUS.SEARCH_DEVICE){
            sensorMainView.setVisibility(View.INVISIBLE);
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

            startSpectatorView.setVisibility(View.INVISIBLE);
        }else if (status == STATUS.CONNECT_DEVICE){
            sensorMainView.setVisibility(View.INVISIBLE);
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
                        status = STATUS.MAIN;
                        updateView();
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

    private void gotoSignupPlanActivity(){
        Intent planIntent = new Intent(this, SignupTrainingPlanActivity.class);
        planIntent.putExtra(AppConst.USER_INTENT, userDto);
        startActivity(planIntent);
        finish();
    }

    private void gotoSigninActivity(){
        Intent signinIntent = new Intent(this, SigninActivity.class);
        signinIntent.putExtra(SharedUtils.EMAIl, userDto.mEmail);
        startActivity(signinIntent);
        finish();
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

//        cancelSearch();
    }

    @Override
    public void onBackPressed() {
        gotoSignupPlanActivity();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
