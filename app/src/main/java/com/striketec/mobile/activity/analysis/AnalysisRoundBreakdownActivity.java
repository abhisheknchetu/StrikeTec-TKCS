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

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.calendar.CalendarDialog;
import com.striketec.mobile.activity.leaderboard.CompareLeaderboardActivity;
import com.striketec.mobile.adapters.RoundPunchListAdapter;
import com.striketec.mobile.customview.CompareLayout;
import com.striketec.mobile.dto.ServerTrainingPunchesDtoResponse;
import com.striketec.mobile.dto.TrainingPunchDto;
import com.striketec.mobile.dto.TrainingRoundDto;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class AnalysisRoundBreakdownActivity extends BaseActivity implements CalendarDialog.DialogListener{

    private static final String TAG = AnalysisRoundBreakdownActivity.class.getSimpleName();
    public static final int REQUEST_GET_ROUND = 4;

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.compareonleaderboar_parent) LinearLayout compareonLeadboardParent;
    @BindView(R.id.compare_bar) LinearLayout comparebarView;
    @BindView(R.id.roundtimeview) LinearLayout roundtimeParentView;

    @BindView(R.id.besttime_bar) CompareLayout bestTimeChart;
    @BindView(R.id.punches_bar) CompareLayout punchesChart;
    @BindView(R.id.speed_bar) CompareLayout speedChart;
    @BindView(R.id.force_bar) CompareLayout forceChart;

    @BindView(R.id.selected) TextView lastLabel;

    @BindView(R.id.second_title) TextView tipsView;
    @BindView(R.id.roundtime) TextView roundTimeView;
    @BindView(R.id.sort_type) TextView sorttypeView;
    @BindView(R.id.sort_type_img) ImageView sorttypeImgView;
    @BindView(R.id.sort_duration) TextView sortdurationView;
    @BindView(R.id.sort_duration_img) ImageView sortdurationImgView;
    @BindView(R.id.sort_speed) TextView sortspeedView;
    @BindView(R.id.sort_speed_img) ImageView sortspeedImgView;
    @BindView(R.id.sort_power) TextView sortpowerView;
    @BindView(R.id.sort_power_img) ImageView sortpowerImgView;

    @BindView(R.id.listview) ListView punchListView;

    @BindView(R.id.timevalue) TextView besttimeView;
    @BindView(R.id.punchcount) TextView punchcountView;
    @BindView(R.id.maxspeed) TextView maxspeedView;
    @BindView(R.id.maxpower) TextView maxpowerView;

    private int currentSortPosition = 0;
    private boolean sorttypeDesc = true, sortdurationDesc = true, sortspeeddesc = true, sortpowerdesc = true;

    int roundId = 0;

    TrainingSessionDto sessionDto = null;
    TrainingRoundDto roundDto = null;
    ArrayList<TrainingPunchDto> punchDtos = new ArrayList<>();

    RoundPunchListAdapter punchListAdapter;

    boolean fromRound = true; // true : from session
    String trainingType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_round_breakdown);

        fromRound = getIntent().getBooleanExtra(AppConst.FROM_ROUND, true);

        if (fromRound){
            roundDto = (TrainingRoundDto) getIntent().getSerializableExtra(AppConst.ROUND_INTENT);
            roundId = roundDto.mId;
            trainingType = getIntent().getStringExtra(AppConst.TRAININGTYPE);
        }else {
            sessionDto = (TrainingSessionDto) getIntent().getSerializableExtra(AppConst.SESSION_INTENT);
            roundId = sessionDto.mRoundIds.get(0).mId;
            trainingType = sessionDto.mTrainingType;
        }

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){

        if (fromRound){
            titleView.setText(getResources().getString(R.string.roundbreakdown));
            comparebarView.setVisibility(View.GONE);
            compareonLeadboardParent.setVisibility(View.GONE);
            roundtimeParentView.setBackgroundColor(getResources().getColor(R.color.selectbg));
        }else {
            comparebarView.setVisibility(View.VISIBLE);
            compareonLeadboardParent.setVisibility(View.VISIBLE);
            roundtimeParentView.setBackgroundColor(getResources().getColor(R.color.transparent));
            if (trainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_QUICKSTART))
                titleView.setText(getResources().getString(R.string.quickstartresults));
            else
                titleView.setText(getResources().getString(R.string.roundresults));

            lastLabel.setText(getResources().getString(R.string.last));
            TrainingSessionDto lastSession = (TrainingSessionDto)getIntent().getSerializableExtra(AppConst.LAST_SESSION);
            updateCompareView(lastSession);
        }

        tipsView.setVisibility(View.VISIBLE);
        tipsView.setText(getResources().getString(R.string.tips));

        punchListAdapter = new RoundPunchListAdapter(this, punchDtos);
        punchListView.setAdapter(punchListAdapter);
        punchListView.setFocusable(false);

        if (fromRound){
            updateView();
        }else {
            String time = "0:00";
            roundTimeView.setText(String.format(getResources().getString(R.string.roundtime), time));
        }


        getPuncheswithRoundid(roundId);

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



    @OnClick({R.id.back, R.id.sort_type_parent, R.id.sort_duration_parent, R.id.sort_speed_parent, R.id.sort_power_parent,
            R.id.compare, R.id.second_title, R.id.compareleaderboard})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.sort_type_parent:
                if (currentSortPosition == 0){
                    sorttypeDesc= !sorttypeDesc;
                }else {
                    currentSortPosition = 0;
                }

                updateSortView();

                break;

            case R.id.sort_duration_parent:

                if (currentSortPosition == 1){
                    sortdurationDesc = !sortdurationDesc;
                }else {
                    currentSortPosition = 1;
                }

                updateSortView();
                break;

            case R.id.sort_speed_parent:

                if (currentSortPosition == 2){
                    sortspeeddesc = !sortspeeddesc;
                }else {
                    currentSortPosition = 2;
                }

                updateSortView();
                break;
            case R.id.sort_power_parent:

                if (currentSortPosition == 3){
                    sortpowerdesc = !sortpowerdesc;
                }else {
                    currentSortPosition = 3;
                }

                updateSortView();
                break;

            case R.id.compare:
                CalendarDialog fragment = CalendarDialog.newInstance(DatesUtil.getDefaultTimefromMilliseconds(DatesUtil.getFirstDayofWeek()),
                        DatesUtil.getDefaultTimefromMilliseconds(System.currentTimeMillis()), false);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null)
                    ft.remove(prev);

                ft.addToBackStack(null);

                fragment.show(ft, "dialog");
                break;

            case R.id.second_title:
                Intent tipsIntent = new Intent(AnalysisRoundBreakdownActivity.this, AnalysisTipsActivity.class);
                startActivity(tipsIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.compareleaderboard:
                Intent compareIntent = new Intent(AnalysisRoundBreakdownActivity.this, CompareLeaderboardActivity.class);
                startActivity(compareIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void updateSortView(){
        if (currentSortPosition == 0){

            if (sorttypeDesc){
                sorttypeImgView.setImageResource(R.drawable.sort_active_down);
                Collections.sort(punchDtos, TYPE_COMPARATOR);
            }else {
                sorttypeImgView.setImageResource(R.drawable.sort_active_up);
                Collections.sort(punchDtos, TYPE_COMPARATOR_DOWN);
            }

            sorttypeView.setTextColor(getResources().getColor(R.color.punches_text));
            sortdurationView.setTextColor(getResources().getColor(R.color.first_punch));
            sortspeedView.setTextColor(getResources().getColor(R.color.first_punch));
            sortpowerView.setTextColor(getResources().getColor(R.color.first_punch));

            sortdurationImgView.setImageResource(R.drawable.sort_deactive);
            sortspeedImgView.setImageResource(R.drawable.sort_deactive);
            sortpowerImgView.setImageResource(R.drawable.sort_deactive);
        }else if(currentSortPosition == 1){
            if (sortdurationDesc){
                sortdurationImgView.setImageResource(R.drawable.sort_active_down);
                Collections.sort(punchDtos, DURATION_COMPARATOR);
            }else {
                sortdurationImgView.setImageResource(R.drawable.sort_active_up);
                Collections.sort(punchDtos, DURATION_COMPARATOR_DOWN);
            }

            sortdurationView.setTextColor(getResources().getColor(R.color.punches_text));
            sorttypeView.setTextColor(getResources().getColor(R.color.first_punch));
            sortspeedView.setTextColor(getResources().getColor(R.color.first_punch));
            sortpowerView.setTextColor(getResources().getColor(R.color.first_punch));

            sorttypeImgView.setImageResource(R.drawable.sort_deactive);
            sortspeedImgView.setImageResource(R.drawable.sort_deactive);
            sortpowerImgView.setImageResource(R.drawable.sort_deactive);
        }else if (currentSortPosition == 2){
            if (sortspeeddesc){
                sortspeedImgView.setImageResource(R.drawable.sort_active_down);
                Collections.sort(punchDtos,SPEED_COMPARATOR);
            }else {
                sortspeedImgView.setImageResource(R.drawable.sort_active_up);
                Collections.sort(punchDtos,SPEED_COMPARATOR_DOWN);
            }

            sortspeedView.setTextColor(getResources().getColor(R.color.punches_text));
            sortdurationView.setTextColor(getResources().getColor(R.color.first_punch));
            sorttypeView.setTextColor(getResources().getColor(R.color.first_punch));
            sortpowerView.setTextColor(getResources().getColor(R.color.first_punch));

            sortdurationImgView.setImageResource(R.drawable.sort_deactive);
            sorttypeImgView.setImageResource(R.drawable.sort_deactive);
            sortpowerImgView.setImageResource(R.drawable.sort_deactive);
        }else {
            if (sortpowerdesc){
                sortpowerImgView.setImageResource(R.drawable.sort_active_down);
                Collections.sort(punchDtos,FORCE_COMPARATOR);
            }else {
                sortpowerImgView.setImageResource(R.drawable.sort_active_up);
                Collections.sort(punchDtos,FORCE_COMPARATOR_DOWN);
            }

            sortpowerView.setTextColor(getResources().getColor(R.color.punches_text));
            sortdurationView.setTextColor(getResources().getColor(R.color.first_punch));
            sortspeedView.setTextColor(getResources().getColor(R.color.first_punch));
            sorttypeView.setTextColor(getResources().getColor(R.color.first_punch));

            sortdurationImgView.setImageResource(R.drawable.sort_deactive);
            sortspeedImgView.setImageResource(R.drawable.sort_deactive);
            sorttypeImgView.setImageResource(R.drawable.sort_deactive);
        }

        punchListAdapter.setData(punchDtos);
        punchListAdapter.notifyDataSetChanged();
    }

    private void updateView(){
        besttimeView.setText(String.valueOf(roundDto.mBestTime));
        punchcountView.setText(String.valueOf(roundDto.mPunchCount));
        maxspeedView.setText(String.valueOf((int)roundDto.mMaxSpeed));
        maxpowerView.setText(String.valueOf((int)roundDto.mMaxForce));

        String time = (PresetUtil.changeSecondsToMinutes((int)((Long.parseLong(roundDto.mEndTime) - Long.parseLong(roundDto.mStartTime)) / 1000)));
        roundTimeView.setText(String.format(getResources().getString(R.string.roundtime), time));

        updateSortView();
    }

    private void getPuncheswithRoundid(int roundId){
        if (CommonUtils.isOnline()){

            RetrofitSingleton.TRAINING_DATA_REST.getPunchesWithRoundId(SharedUtils.getHeader(), String.valueOf(roundId)).enqueue(new IndicatorCallback<ServerTrainingPunchesDtoResponse>(AnalysisRoundBreakdownActivity.this) {
                @Override
                public void onResponse(Call<ServerTrainingPunchesDtoResponse> call, Response<ServerTrainingPunchesDtoResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        ServerTrainingPunchesDtoResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " + responseDto.mMessage);

                            roundDto = responseDto.mRound;
                            punchDtos.clear();
                            punchDtos.addAll(responseDto.mPunches);

                            updateView();
                        }else {
                            if (AppConst.DEBUG)
                                Log.e(TAG, "error message = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerTrainingPunchesDtoResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(AnalysisRoundBreakdownActivity.this, t.getLocalizedMessage());
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

    public Comparator<TrainingPunchDto> TYPE_COMPARATOR = new Comparator<TrainingPunchDto>() {
        @Override
        public int compare(TrainingPunchDto lhs, TrainingPunchDto rhs) {
              return CommonUtils.getTypefromvalue(rhs.mPunchType).compareToIgnoreCase(CommonUtils.getTypefromvalue(lhs.mPunchType));
        }
    };

    public Comparator<TrainingPunchDto> DURATION_COMPARATOR = new Comparator<TrainingPunchDto>() {
        @Override
        public int compare(TrainingPunchDto lhs, TrainingPunchDto rhs) {
            return (int) (rhs.mPunchDuration * 100 - lhs.mPunchDuration * 100);
        }
    };

    public Comparator<TrainingPunchDto> SPEED_COMPARATOR = new Comparator<TrainingPunchDto>() {
        @Override
        public int compare(TrainingPunchDto lhs, TrainingPunchDto rhs) {
            return (int) (rhs.mSpeed * 10 - lhs.mSpeed * 10);
        }
    };

    public Comparator<TrainingPunchDto> FORCE_COMPARATOR = new Comparator<TrainingPunchDto>() {
        @Override
        public int compare(TrainingPunchDto lhs, TrainingPunchDto rhs) {
            return (int) (rhs.mForce * 100 - lhs.mForce * 100);
        }
    };

    public Comparator<TrainingPunchDto> TYPE_COMPARATOR_DOWN = new Comparator<TrainingPunchDto>() {
        @Override
        public int compare(TrainingPunchDto lhs, TrainingPunchDto rhs) {
            return CommonUtils.getTypefromvalue(lhs.mPunchType).compareToIgnoreCase(CommonUtils.getTypefromvalue(rhs.mPunchType));
        }
    };

    public Comparator<TrainingPunchDto> DURATION_COMPARATOR_DOWN = new Comparator<TrainingPunchDto>() {
        @Override
        public int compare(TrainingPunchDto lhs, TrainingPunchDto rhs) {
            return (int) (lhs.mPunchDuration * 100 - rhs.mPunchDuration * 100);
        }
    };

    public Comparator<TrainingPunchDto> SPEED_COMPARATOR_DOWN = new Comparator<TrainingPunchDto>() {
        @Override
        public int compare(TrainingPunchDto lhs, TrainingPunchDto rhs) {
            return (int) (lhs.mSpeed * 10 - rhs.mSpeed * 10);
        }
    };

    public Comparator<TrainingPunchDto> FORCE_COMPARATOR_DOWN = new Comparator<TrainingPunchDto>() {
        @Override
        public int compare(TrainingPunchDto lhs, TrainingPunchDto rhs) {
            return (int) (lhs.mForce * 100 - rhs.mForce * 100);
        }
    };

    @Override
    public void onDialogClosed() {

    }

    @Override
    public void onDateChanged(Date startDate, Date endDate) {

        Intent compareIntent = new Intent(AnalysisRoundBreakdownActivity.this, CompareSelectSessionsActivity.class);

        compareIntent.putExtra(AppConst.START_DATE, startDate.getTime());
        compareIntent.putExtra(AppConst.END_DATE, endDate.getTime());
        compareIntent.putExtra(AppConst.TRAININGTYPE, trainingType);

//        startActivity(compareIntent);

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
