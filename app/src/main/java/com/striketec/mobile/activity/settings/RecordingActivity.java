package com.striketec.mobile.activity.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.RecordingsListAdapter;
import com.striketec.mobile.dto.RecordingTypeDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * user : rakeshk2
 * date : 10/22/2017
 * description : This class is responsible to apply different functions on Recording like
 * record, play, delete and upload to server audios.
 **/
public class RecordingActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.recordingRecyclerView)
    RecyclerView recordingRecyclerView;
    private RecordingsListAdapter adapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        ButterKnife.bind(this);

        initViews();

        bindList();
    }

    /**
     * Show list of combos to select by user before start recording.
     */
    private void bindList() {
        ArrayList<RecordingTypeDto> recordingTypeDtoList = new ArrayList<>();
        String[] workoutTypes = getResources().getStringArray(R.array.comboTypes); /*{"Attack", "Crafty", "Left Overs", "Defensive", "Super Banger"};*/
        for (int itemCounter = 0; itemCounter < workoutTypes.length; itemCounter++) {
            RecordingTypeDto recordingList = new RecordingTypeDto();
            recordingList.setName(workoutTypes[itemCounter]);
            recordingList.setId(itemCounter);

            if (CommonUtils.searchFile(recordingList.getName()) != null) {
                recordingList.setFileExists(true);
            } else {
                recordingList.setFileExists(false);
            }

            recordingTypeDtoList.add(recordingList);
        }

        adapter = new RecordingsListAdapter(mContext, recordingTypeDtoList);
        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 1);
        recordingRecyclerView.setLayoutManager(mLayoutManager);
        recordingRecyclerView.setAdapter(adapter);
    }

    private void initViews() {
        mContext = RecordingActivity.this;
        titleView.setText(getResources().getString(R.string.recording));
    }

    @OnClick({R.id.back})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.back:
                    finish();
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConst.RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        CommonUtils.showToastMessage(getString(R.string.permission_granted));
                    } else {
                        CommonUtils.showToastMessage(getString(R.string.permission_denied));
                    }
                }
                break;
        }
    }

    /**
     * Check permissions
     */
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopPlayingAudio();
        }
    }

}
