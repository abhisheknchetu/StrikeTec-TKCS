package com.striketec.mobile.activity.analysis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.activity.calendar.CalendarDialog;
import com.striketec.mobile.adapters.CompareSessionListAdapter;
import com.striketec.mobile.dto.ServerTrainingSessionDtoResponse;
import com.striketec.mobile.dto.TrainingRoundDto;
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

public class CompareSelectSessionsActivity extends BaseActivity implements CalendarDialog.DialogListener {

    private static final String TAG = CompareSelectSessionsActivity.class.getSimpleName();

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.second_title) TextView secondTitleView;
    @BindView(R.id.nextweek) ImageView nextweekArrow;

    @BindView(R.id.listview) ListView sessionListView;
    @BindView(R.id.day_duration) TextView durationView;

    ArrayList<TrainingSessionDto> sessionDtos = new ArrayList<>();

    CompareSessionListAdapter sessionListAdapter;

    String type;
    String utcstartDateString, utcendDateString;
    long startMilli, endMilli;
    Date startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_selectsession);

        type = getIntent().getStringExtra(AppConst.TRAININGTYPE);
        startMilli = getIntent().getLongExtra(AppConst.START_DATE, DatesUtil.getFirstDayofWeek());
        endMilli = getIntent().getLongExtra(AppConst.END_DATE, System.currentTimeMillis());
        utcstartDateString = DatesUtil.getUTCfromMilliseconds(startMilli);
        utcendDateString = DatesUtil.getUTCfromMilliseconds(endMilli);

        startDate = DatesUtil.getDateFromMilliseconds(startMilli);
        endDate = DatesUtil.getDateFromMilliseconds(endMilli);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){

        secondTitleView.setVisibility(View.VISIBLE);
        secondTitleView.setText(getResources().getString(R.string.cancel));

        String durationTxt = DatesUtil.getDuration(new Date(startMilli), new Date(endMilli));

        durationView.setText(durationTxt);

        if (type.equalsIgnoreCase(AppConst.TRAINING_TYPE_QUICKSTART)){
            titleView.setText(getResources().getString(R.string.quickstarttrainings));
        }else if (type.equalsIgnoreCase(AppConst.TRAINING_TYPE_ROUND)){
            titleView.setText(getResources().getString(R.string.roundtraininigs));
        }

        sessionListAdapter = new CompareSessionListAdapter(this, sessionDtos);
        sessionListView.setAdapter(sessionListAdapter);

        getSessionsWithType();

        updateNextArrow(startDate, endDate);
    }

    private void getSessionsWithType(){
        if (CommonUtils.isOnline()){
            Map<String, Object> queryMap = new HashedMap();

            queryMap.put("start_date", utcstartDateString);
            queryMap.put("end_date", utcendDateString);
            queryMap.put("training_type_id", type);

            RetrofitSingleton.TRAINING_DATA_REST.getSessions(SharedUtils.getHeader(), queryMap).enqueue(new IndicatorCallback<ServerTrainingSessionDtoResponse>(CompareSelectSessionsActivity.this) {
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

                            sessionListAdapter.notifyDataSetChanged();
                        }else {
                            if (AppConst.DEBUG)
                                Log.e(TAG, "error message = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerTrainingSessionDtoResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(CompareSelectSessionsActivity.this, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }


    @OnClick({R.id.back, R.id.second_title, R.id.nextweek, R.id.previousweek})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.second_title:
                finish();
                break;

            case R.id.nextweek:
                getNextWeek();
                break;

            case R.id.previousweek:
                getPreviousWeek();
                break;
        }
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(int position){

        TrainingSessionDto sessionDto = sessionListAdapter.getItem(position);

        Intent intent = new Intent();
        intent.putExtra(AppConst.LAST_SESSION, sessionDto);

        setResult(Activity.RESULT_OK, intent);
        finish();
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
        startDate = startDate1;
        endDate = endDate1;

        utcstartDateString = DatesUtil.getUTCfromMilliseconds(startDate1.getTime());
        utcendDateString = DatesUtil.getUTCfromMilliseconds(endDate1.getTime());

        durationView.setText(DatesUtil.getDuration(startDate1, endDate1));

        getSessionsWithType();

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
