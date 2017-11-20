package com.striketec.mobile.activity.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.calendar.CalendarDialog;
import com.striketec.mobile.adapters.SessionListAdapter;
import com.striketec.mobile.dto.ServerTrainingSessionDtoResponse;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.SharedUtils;

import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Response;

public class AnalysisSessionsActivity extends BaseActivity implements CalendarDialog.DialogListener{

    private static String TAG = AnalysisSessionsActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.nextweek) ImageView nextweekArrow;
    @BindView(R.id.second_image)    ImageView secondImageView;
    @BindView(R.id.listview) ListView sessionListView;
    @BindView(R.id.day_duration) TextView dayDurationView;
    @BindView(R.id.session_counts)
    TextView sessioncountView;
    @BindView(R.id.punchcount_value) TextView punchcountView;
    @BindView(R.id.speed_value) TextView speedView;
    @BindView(R.id.force_value) TextView forceView;

    private SessionListAdapter listAdapter;

    ArrayList<TrainingSessionDto> sessionDtos = new ArrayList<>();

    long startMilli, endMilli;

    Date startDate, endDate;
    String startDateString, endDateString;
    String utcstartDateString, utcendDateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_sessions);

//        startDateString = getIntent().getStringExtra(AppConst.START_DATE);
//        endDateString = getIntent().getStringExtra(AppConst.END_DATE);
        startMilli = getIntent().getLongExtra(AppConst.START_DATE, DatesUtil.getFirstDayofWeek());
        endMilli = getIntent().getLongExtra(AppConst.END_DATE, System.currentTimeMillis());
        startDateString = DatesUtil.getDefaultTimefromMilliseconds(startMilli);
        endDateString = DatesUtil.getDefaultTimefromMilliseconds(endMilli);
        utcstartDateString = DatesUtil.getUTCfromMilliseconds(startMilli);
        utcendDateString = DatesUtil.getUTCfromMilliseconds(endMilli);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.analysis));
        secondImageView.setVisibility(View.VISIBLE);
        secondImageView.setImageResource(R.drawable.compare_icon_white);
        listAdapter = new SessionListAdapter(this, sessionDtos);
        sessionListView.setAdapter(listAdapter);

        startDate = DatesUtil.getDatefromString(startDateString);
        endDate = DatesUtil.getDatefromString(endDateString);

        dayDurationView.setText(DatesUtil.getDuration(startDate, endDate));

        resetSessioninfoView();

        loadSessions(utcstartDateString, utcendDateString);

        updateNextArrow(startDate, endDate);
    }

    @OnClick({R.id.back, R.id.second_image, R.id.previousweek, R.id.nextweek})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.second_image:
                CalendarDialog fragment = CalendarDialog.newInstance(startDateString, endDateString, false);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null)
                    ft.remove(prev);

                ft.addToBackStack(null);

                fragment.show(ft, "dialog");

                break;

            case R.id.previousweek:
                getPreviousWeek();
                break;

            case R.id.nextweek:
                getNextWeek();
                break;
        }
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(int position){
        TrainingSessionDto sessionDto = listAdapter.getItem(position);
        Intent roundresultIntent;
        if (sessionDto.mTrainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_COMBINATION)){
            roundresultIntent = new Intent(this, AnalysisCombinationActivity.class);
        }else if (sessionDto.mTrainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_SETS)){
            roundresultIntent = new Intent(this, AnalysisSetroutineActivity.class);
        }else {
            if (!sessionDto.mTrainingType.equalsIgnoreCase(AppConst.TRAINING_TYPE_WORKOUT)){
                if (sessionDto.mRoundIds.size() == 0)
                    return;

                if (sessionDto.mRoundIds.size() == 1){
                    roundresultIntent = new Intent(this, AnalysisRoundBreakdownActivity.class);
                    roundresultIntent.putExtra(AppConst.FROM_ROUND, false);
                }else {
                    roundresultIntent = new Intent(this, AnalysisRoundsActivity.class);
                }

                //get last session and send
                TrainingSessionDto lastSession = getlastSession(sessionDto);
                if (lastSession != null){
                    roundresultIntent.putExtra(AppConst.LAST_SESSION, lastSession);
                }
            }else {
                roundresultIntent = new Intent(this, AnalysisRoundsActivity.class);
            }

        }

        roundresultIntent.putExtra(AppConst.SESSION_INTENT, sessionDto);

        startActivity(roundresultIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private TrainingSessionDto getlastSession(TrainingSessionDto sessionDto){

        for (int i = 0; i < sessionDtos.size(); i++){
            TrainingSessionDto temp = sessionDtos.get(sessionDtos.size() - 1 - i);
            if (temp.mTrainingType.equalsIgnoreCase(sessionDto.mTrainingType) && !temp.mStartTime.equalsIgnoreCase(sessionDto.mStartTime))
                return temp;
        }

        return null;
    }

    private void resetSessioninfoView(){
        sessioncountView.setText("0");
        punchcountView.setText("0");
        speedView.setText("0");
        forceView.setText("0");
    }

    private void updateView(){
        int punchcount = 0;
        double sumavgforce = 0, sumavgspeed = 0;
        double avgforce = 0, avgspeed = 0;

        for (int i = 0; i < sessionDtos.size(); i++){
            punchcount += sessionDtos.get(i).mPunchCount;
            sumavgforce += sessionDtos.get(i).mAvgForce * sessionDtos.get(i).mPunchCount;
            sumavgspeed += sessionDtos.get(i).mAvgSpeed * sessionDtos.get(i).mPunchCount;
        }

        if (punchcount != 0){
            avgforce = sumavgforce / punchcount;
            avgspeed = sumavgspeed / punchcount;
        }

        sessioncountView.setText(String.valueOf(sessionDtos.size()));
        punchcountView.setText(String.valueOf(punchcount));
        speedView.setText(String.valueOf((int)avgspeed));
        forceView.setText(String.valueOf((int)avgforce));
    }

    private void loadSessions(final String startDate, final String endDate){

        if (CommonUtils.isOnline()){
            Map<String, Object> queryMap = new HashedMap();

            queryMap.put("start_date", startDate);
            queryMap.put("end_date", endDate);

            RetrofitSingleton.TRAINING_DATA_REST.getSessions(SharedUtils.getHeader(), queryMap).enqueue(new IndicatorCallback<ServerTrainingSessionDtoResponse>(AnalysisSessionsActivity.this) {
                @Override
                public void onResponse(Call<ServerTrainingSessionDtoResponse> call, Response<ServerTrainingSessionDtoResponse> response) {
                    super.onResponse(call, response);

                    if (response.body() != null){
                        ServerTrainingSessionDtoResponse responseDto = response.body();

                        if (!responseDto.mError){
                            if (AppConst.DEBUG)
                                Log.e(TAG, "response = " + responseDto.mMessage);

                            sessionDtos.clear();
                            sessionDtos.addAll(responseDto.mSessions);

                            updateView();
                            listAdapter.notifyDataSetChanged();
                        }else {
                            if (AppConst.DEBUG)
                                Log.e(TAG, "error message = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerTrainingSessionDtoResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(AnalysisSessionsActivity.this, t.getLocalizedMessage());
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
    public void onDateChanged(Date startDate1, Date endDate1) {

        //startdate1, enddate1 is based on current time zone, when load sessions , have to change utc time zone

        startDate = startDate1;
        endDate = endDate1;
        startDateString = DatesUtil.getDefaultTimefromMilliseconds(startDate1.getTime());
        endDateString = DatesUtil.getDefaultTimefromMilliseconds(endDate1.getTime());
        utcstartDateString = DatesUtil.getUTCfromMilliseconds(startDate1.getTime());
        utcendDateString = DatesUtil.getUTCfromMilliseconds(endDate1.getTime());

        dayDurationView.setText(DatesUtil.getDuration(startDate1, endDate1));

        resetSessioninfoView();
        loadSessions(utcstartDateString, utcendDateString);

        updateNextArrow(startDate, endDate);
    }

    private void getPreviousWeek(){
        ArrayList<Date> previous = DatesUtil.getPreviousWeek(startDate);

        onDateChanged(previous.get(0), previous.get(1));
    }

    private void getNextWeek(){
        ArrayList<Date> nextWeek = DatesUtil.getNextWeek(startDate);

        onDateChanged(nextWeek.get(0), nextWeek.get(1));
    }

    private void updateNextArrow(Date startDate1, Date endDate1){
        Calendar calendar = Calendar.getInstance();
        Date current = calendar.getTime();

        if (DatesUtil.getDefaultTimefromMilliseconds(calendar.getTimeInMillis()).equalsIgnoreCase(DatesUtil.getDefaultTimefromMilliseconds(startDate1.getTime())) ||
                DatesUtil.getDefaultTimefromMilliseconds(calendar.getTimeInMillis()).equalsIgnoreCase(DatesUtil.getDefaultTimefromMilliseconds(endDate1.getTime()))){
            nextweekArrow.setVisibility(View.INVISIBLE);
            return;
        }

        if (current.after(startDate1) && current.before(endDate1)){
            nextweekArrow.setVisibility(View.INVISIBLE);
            return;
        }

        nextweekArrow.setVisibility(View.VISIBLE);
    }
}
