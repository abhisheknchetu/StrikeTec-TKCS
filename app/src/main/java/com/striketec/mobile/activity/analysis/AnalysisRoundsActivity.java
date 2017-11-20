package com.striketec.mobile.activity.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;
import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.calendar.CalendarDialog;
import com.striketec.mobile.activity.leaderboard.CompareLeaderboardActivity;
import com.striketec.mobile.adapters.RoundListAdapter;
import com.striketec.mobile.customview.CompareLayout;
import com.striketec.mobile.dto.ServerTrainingRoundsDtoResponse;
import com.striketec.mobile.dto.TrainingRoundDto;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.dto.WorkoutDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.ComboSetUtil;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Response;

public class AnalysisRoundsActivity extends BaseActivity implements CalendarDialog.DialogListener {
    public static final String TAG = AnalysisRoundsActivity.class.getSimpleName();

    public static final int REQUEST_GET_ROUND = 4;

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.second_image) ImageView secondImageView;
    @BindView(R.id.selected) TextView lastLabel;
    @BindView(R.id.besttime_bar) CompareLayout bestTimeChart;
    @BindView(R.id.punches_bar) CompareLayout punchesChart;
    @BindView(R.id.speed_bar) CompareLayout speedChart;
    @BindView(R.id.force_bar) CompareLayout forceChart;
    @BindView(R.id.timevalue) TextView timevalueView;
    @BindView(R.id.punchcount) TextView punchcountView;
    @BindView(R.id.roundcount) TextView roundcountView;
    @BindView(R.id.listview) ListView roundlistView;
    @BindView(R.id.compare_bar)  LinearLayout compareParentView;
    @BindView(R.id.stickyscrollview)  StickyScrollView stickyScrollView;
    @BindView(R.id.divider) View dividerView;



    TrainingSessionDto sessionDto;
    ArrayList<TrainingRoundDto> trainingRoundDtos = new ArrayList<>();

    int planId = -1;
    WorkoutDto workoutDto = null;

    RoundListAdapter roundListAdapter;
    String trainingType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_rounds);

        sessionDto = (TrainingSessionDto)getIntent().getSerializableExtra(AppConst.SESSION_INTENT);
        trainingType = sessionDto.mTrainingType;
        planId = sessionDto.mPlanId;

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        if (trainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_ROUND)){
            titleView.setText(getResources().getString(R.string.training_result_round));
            dividerView.setVisibility(View.VISIBLE);
            compareParentView.setVisibility(View.VISIBLE);
        }else if (trainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_QUICKSTART)){
            titleView.setText(getResources().getString(R.string.training_result_quickstart));
            dividerView.setVisibility(View.VISIBLE);
            compareParentView.setVisibility(View.VISIBLE);
        }else if(trainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_WORKOUT)){
            titleView.setText(ComboSetUtil.getWorkoutDtoWithID(planId).getName());
            dividerView.setVisibility(View.GONE);
            compareParentView.setVisibility(View.GONE);

            workoutDto = ComboSetUtil.getWorkoutDtoWithID(planId);
        }

        timevalueView.setText(PresetUtil.changeSecondsToMinutes((int)((Long.parseLong(sessionDto.mEndTime) - Long.parseLong(sessionDto.mStartTime)) / 1000)));
        punchcountView.setText(String.valueOf(sessionDto.mPunchCount));
        roundcountView.setText(String.valueOf(sessionDto.mRoundIds.size()));

        if(!trainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_WORKOUT)){
            TrainingSessionDto lastSession = (TrainingSessionDto)getIntent().getSerializableExtra(AppConst.LAST_SESSION);
            lastLabel.setText(getResources().getString(R.string.last));
            updateCompareView(lastSession);
        }

        roundListAdapter = new RoundListAdapter(this, trainingRoundDtos);
        roundlistView.setAdapter(roundListAdapter);
        roundlistView.setFocusable(false);

        getRoundsofSession(sessionDto.mId);
    }

    private void updateCompareView(TrainingSessionDto lastSession){

        if (lastSession != null) {
            bestTimeChart.setData(lastSession.mBestTime, sessionDto.mBestTime, true, true, R.drawable.bg_bar_first_punch, R.drawable.bg_bar_last_punch, R.color.punches_text);
            punchesChart.setData(lastSession.mPunchCount, sessionDto.mPunchCount, false, false, R.drawable.bg_bar_first_orange, R.drawable.bg_bar_last_orange, R.color.orange);
            speedChart.setData((float)lastSession.mAvgSpeed, (float)sessionDto.mAvgSpeed, false, false, R.drawable.bg_bar_first_speed, R.drawable.bg_bar_last_speed, R.color.speed_color);
            forceChart.setData((float)lastSession.mAvgForce, (float)sessionDto.mAvgForce, false, false, R.drawable.bg_bar_first_force, R.drawable.bg_bar_last_force, R.color.force_color);
        }else {
            bestTimeChart.setData(0f, 0.24f, true, false, R.drawable.bg_bar_first_punch, R.drawable.bg_bar_last_punch, R.color.punches_text);
            punchesChart.setData(0f, 380f, false, false, R.drawable.bg_bar_first_orange, R.drawable.bg_bar_last_orange, R.color.orange);
            speedChart.setData(0f, 220f, false, false, R.drawable.bg_bar_first_speed, R.drawable.bg_bar_last_speed, R.color.speed_color);
            forceChart.setData(0f, 320f, false, false, R.drawable.bg_bar_first_force, R.drawable.bg_bar_last_force, R.color.force_color);
        }
    }

    @OnClick({R.id.back, R.id.compare, R.id.compareleaderboard})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.compare:
                String startDateString = DatesUtil.getDefaultTimefromMilliseconds(DatesUtil.getFirstDayofWeek());
                String endDateString = DatesUtil.getDefaultTimefromMilliseconds(System.currentTimeMillis());

                CalendarDialog fragment = CalendarDialog.newInstance(startDateString, endDateString, false);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null)
                    ft.remove(prev);

                ft.addToBackStack(null);

                fragment.show(ft, "dialog");
                break;

            case R.id.compareleaderboard:
                Intent compareIntent = new Intent(AnalysisRoundsActivity.this, CompareLeaderboardActivity.class);
                startActivity(compareIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(int position){

        TrainingRoundDto roundDto = roundListAdapter.getItem(position);
        Intent breakdownIntent = null;

        if (workoutDto == null){
            breakdownIntent = new Intent(this, AnalysisRoundBreakdownActivity.class);
            breakdownIntent.putExtra(AppConst.TRAININGTYPE, trainingType);
        }else {
            breakdownIntent = new Intent(this, AnalysisSetroutineActivity.class);
            breakdownIntent.putExtra(AppConst.WORKOUT_ID, workoutDto.getId());
            breakdownIntent.putExtra(AppConst.WORKOUT_ROUND_POSITION, position);
        }

        breakdownIntent.putExtra(AppConst.ROUND_INTENT, roundDto);

        startActivity(breakdownIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void getRoundsofSession(int sessionId){
        if (CommonUtils.isOnline()){

            RetrofitSingleton.TRAINING_DATA_REST.getRoundswithSessionId(SharedUtils.getHeader(), String.valueOf(sessionId)).enqueue(new IndicatorCallback<ServerTrainingRoundsDtoResponse>(AnalysisRoundsActivity.this) {
                @Override
                public void onResponse(Call<ServerTrainingRoundsDtoResponse> call, Response<ServerTrainingRoundsDtoResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        ServerTrainingRoundsDtoResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " + responseDto.mMessage);

                            trainingRoundDtos.clear();
                            trainingRoundDtos.addAll(responseDto.mRounds);

                            roundListAdapter.notifyDataSetChanged();
                        }else {
                            if (AppConst.DEBUG)
                                Log.e(TAG, "error message = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerTrainingRoundsDtoResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(AnalysisRoundsActivity.this, t.getLocalizedMessage());
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
    public void onDialogClosed() {

    }

    @Override
    public void onDateChanged(Date startDate, Date endDate) {
        Intent compareIntent = new Intent(AnalysisRoundsActivity.this, CompareSelectSessionsActivity.class);

        compareIntent.putExtra(AppConst.START_DATE, startDate.getTime());
        compareIntent.putExtra(AppConst.END_DATE, endDate.getTime());
        compareIntent.putExtra(AppConst.TRAININGTYPE, trainingType);

        startActivityForResult(compareIntent, REQUEST_GET_ROUND);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_GET_ROUND){
                TrainingSessionDto lastsessionDto = (TrainingSessionDto)data.getSerializableExtra(AppConst.LAST_SESSION);
                if (lastsessionDto != null) {
                    lastLabel.setText(DatesUtil.getDateFromMilliseconds(lastsessionDto.mStartTime));
                    updateCompareView(lastsessionDto);
                }
            }
        }
    }
}
