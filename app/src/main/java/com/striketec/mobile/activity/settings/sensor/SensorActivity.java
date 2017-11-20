package com.striketec.mobile.activity.settings.sensor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SensorActivity extends BaseActivity {


    private static final int LOCATION_PERMISSION = 101;
    private static final int REQUEST_ENABLE_BT = 0;

    @BindView(R.id.title)
    TextView titleView;

    @BindView(R.id.left_parent) LinearLayout leftparentView;
    @BindView(R.id.right_parent) LinearLayout rightparentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.sensors));
    }

    @OnClick({R.id.back, R.id.next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.next:
                actionNext();
                break;
        }
    }

    private void actionNext(){
        int hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
            return;
        }

        BluetoothAdapter bluetoothAdapter = null;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            return;
        }

        Intent searchIntent = new Intent(SensorActivity.this, SearchSensorActivity.class);
        startActivity(searchIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateViews(true, leftparentView, generateSensor());
        updateViews(false, rightparentView, generateSensor());
    }

    private void updateViews(boolean isLeft, View view, SensorValueDto sensorValueDto){
        SensorViewHolder sensorViewHolder = new SensorViewHolder(view);

        if (isLeft)
            sensorViewHolder.handView.setText(getResources().getString(R.string.leftarm));
        else
            sensorViewHolder.handView.setText(getResources().getString(R.string.rightarm));

        if (sensorValueDto.connected){
            sensorViewHolder.statusView.setText(getResources().getString(R.string.connected));
            sensorViewHolder.statusView.setTextColor(getResources().getColor(R.color.speed_color));

            sensorViewHolder.batteryProgressBar.setMax(100);
            sensorViewHolder.batteryProgressBar.setProgress(sensorValueDto.battery);
            sensorViewHolder.batterValueView.setText(sensorValueDto.battery + "%");

            if (sensorValueDto.battery < 30){
                sensorViewHolder.batteryProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_belowgoalbar));
                sensorViewHolder.batterValueView.setTextColor(getResources().getColor(R.color.force_color));
            }else if (sensorValueDto.battery < 60){
                sensorViewHolder.batteryProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_averagegoalbar));
                sensorViewHolder.batterValueView.setTextColor(getResources().getColor(R.color.orange));
            }else {
                sensorViewHolder.batteryProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_abovegoalbar));
                sensorViewHolder.batterValueView.setTextColor(getResources().getColor(R.color.speed_color));
            }

//            sensorViewHolder.accuracyProgressBar.setMax(100);
//            sensorViewHolder.accuracyProgressBar.setProgress(sensorValueDto.accuracy);
//            sensorViewHolder.accuracyValueView.setText(sensorValueDto.accuracy + "%");

//            if (sensorValueDto.accuracy < 30){
//                sensorViewHolder.accuracyProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_belowgoalbar));
//                sensorViewHolder.accuracyValueView.setTextColor(getResources().getColor(R.color.force_color));
//            }else if (sensorValueDto.accuracy < 60){
//                sensorViewHolder.accuracyProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_averagegoalbar));
//                sensorViewHolder.accuracyValueView.setTextColor(getResources().getColor(R.color.orange));
//            }else {
//                sensorViewHolder.accuracyProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_abovegoalbar));
//                sensorViewHolder.accuracyValueView.setTextColor(getResources().getColor(R.color.speed_color));
//            }

            sensorViewHolder.disconnectBtn.setText(getResources().getString(R.string.disconnect));

            sensorViewHolder.controlParentView.setVisibility(View.VISIBLE);
        }else {
            sensorViewHolder.statusView.setText(getResources().getString(R.string.disconnected));
            sensorViewHolder.statusView.setTextColor(getResources().getColor(R.color.force_color));
//            sensorViewHolder.controlParentView.setVisibility(View.GONE);
            sensorViewHolder.disconnectBtn.setText(getResources().getString(R.string.connect));
            sensorViewHolder.batteryProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_belowgoalbar));
//            sensorViewHolder.accuracyProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogress_belowgoalbar));
            sensorViewHolder.batterValueView.setTextColor(getResources().getColor(R.color.opacity_black));
//            sensorViewHolder.accuracyValueView.setTextColor(getResources().getColor(R.color.opacity_black));
            sensorViewHolder.batterValueView.setText(sensorValueDto.battery + "%");
//            sensorViewHolder.accuracyValueView.setText(sensorValueDto.accuracy + "%");

        }

    }

    private SensorValueDto generateSensor(){
        SensorValueDto sensorValueDto = new SensorValueDto();

        int connectint = CommonUtils.getRandomNum(5, 1);

        if (connectint <= 3)
            sensorValueDto.connected = true;
        else
            sensorValueDto.connected = false;

        if (sensorValueDto.connected) {
            sensorValueDto.battery = CommonUtils.getRandomNum(100, 10);
//            sensorValueDto.accuracy = CommonUtils.getRandomNum(100, 10);
        }else{
//            sensorValueDto.accuracy = 0;
            sensorValueDto.battery = 0;
        }

        return sensorValueDto;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public class SensorViewHolder{
        @BindView(R.id.sensor_hand) TextView handView;
        @BindView(R.id.connect_status) TextView statusView;
        @BindView(R.id.battery_progress) ProgressBar batteryProgressBar;
        @BindView(R.id.battery_value) TextView batterValueView;
//        @BindView(R.id.accuracy_progress) ProgressBar accuracyProgressBar;
//        @BindView(R.id.accracy_value) TextView accuracyValueView;
        @BindView(R.id.calibrate_btn)  Button calibrateBtn;
        @BindView(R.id.disconnect_btn) Button disconnectBtn;
        @BindView(R.id.control_parent) LinearLayout controlParentView;

        public SensorViewHolder (View view){
            ButterKnife.bind(this,view);
        }
    }

    public class SensorValueDto{
        public boolean connected;
        public int battery;
//        public int accuracy;
    }
}
