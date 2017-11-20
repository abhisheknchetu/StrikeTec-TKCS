package com.striketec.mobile.activity.tabs;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.TrainingActivity;
import com.striketec.mobile.activity.tabs.challenge.ChallengeFragment;
import com.striketec.mobile.activity.tabs.guidance.GuidanceFragment;
import com.striketec.mobile.activity.tabs.home.HomeFragment;
import com.striketec.mobile.activity.tabs.more.MoreFragment;
import com.striketec.mobile.activity.tabs.training.TrainingChooseActivity;
import com.striketec.mobile.customview.CustomViewPager;
import com.striketec.mobile.dto.DBTrainingRoundDto;
import com.striketec.mobile.dto.DBTrainingSessionDto;
import com.striketec.mobile.dto.ServerTrainingSessionDtoResponse;
import com.striketec.mobile.dto.TrainingBatteryLayoutDTO;
import com.striketec.mobile.dto.TrainingBatteryVoltageDTO;
import com.striketec.mobile.dto.TrainingConnectStatusDTO;
import com.striketec.mobile.dto.DBTrainingPunchDto;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.dto.UploadDataResponse;
import com.striketec.mobile.dto.VideoDto;
import com.striketec.mobile.interfaces.UploadFinishCallback;
import com.striketec.mobile.interfaces.VideoCallback;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.services.TrainingDataUploadService;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.SharedUtils;

