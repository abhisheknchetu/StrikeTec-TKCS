package com.striketec.mobile.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.settings.sensor.SensorActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        titleView.setText(getResources().getString(R.string.settings));
    }

    @OnClick({R.id.record_audio_layout, R.id.back, R.id.notification_layout, R.id.connectedaccount_layout, R.id.sensor_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            /**
            user : rakeshk2
            date : 11/1/2017
            description : Start Recording activity.
            **/
            case R.id.record_audio_layout:
                Intent audioIntent = new Intent(SettingsActivity.this, RecordingActivity.class);
                startActivity(audioIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.notification_layout:
                Intent notificaitonIntent = new Intent(SettingsActivity.this, SettingsNotificationsActivity.class);
                startActivity(notificaitonIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.sensor_layout:
                Intent sensorIntent = new Intent(SettingsActivity.this, SensorActivity.class);
                startActivity(sensorIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.connectedaccount_layout:
                Intent accountIntent = new Intent(SettingsActivity.this, ConnectedAccountsActivity.class);
                startActivity(accountIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
