package com.striketec.mobile.activity.tabs.training.roundtraining;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.tabs.training.quickstart.QuickstartSettingsActivity;
import com.striketec.mobile.activity.tabs.training.quickstart.TrainingRoundSettingsFragment;
import com.striketec.mobile.dto.PresetDto;
import com.striketec.mobile.util.AppConst;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoundtrainingSettingsActivity extends BaseActivity {

    public static final String TAG = RoundtrainingSettingsActivity.class.getSimpleName();

    @BindView(R.id.title)
    TextView titleView;

    private TrainingRoundSettingsFragment roundSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roundtraining_settings);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.roundtrainingsettings));

        roundSettingsFragment = TrainingRoundSettingsFragment.newInstance(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, roundSettingsFragment, AppConst.ROUND_SETTINGS);
        fragmentTransaction.commit();
    }

    @OnClick({R.id.starttraining, R.id.back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.starttraining:
                startTraining();
                break;

            case R.id.back:
                finish();
                break;
        }
    }

    private void startTraining(){
        if (roundSettingsFragment != null) {
            Intent roundIntent = new Intent(this, RoundTrainingActivity.class);
            PresetDto presetDto = roundSettingsFragment.getCurrentPreset();
            roundIntent.putExtra(AppConst.PRESET, presetDto);
            startActivity(roundIntent);
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