import org.apache.commons.collections.map.HashedMap;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class MainActivity extends TrainingActivity implements VideoCallback{

    public static final String TAG = MainActivity.class.getSimpleName();
    final int UPLOAD_PERIOD = 3 * 60 * 1000;
    int UPLOAD_LIMIT = 300;

    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) CustomViewPager viewPager;

    private MyPagerAdapter pagerAdapter;

    static MainActivity instance;
    public static MainActivity getInstance(){
        return instance;
    }

    UploadFinishCallback uploadCallback = null;

    public boolean isUploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setKeyboardAction(this.getWindow().getDecorView().getRootView());

        ButterKnife.bind(this);

        initViews();
        SharedUtils.saveIntValue(SharedUtils.USER_SERVER_ID, StriketecApp.currentUser.mId);
        instance = this;
    }

    @OnClick({R.id.main_trainingbtn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.main_trainingbtn:
                Intent chooseIntent =  new Intent(MainActivity.this, TrainingChooseActivity.class);
                startActivity(chooseIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    private void initViews(){

        if (!AppConst.SENSOR_DEBUG) {
            connectSensor(false);
//            connectSensor(false);
        }

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(pagerAdapter);

        if (mTabLayout != null) {
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab != null && tab.getCustomView() != null) {
                        ((ImageView)tab.getCustomView().findViewById(R.id.tabicon)).setImageResource(pagerAdapter.mTabsSelectedIcon[tab.getPosition()]);
                        ((TextView)tab.getCustomView().findViewById(R.id.tabtitle)).setTextColor(getResources().getColor(R.color.orange));
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    if (tab != null && tab.getCustomView() != null) {
                        ((ImageView)tab.getCustomView().findViewById(R.id.tabicon)).setImageResource(pagerAdapter.mTabsUnSelectedIcon[tab.getPosition()]);
                        ((TextView)tab.getCustomView().findViewById(R.id.tabtitle)).setTextColor(getResources().getColor(R.color.white));
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });

            mTabLayout.setupWithViewPager(viewPager);

            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(pagerAdapter.getTabView(i));
                }
            }

            ((ImageView)mTabLayout.getTabAt(0).getCustomView().findViewById(R.id.tabicon)).setImageResource(pagerAdapter.mTabsSelectedIcon[0]);
            ((TextView)mTabLayout.getTabAt(0).getCustomView().findViewById(R.id.tabtitle)).setTextColor(getResources().getColor(R.color.orange));

        }

        //update unfinished sessions, rounds
        dbAdapter.endAllTrainingRounds();
        dbAdapter.endAllTrainingSession();

        //start upload service
        Intent uploadService = new Intent(this, TrainingDataUploadService.class);
        startService(uploadService);
    }

    public void updateSensor(){
        if (!AppConst.SENSOR_DEBUG) {
            connectSensor(true);
        }
    }

    public void connectSensor(boolean isLeft){
        deviceLeft = StriketecApp.currentUser.mLeftHandSensor;//AppConst.LEFT_MAC_ADDRESS;
        deviceRight = StriketecApp.currentUser.mRightHandSensor;//AppConst.RIGHT_MAC_ADDRESS;

        if (TextUtils.isEmpty(deviceLeft))
            deviceLeft = "";

        if (TextUtils.isEmpty(deviceRight))
            deviceRight = "";

        if (TextUtils.isEmpty(deviceLeft) && TextUtils.isEmpty(deviceRight)){
            CommonUtils.showToastMessage(AppConst.DEVICE_ID_MUST_NOT_BE_BLANK_ERROR);
        }else if (!TextUtils.isEmpty(deviceLeft) && !TextUtils.isEmpty(deviceRight) && deviceLeft.equalsIgnoreCase(deviceRight)){
            CommonUtils.showToastMessage(AppConst.DEVICE_ID_MUST_NOT_BE_SAME);
        }else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, AppConst.REQUEST_ENABLE_BT);
            }

            if (AppConst.DEBUG)
                Log.e(TAG, "start connect sensor");

            if (isLeft){
                new ShowLoaderTask(this).execute("left");
            }else {
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

        String punchType, hand, type;
        try {
            punchType = punchDataJson.getString("punchType");
            String speed = punchDataJson.getString("speed");
            String force = punchDataJson.getString("force");

            hand = punchType.charAt(0)+AppConst.BLANK_TEXT;

            if (punchType.equalsIgnoreCase(AppConst.LEFT_UNRECOGNIZED) || punchType.equalsIgnoreCase(AppConst.RIGHT_UNRECOGNIZED)){
                type = AppConst.UNRECOGNIZED_ABBREVIATION_TEXT;
            }else {
                type = punchType.charAt(1) + AppConst.BLANK_TEXT;
            }
            DBTrainingPunchDto punchDto = new DBTrainingPunchDto(trainingRoundStartTime, String.valueOf(System.currentTimeMillis()), type, hand, 0.5f, Integer.parseInt(force), Integer.parseInt(speed));
            savePunchInfotoDB(punchDto);
            EventBus.getDefault().post(punchDto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sensorTest(){
        String hand, type;
        int force, speed;

        int handInt = CommonUtils.getRandomNum(5, 1);

        if (handInt < 3)
            hand = "L";
        else
            hand = "R";

        int typeInt = CommonUtils.getRandomNum(10, 1);

        if (typeInt < 3)
            type = AppConst.JAB_ABBREVIATION_TEXT;
        else if (typeInt < 6){
            type = AppConst.HOOK_ABBREVIATION_TEXT;
        }else if (typeInt == 6){
            type = AppConst.STRAIGHT_ABBREVIATION_TEXT;
        }else if (typeInt < 9){
            type = AppConst.UPPERCUT_ABBREVIATION_TEXT;
        }else
            type = AppConst.UNRECOGNIZED_ABBREVIATION_TEXT;

        force = CommonUtils.getRandomNum(600, 200);
        speed = CommonUtils.getRandomNum(35, 10);

        DBTrainingPunchDto punchDto = new DBTrainingPunchDto(trainingRoundStartTime, String.valueOf(System.currentTimeMillis()), type, hand, 0.5f, force, speed);
        savePunchInfotoDB(punchDto);
        EventBus.getDefault().post(punchDto);
    }

    private void savePunchInfotoDB(DBTrainingPunchDto punchDto){
        dbAdapter.insertTrainingPunch(StriketecApp.currentUser.mId, punchDto);
    }

    @Override
    public void handlingConnectionToastResponse(Response response, Message msg) {
        try {
            switch (response) {
                case success:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(AppConst.TOAST), Toast.LENGTH_SHORT).show();
                    deviceConnectionSuccess(msg);
                    break;

                case unsuccess:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(AppConst.TOAST), Toast.LENGTH_SHORT).show();
                    if (msg.getData().getString("DeviceAddress").equals(deviceRight)) {

                        EventBus.getDefault().post(new TrainingBatteryLayoutDTO(false, true));

                        isDeviceRightConnectionFinish = true;
                        checkDeviceConnectionFlag++;
                        rightHandConnectionThread = null;
                        rightSensorConnected = false;

                        initiateTraining();
                    } else if (msg.getData().getString("DeviceAddress").equals(deviceLeft)) {

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

    public void startUplaod(boolean issession){
        isUploading = true;
        startuploadRounds(issession);
    }

    int count = 0;

    private void startuploadSessions(){
        Gson gsonTrainingSession = new GsonBuilder().create();
        ArrayList<DBTrainingSessionDto> dbTrainingSessionDtos = StriketecApp.dbAdapter.getAllNonSynchronizedTrainingSessionRecords(UPLOAD_LIMIT, SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1));

        if (dbTrainingSessionDtos.size() == 0){
            if (count == 0)
                uploadCallback.getSessionsError();
            else
                syncFinished();
        }else {
            count++;

            HashMap<String, ArrayList<DBTrainingSessionDto>> hashMap = new HashMap<>();
            hashMap.put("data", dbTrainingSessionDtos);
            String jsonsession = gsonTrainingSession.toJson(hashMap);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), jsonsession);

            if (AppConst.DEBUG)
                Log.e(TAG, "session json string = " + jsonsession);

            RetrofitSingleton.TRAINING_DATA_REST.uploadSessions("application/json", SharedUtils.getHeader(), body).enqueue(new IndicatorCallback<UploadDataResponse>(getApplicationContext(), false) {
                @Override
                public void onResponse(Call<UploadDataResponse> call, retrofit2.Response<UploadDataResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        UploadDataResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage + "    " + responseDto.mData.size());

                            //update training session table
                            for (int i = 0; i < responseDto.mData.size(); i++){
                                StriketecApp.dbAdapter.syncedTrainingSession(SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1), responseDto.mData.get(i).mStartTime);
                            }

                            startuploadSessions();

                        }else {
                            Log.e(TAG, "respone = " + responseDto.mMessage);
                            syncFinished();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadDataResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    syncFinished();
                }
            });
        }
    }

    private void startuploadRounds(final boolean isSession){
        Gson gsonTrainingRound = new GsonBuilder().create();
        ArrayList<DBTrainingRoundDto> dbTrainingRoundDtos = StriketecApp.dbAdapter.getAllNonSynchronizedTrainingRoundRecords(UPLOAD_LIMIT, SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1));

        if (dbTrainingRoundDtos.size() == 0){
            startuploadPunches(isSession);
        }else {
            HashMap<String, ArrayList<DBTrainingRoundDto>> hashMap = new HashMap<>();
            hashMap.put("data", dbTrainingRoundDtos);
            String jsonround = gsonTrainingRound.toJson(hashMap);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), jsonround);

            if (AppConst.DEBUG)
                Log.e(TAG, "round json string = " + jsonround);

            RetrofitSingleton.TRAINING_DATA_REST.uploadRounds( "application/json", SharedUtils.getHeader(), body).enqueue(new IndicatorCallback<UploadDataResponse>(getApplicationContext(), false) {
                @Override
                public void onResponse(Call<UploadDataResponse> call, retrofit2.Response<UploadDataResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        UploadDataResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage + responseDto.mData.size());

                            //update training round table
                            for (int i = 0; i < responseDto.mData.size(); i++){
                                StriketecApp.dbAdapter.syncedTrainingRound(SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1), responseDto.mData.get(i).mStartTime);
                            }

                            startuploadRounds(isSession);
                        }else {
                            if (isSession)
                                syncFinished();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadDataResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    if (isSession)
                        syncFinished();
                }
            });
        }
    }

    private void startuploadPunches(final boolean isSession){
        Gson gsonTrainingPunch = new GsonBuilder().create();
        ArrayList<DBTrainingPunchDto> dbTrainingPunches = StriketecApp.dbAdapter.getAllNonSynchronizedTrainingPunchRecords(UPLOAD_LIMIT, SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1));

        if (dbTrainingPunches.size() == 0){
            //upload is finished
            if (isSession){
                count = 0;
                startuploadSessions();
            }else {
                isUploading = false;
            }

        }else {
            HashMap<String, ArrayList<DBTrainingPunchDto>> hashMap = new HashMap<>();
            hashMap.put("data", dbTrainingPunches);
            String jsonpunch = gsonTrainingPunch.toJson(hashMap);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), jsonpunch);

            if (AppConst.DEBUG)
                Log.e(TAG, "punch json string = " + jsonpunch);

            RetrofitSingleton.TRAINING_DATA_REST.uploadPunches("application/json",  SharedUtils.getHeader(), body).enqueue(new IndicatorCallback<UploadDataResponse>(getApplicationContext(), false) {
                @Override
                public void onResponse(Call<UploadDataResponse> call, retrofit2.Response<UploadDataResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        UploadDataResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " +responseDto.mMessage +  responseDto.mData.size());

                            //update training punch table
                            for (int i = 0; i < responseDto.mData.size(); i++){
                                StriketecApp.dbAdapter.syncedTrainingPunch(SharedUtils.getIntvalue(SharedUtils.USER_SERVER_ID, -1), responseDto.mData.get(i).mStartTime);
                            }

                            startuploadPunches(isSession);

                        }else {
                            Log.e(TAG, "respone = " + responseDto.mMessage);
                            if (isSession)
                                syncFinished();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadDataResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    if (isSession)
                        syncFinished();
                }
            });
        }
    }

    private void syncFinished (){
        isUploading = false;

        if (uploadCallback != null) {
            uploadCallback.syncFinished();

            if (HomeFragment.homeFragment != null){
                HomeFragment.homeFragment.update();
            }
        }
    }

    public void getSessionsWithType(String trainingType, boolean isweek){
        if (CommonUtils.isOnline()){
            final ArrayList<TrainingSessionDto> sessionDtos = new ArrayList<>();

            Map<String, Object> queryMap = new HashedMap();

            String utcendDateString = DatesUtil.getUTCfromMilliseconds(System.currentTimeMillis());
            String utcstartDateString = isweek?DatesUtil.getUTCfromMilliseconds(DatesUtil.getFirstDayofWeek()):utcendDateString;

            queryMap.put("start_date", utcstartDateString);
            queryMap.put("end_date", utcendDateString);
            queryMap.put("training_type_id", trainingType);

            RetrofitSingleton.TRAINING_DATA_REST.getSessions(SharedUtils.getHeader(), queryMap).enqueue(new IndicatorCallback<ServerTrainingSessionDtoResponse>(this, false) {
                @Override
                public void onResponse(Call<ServerTrainingSessionDtoResponse> call, retrofit2.Response<ServerTrainingSessionDtoResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        ServerTrainingSessionDtoResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " + responseDto.mMessage);

                            sessionDtos.clear();
                            sessionDtos.addAll(responseDto.mSessions);

                            uploadCallback.getSessionsWithType(sessionDtos);

                        }else {
                            if (AppConst.DEBUG)
                                Log.e(TAG, "error message = " + responseDto.mMessage);

                            uploadCallback.getSessionsError();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerTrainingSessionDtoResponse> call, Throwable t) {
                    super.onFailure(call, t);

                    uploadCallback.getSessionsError();
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectSensors();
    }

    @Override
    public void viewCountChanged(VideoDto videoDto) {

        if (GuidanceFragment.guidanceFragment != null){
            GuidanceFragment.guidanceFragment.updateViewCount(videoDto);
        }
    }

    @Override
    public void favoriteChanged(VideoDto videoDto) {
        if (GuidanceFragment.guidanceFragment != null){
            GuidanceFragment.guidanceFragment.updateFavoriteStatus(videoDto);
        }
    }

    public class TabViewHolder {
        @BindView(R.id.tabtitle) TextView titleView;
        @BindView(R.id.tabicon) ImageView iconView;
        @BindView(R.id.parent) LinearLayout parentView;

        TabViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 4;

        private String mTabsTitle[] = {
                getResources().getString(R.string.tabhome),
                getResources().getString(R.string.tabguidance),
//                getResources().getString(R.string.tabtraining),
                getResources().getString(R.string.tabchallenges),
                getResources().getString(R.string.tabmore)
        };

        private int mTabsUnSelectedIcon[] = {R.drawable.tab_home, R.drawable.tab_guidance,/* R.drawable.tab_training,*/ R.drawable.tab_challenges,  R.drawable.tab_more};
        private int mTabsSelectedIcon[] = {R.drawable.tab_homeselected, R.drawable.tab_guidanceselected, /*R.drawable.tab_trainingselected,*/
                R.drawable.tab_challengeselected,  R.drawable.tab_moreselected};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_tab, null);
            TabViewHolder tabViewHolder = new TabViewHolder(view);

            WindowManager wm = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();

            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
            {
                display.getSize(size);
            }
            else
            {
                size.set(display.getWidth(), display.getHeight());
            }

            tabViewHolder.titleView.setText(mTabsTitle[position]);
            tabViewHolder.iconView.setImageResource(mTabsUnSelectedIcon[position]);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)tabViewHolder.parentView.getLayoutParams();
            params.width = size.x / 5;

            if (position == 2){
                params.setMargins(size.x/ 5, 0, 0, 0);
            }else {
                params.setMargins(0, 0, 0, 0);
            }

            tabViewHolder.parentView.setLayoutParams(params);

            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return HomeFragment.newInstance(MainActivity.this);
                case 1:
                    return GuidanceFragment.newInstance(MainActivity.this);
                case 2:
                    return ChallengeFragment.newInstance(MainActivity.this);
                case 3:
                    return MoreFragment.newInstance(MainActivity.this);
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }

    public void setUploadCallback(UploadFinishCallback uploadCallback){
        this.uploadCallback = uploadCallback;
    }

    public void removeUploadCallback (){
        this.uploadCallback = null;
    }


}
