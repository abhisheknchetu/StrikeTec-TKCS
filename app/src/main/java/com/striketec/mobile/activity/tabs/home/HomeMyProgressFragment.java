package com.striketec.mobile.activity.tabs.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.activity.analysis.AnalysisSessionsActivity;
import com.striketec.mobile.customview.GraphLinearLayout;
import com.striketec.mobile.dto.ServerTrainingSessionDtoResponse;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DatesUtil;
import com.striketec.mobile.util.SharedUtils;

import org.apache.commons.collections.map.HashedMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;


public class HomeMyProgressFragment extends Fragment {

    public static String TAG = HomeMyProgressFragment.class.getSimpleName();

    @BindView(R.id.graphview)
    GraphLinearLayout graphLinearLayout;
    @BindView(R.id.session_counts)
    TextView sessioncountView;
    @BindView(R.id.nextweek)
    ImageView nextweekArrow;
    @BindView(R.id.duration) TextView durationView;
    @BindView(R.id.punchcount_value) TextView punchcountView;
    @BindView(R.id.speed_value) TextView speedView;
    @BindView(R.id.force_value) TextView forceView;


    MainActivity mainActivity;

    public static HomeMyProgressFragment myProgressFragment;
    private static Context mContext;

    private ArrayList<TrainingSessionDto> sessionDtos = new ArrayList<>();

    private String utcstartDate, utcendDate;
    private Date startDate, endDate;

    public static HomeMyProgressFragment newInstance(Context context) {
        mContext = context;
        myProgressFragment = new HomeMyProgressFragment();
        return myProgressFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivity = (MainActivity)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_myprogress, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    @OnClick({R.id.analysis, R.id.nextweek, R.id.previousweek})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.analysis:
                gotoAnalysisActivity();
                break;

            case R.id.nextweek:
                getNextWeek();
                break;

            case R.id.previousweek:
                getPreviousWeek();
                break;
        }
    }

    private void initViews(){
        utcstartDate = DatesUtil.getUTCfromMilliseconds(DatesUtil.getFirstDayofWeek());
        utcendDate = DatesUtil.getUTCfromMilliseconds(System.currentTimeMillis());

        startDate = DatesUtil.getDateFromMilliseconds(DatesUtil.getFirstDayofWeek());
        endDate = DatesUtil.getDateFromMilliseconds(System.currentTimeMillis());

        updateNextArrow(startDate, endDate);

        loadSessions(utcstartDate, utcendDate, false);
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

    private void gotoAnalysisActivity(){
        Intent analysisIntent = new Intent(mainActivity, AnalysisSessionsActivity.class);
        analysisIntent.putExtra(AppConst.START_DATE, startDate.getTime());
        analysisIntent.putExtra(AppConst.END_DATE, endDate.getTime());
        startActivity(analysisIntent);
        mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void update(){
        utcstartDate = DatesUtil.getUTCfromMilliseconds(DatesUtil.getFirstDayofWeek());
        utcendDate = DatesUtil.getUTCfromMilliseconds(System.currentTimeMillis());
        startDate = DatesUtil.getDateFromMilliseconds(DatesUtil.getFirstDayofWeek());
        endDate = DatesUtil.getDateFromMilliseconds(System.currentTimeMillis());

        updateNextArrow(startDate, endDate);

        loadSessions(utcstartDate, utcendDate, false);
    }

    private void loadSessions(final String startDate, final String endDate, boolean showDialog){
        if (CommonUtils.isOnline()){
            Map<String, Object> queryMap = new HashedMap();

            queryMap.put("start_date", startDate);
            queryMap.put("end_date", endDate);

            RetrofitSingleton.TRAINING_DATA_REST.getSessions(SharedUtils.getHeader(), queryMap).enqueue(new IndicatorCallback<ServerTrainingSessionDtoResponse>(mainActivity, showDialog) {
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

                            graphLinearLayout.setData(sessionDtos);
                        }else {
                            if (AppConst.DEBUG)
                                Log.e(TAG, "error message = " + responseDto.mMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerTrainingSessionDtoResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showAlert(mainActivity, t.getLocalizedMessage());
                }
            });
        }else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void onDateChanged(Date startDate1, Date endDate1){
        startDate = startDate1;
        endDate = endDate1;

        utcstartDate = DatesUtil.getUTCfromMilliseconds(startDate1.getTime());
        utcendDate = DatesUtil.getUTCfromMilliseconds(endDate1.getTime());

        loadSessions(utcstartDate, utcendDate, false);

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
            durationView.setText(getResources().getString(R.string.thisweek));

            return;
        }

        if (current.after(startDate1) && current.before(endDate1)){
            nextweekArrow.setVisibility(View.INVISIBLE);
            durationView.setText(getResources().getString(R.string.thisweek));
            return;
        }

        durationView.setText(DatesUtil.getDuration(startDate1, endDate1));

        nextweekArrow.setVisibility(View.VISIBLE);
    }
}
